package org.automation.controllers;

import org.automation.database.DatabaseManager;
import org.automation.entities.Sensor;
import org.automation.repositories.SensorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * SensorManager - Central controller for factory sensors
 * - Add/remove sensors
 * - Start/stop sensors
 * - Bulk operations
 * - Toggle automatic/control modes per sensor and in bulk
 * - Graceful shutdown
 */
public class SensorManager {

    private final Map<Integer, Sensor> sensors = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final DatabaseManager dbManager;
    private final SensorRepository sensorRepo;

    public SensorManager() {
        System.out.println("SensorManager initialized");
        // initialize database and repository
        this.dbManager = new DatabaseManager();
        SensorRepository repo = null;
        try {
            if (this.dbManager.connect()) {
                repo = new SensorRepository(this.dbManager);
                repo.ensureTable();
                // load persisted sensors
                List<Sensor> persisted = repo.findAll();
                for (Sensor s : persisted) {
                    sensors.putIfAbsent(s.getSensorId(), s);
                    System.out.println("Loaded sensor from DB: " + s.getSensorInfo());
                }
            }
        } catch (Exception e) {
            System.err.println("Database init failed: " + e.getMessage());
        }
        this.sensorRepo = repo;
    }

    // -------------------------
    // Basic sensor management
    // -------------------------
    public void addSensor(Sensor sensor) {
        if (sensor == null) return;
        sensors.putIfAbsent(sensor.getSensorId(), sensor);
        System.out.println("Added sensor: " + sensor.getSensorInfo());
        if (sensorRepo != null) {
            try {
                sensorRepo.save(sensor);
            } catch (Exception e) {
                System.err.println("Failed to persist sensor: " + e.getMessage());
            }
        }
    }

    public boolean removeSensor(int sensorId) {
        Sensor removed = sensors.remove(sensorId);
        if (removed != null) {
            System.out.println("Removed sensor: " + removed.getSensorInfo());
            if (sensorRepo != null) {
                try {
                    sensorRepo.deleteById(sensorId);
                } catch (Exception e) {
                    System.err.println("Failed to remove sensor from DB: " + e.getMessage());
                }
            }
            return true;
        } else {
            System.err.println("Sensor ID " + sensorId + " not found");
            return false;
        }
    }

    public Sensor findSensorById(int sensorId) {
        return sensors.get(sensorId);
    }

    // -------------------------
    // Start / Stop a specific sensor
    // -------------------------
    public boolean startSensor(int sensorId) {
        Sensor s = findSensorById(sensorId);
        if (s == null) return false;
        if (s.isActive()) return false;
        try {
            s.start();
            return true;
        } catch (Exception e) {
            System.err.println("Failed to start sensor " + sensorId + ": " + e.getMessage());
            return false;
        }
    }

    public boolean stopSensor(int sensorId) {
        Sensor s = findSensorById(sensorId);
        if (s == null) return false;
        if (!s.isActive()) return false;
        try {
            s.stop();
            return true;
        } catch (Exception e) {
            System.err.println("Failed to stop sensor " + sensorId + ": " + e.getMessage());
            return false;
        }
    }

    // -------------------------
    // Bulk operations & queries
    // -------------------------
    public void startAll() {
        new ArrayList<>(sensors.values()).forEach(s -> { if (!s.isActive()) s.start(); });
    }

    public void stopAll() {
        new ArrayList<>(sensors.values()).forEach(s -> { if (s.isActive()) s.stop(); });
    }

    public List<String> listSensorInfo() {
        List<String> info = new ArrayList<>();
        for (Sensor s : sensors.values()) info.add(s.getSensorInfo());
        return info;
    }

    public String getSensorInfo(int sensorId) {
        Sensor s = findSensorById(sensorId);
        return s == null ? null : s.getSensorInfo();
    }

    // -------------------------
    // Control / automatic toggles
    // -------------------------
    public boolean setSensorAutomaticMode(int sensorId, boolean enabled) {
        Sensor s = findSensorById(sensorId);
        if (s == null) return false;
        if (enabled) s.enableAutomaticMode(); else s.disableAutomaticMode();
        return true;
    }

    public boolean setSensorControl(int sensorId, boolean enabled, double target, double tolerance) {
        Sensor s = findSensorById(sensorId);
        if (s == null) return false;
        if (enabled) s.enableControl(target, tolerance); else s.disableControl();
        return true;
    }

    public boolean setAllAutomaticMode(boolean enabled) {
        new ArrayList<>(sensors.values()).forEach(s -> { if (enabled) s.enableAutomaticMode(); else s.disableAutomaticMode(); });
        return true;
    }

    public boolean setAllControl(boolean enabled, double target, double tolerance) {
        new ArrayList<>(sensors.values()).forEach(s -> { if (enabled) s.enableControl(target, tolerance); else s.disableControl(); });
        return true;
    }

    // -------------------------
    // Async helpers
    // -------------------------
    public void startSensorAsync(int sensorId) {
        executor.submit(() -> startSensor(sensorId));
    }

    public void stopSensorAsync(int sensorId) {
        executor.submit(() -> stopSensor(sensorId));
    }

    // -------------------------
    // Graceful shutdown
    // -------------------------
    public void shutdown(long timeoutSeconds) {
        // stop sensors first
        stopAll();
        // shutdown executor
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        try {
            // shutdown the simulation clock to avoid lingering threads
            try { org.automation.engine.SimulationClock.getInstance().shutdown(); } catch (Exception ignored) {}
            if (dbManager != null) dbManager.disconnect();
        } catch (Exception ignored) {}
        System.out.println("SensorManager shutdown complete");
    }
}
