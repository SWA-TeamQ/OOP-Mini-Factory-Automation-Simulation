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
            System.err.println("Database init failed:");
            e.printStackTrace();

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

    public List<String> listSensorInfo() {
        List<String> info = new ArrayList<>();
        for (Sensor s : sensors.values()) info.add(s.getSensorInfo());
        return info;
    }

    public String getSensorInfo(int sensorId) {
        Sensor s = findSensorById(sensorId);
        return s == null ? null : s.getSensorInfo();
    }

    public void removeAllSensors() {
        // Clear sensors from the database, then clear in-memory map
        if (sensorRepo != null) {
            try {
                // delete all rows from Sensor table
                sensorRepo.delete(null, null);
            } catch (Exception e) {
                System.err.println("Failed to clear sensors from DB: " + e.getMessage());
            }
        }
        sensors.clear();
    }

    public void updateSensors() {
        if (sensorRepo == null) return;
        for (Sensor s : sensors.values()) {
            try {
                sensorRepo.save(s);
            } catch (Exception e) {
                System.err.println("Failed to persist sensor (ID=" + s.getSensorId() + "): " + e.getMessage());
            }
        }
    }

    // -------------------------
    // Graceful shutdown
    // -------------------------
    public void shutdown(long timeoutSeconds) {
        // stop sensors first
        
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
