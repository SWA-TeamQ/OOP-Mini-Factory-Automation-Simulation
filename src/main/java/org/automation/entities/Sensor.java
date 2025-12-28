package org.automation.entities;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract Sensor base class.
 * - Centralizes lifecycle bookkeeping and status management
 * - Final template method for interval -> increments mapping
 * - Provides lightweight automatic/control flags and APIs used by concrete sensors
 *
 * Thread-safety:
 * - start()/stop() are synchronized to protect lifecycle transitions.
 * - primaryIncrement and coolingRate are volatile to ensure visibility across threads.
 */
public abstract class Sensor {
    private static final AtomicInteger ID_GEN = new AtomicInteger(1);

    // identity / metadata
    protected final int sensorId;
    protected final String sensorType;
    protected String location;
    protected String status;

    // lifecycle
    protected volatile boolean isActive;
    protected LocalDateTime startTime;
    protected LocalDateTime stopTime;

    // shared, clock-driven increments
    protected volatile double primaryIncrement;
    protected volatile double coolingRate;

    // automation flag (default: automatic enabled)
    protected volatile boolean automaticMode = true;

    protected Sensor(String sensorType, String location, String status) {
        this.sensorId = ID_GEN.getAndIncrement();
        this.sensorType = sensorType;
        this.location = location;
        this.status = status;
        this.isActive = false;
        this.primaryIncrement = getBaselineIncrement();
        this.coolingRate = getBaselineCooling();
    }

    /**
     * Construct sensor with a forced id (used when loading from persistent storage).
     * Ensures the global ID generator is advanced past the forced id to avoid collisions.
     */
    protected Sensor(int forcedId, String sensorType, String location, String status) {
        this.sensorId = forcedId;
        this.sensorType = sensorType;
        this.location = location;
        this.status = status;
        this.isActive = false;
        this.primaryIncrement = getBaselineIncrement();
        this.coolingRate = getBaselineCooling();
        // advance ID generator to avoid future collisions
        int next = forcedId + 1;
        while (true) {
            int cur = ID_GEN.get();
            if (cur >= next) break;
            if (ID_GEN.compareAndSet(cur, next)) break;
        }
    }

    // -----------------------
    // Lifecycle (centralized)
    // -----------------------
    protected void activateSensor() { isActive = true; setStatus("Active"); }
    protected void deactivateSensor() { isActive = false; setStatus("Paused"); }

    public boolean isActive() { return isActive; }
    public int getSensorId() { return sensorId; }
    public String getSensorType() { return sensorType; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }

    // -----------------------
    // Status helpers
    // -----------------------
    protected synchronized void setStatus(String newStatus) {
        this.status = newStatus;
    }

    /**
     * Update status after a read. Keeps statuses consistent across sensors.
     * @param valid whether the last reading validated successfully
     */
    protected synchronized void updateStatusAfterRead(boolean valid) {
        if (!isActive) setStatus("Stopped");
        else if (!valid) setStatus("Error");
        else setStatus("OK");
    }
    // -----------------------
    // Interval -> increment mapping (single place)
    // -----------------------
    /**
     * Final template method mapping clock interval -> increments.
     */
    public final void setSimulationInterval(int intervalMs) {
        double baseline = getBaselineIncrement();
        double baselineCooling = getBaselineCooling();

        if (intervalMs <= 1000) {
            primaryIncrement = baseline;
            coolingRate = baselineCooling;
        } else if (intervalMs <= 2000) {
            primaryIncrement = baseline * 2.0;
            coolingRate = baselineCooling * 2.0;
        } else {
            primaryIncrement = baseline * 4.0;
            coolingRate = baselineCooling * 4.0;
        }

        onIntervalUpdated(intervalMs);
    }

    // Subclass hooks
    protected abstract double getBaselineIncrement();
    protected double getBaselineCooling() { return 0.5; }
    protected void onIntervalUpdated(int intervalMs) { /* optional override */ }

    // -----------------------
    // Shared helpers
    // -----------------------
    protected void setCurrentValue(Object value) { /* telemetry hook */ }

    protected void raiseAlert(String title, String details) {
        setStatus("Error");
        System.err.println("ALERT [" + sensorId + "] " + title + " - " + details);
    }

    // -----------------------
    // Abstract contract
    // -----------------------
    public abstract Object getValue();
    public abstract void readValue();
    public abstract void updateValue(double change);
    public abstract void calibrateSensor();
    public abstract boolean validateReading();
    public abstract void sendAlert(double currentValue);
    public abstract String getSensorInfo();
    public abstract void performCycle();

    // helper to manually adjust next id (rarely needed)
    public static void setNextId(int next) {
        if (next <= 0) return;
        ID_GEN.set(next);
    }
}
