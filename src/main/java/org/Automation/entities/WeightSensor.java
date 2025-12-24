package org.automation.entities;

import org.automation.engine.SimulationClock;
import org.automation.engine.ClockObserver;

import java.time.LocalDateTime;

/**
 * WeightSensor
 * - Calibration offset is applied only when reporting/validating (Option 1)
 * - Internal simulated value (currentWeight) remains the raw truth
 * - Uses Sensor.primaryIncrement (clock-driven) for automatic per-tick weight changes
 * - Uses Sensor's automaticMode and controlEnabled flags; provides convenience wrappers
 */
public class WeightSensor extends Sensor implements ClockObserver {

    private double currentWeight;
    private final String weightUnit;
    private double machineCapacity;
    private boolean weightControlEnabled;
    private double lastWeight;

    private double calibrationOffset;
    private boolean calibrated = false;

    private volatile LocalDateTime lastActionTime;
    private SimulationClock simulationClock;

    public WeightSensor(String sensorType, String location, String status,
                        double initialWeight, double capacity, String weightUnit) {
        super(sensorType, location, status);
        this.currentWeight = initialWeight;
        this.lastWeight = initialWeight;
        this.machineCapacity = capacity;
        this.weightUnit = weightUnit;
        this.weightControlEnabled = true;
        this.simulationClock = SimulationClock.getInstance();
        // initialize base control fields
        this.controlTarget = initialWeight;
        this.controlTolerance = 0.0;
    }

    /**
     * Construct a WeightSensor with an explicit sensor id (used when loading from DB).
     */
    public WeightSensor(int sensorId, String sensorType, String location, String status,
                        double initialWeight, double capacity, String weightUnit) {
        super(sensorId, sensorType, location, status);
        this.currentWeight = initialWeight;
        this.lastWeight = initialWeight;
        this.machineCapacity = capacity;
        this.weightUnit = weightUnit;
        this.weightControlEnabled = true;
        this.simulationClock = SimulationClock.getInstance();
        this.controlTarget = initialWeight;
        this.controlTolerance = 0.0;
    }

    @Override
    protected double getBaselineIncrement() { return 1.0; }

    @Override
    protected double getBaselineCooling() { return 0.5; }

    @Override
    protected void onStart() {
        simulationClock.register(this);
        // initialize increments based on current clock interval so first cycle is aligned
        try { setSimulationInterval(simulationClock.getTickIntervalMs()); } catch (Exception ignored) {}
        calibrateSensor();
        updateValue(0);
        setStatus("Active");
        System.out.println("⚖️ WeightSensor " + getSensorId() + " started at " + currentWeight + weightUnit);
    }

    @Override
    protected void onStop() {
        try { simulationClock.unregister(this); } catch (Exception ignored) {}
        setStatus("Stopped");
        System.out.println("🛑 WeightSensor " + getSensorId() + " stopped at " + currentWeight + weightUnit);
    }

    @Override
    public void onTick(LocalDateTime currentTime) {
        synchronized (this) {
            if (!isActive()) return;
            if (!automaticMode) return; // gate automatic behavior
            if (lastActionTime == null || currentTime.minusSeconds(1).isAfter(lastActionTime)) {
                try {
                    int intervalMs = SimulationClock.getInstance().getTickIntervalMs();
                    setSimulationInterval(intervalMs);
                    if (controlEnabled && weightControlEnabled) {
                        performCycle();
                    } else {
                        // passive automatic addition if desired
                        updateValue(primaryIncrement);
                    }
                } catch (IllegalArgumentException e) {
                    sendAlert(getCalibratedWeight());
                    setStatus("Error");
                }
                lastActionTime = currentTime;
            }
        }
    }

    // -----------------------
    // Abstract implementations
    // -----------------------
    @Override
    public Object getValue() { return currentWeight; }

    @Override
    public void readValue() {
        calibrateSensor();
        double calibratedWeight = getCalibratedWeight();
        System.out.println(getSensorInfo() + " | Calibrated Weight: " + String.format("%.2f%s", calibratedWeight, weightUnit));
        boolean valid = validateReading();
        if (!valid) sendAlert(calibratedWeight);
        updateStatusAfterRead(valid);
        System.out.println("⚡ Status: " + (valid ? "Within Capacity" : "Out of Range"));
    }

    @Override
    public void updateValue(double change) {
        try {
            simulateWeight(change);
        } catch (IllegalArgumentException e) {
            sendAlert(getCalibratedWeight());
            setStatus("Error");
            return;
        }
        readValue();
    }

    @Override
    public void calibrateSensor() {
        if (!calibrated) {
            setStatus("Calibrating");
            System.out.println("🔧 Calibrating weight sensor " + getSensorId());
            this.calibrationOffset = 0.1 + Math.random() * 0.2;
            calibrated = true;
            setStatus("OK");
        }
    }

    public double getCalibratedWeight() {
        return currentWeight + calibrationOffset;
    }

    @Override
    public boolean validateReading() {
        double calibrated = getCalibratedWeight();
        return calibrated > 0 && calibrated <= machineCapacity;
    }

    @Override
    public void sendAlert(double currentValue) {
        raiseAlert("Weight out of range", String.format("%.2f%s", currentValue, weightUnit));
    }

    @Override
    public String getSensorInfo() {
        return String.format("WeightSensor[ID=%d, Type=%s, Location=%s, Status=%s, Current=%.2f%s, Capacity=%.2f%s, Auto=%s]",
                getSensorId(), getSensorType(), getLocation(), getStatus(),
                currentWeight, weightUnit, machineCapacity, weightUnit, automaticMode ? "On" : "Off");
    }

    // -----------------------
    // Simulation logic (raw value)
    // -----------------------
    private synchronized void simulateWeight(double change) {
        double newWeight = currentWeight + change;

        if (newWeight <= 0) {
            throw new IllegalArgumentException("Load cannot be ≤ 0. Attempted: " + newWeight + weightUnit);
        }
        if (newWeight > machineCapacity) {
            throw new IllegalArgumentException("Overload detected. Attempted: " + newWeight + weightUnit);
        }
        if (Math.abs(newWeight - lastWeight) > machineCapacity * 0.3) {
            throw new IllegalArgumentException("Shock load detected. Change: " + (newWeight - lastWeight) + weightUnit);
        }

        currentWeight = newWeight;
        lastWeight = currentWeight;
        setCurrentValue(currentWeight);
    }

    // Manual control
    public boolean addWeight(double weight) {
        try {
            updateValue(weight);
            System.out.println("⚖️ Added " + weight + weightUnit + " -> Current: " + currentWeight + weightUnit);
            return true;
        } catch (IllegalArgumentException e) {
            sendAlert(getCalibratedWeight());
            setStatus("Error");
            return false;
        }
    }

    public boolean removeWeight(double weight) {
        try {
            updateValue(-weight);
            System.out.println("⚖️ Removed " + weight + weightUnit + " -> Current: " + currentWeight + weightUnit);
            return true;
        } catch (IllegalArgumentException e) {
            sendAlert(getCalibratedWeight());
            setStatus("Error");
            return false;
        }
    }

    // Control wrappers specific to weight
    public void enableWeightControl(double capacity) {
        this.machineCapacity = capacity;
        this.weightControlEnabled = true;
        enableControl(currentWeight, 0.0);
    }

    public void disableWeightControl() {
        this.weightControlEnabled = false;
        disableControl();
    }

    @Override
    public void performCycle() {
        if (!weightControlEnabled) return;
        // simple control: try to maintain controlTarget (base) within controlTolerance
        double calibrated = getCalibratedWeight();
        if (calibrated < controlTarget - controlTolerance) {
            // add small increment
            updateValue(primaryIncrement);
        } else if (calibrated > controlTarget + controlTolerance) {
            // remove small amount (if safe)
            try {
                updateValue(-primaryIncrement);
            } catch (Exception e) {
                // if removal would violate safety, raise alert and disable automatic control
                raiseAlert("Auto-remove failed", e.getMessage());
                disableAutomaticMode();
                disableControl();
            }
        } else {
            setStatus("WithinDeadband");
        }
    }

    // getters
    public double getCurrentWeight() { return currentWeight; }
    public String getWeightUnit() { return weightUnit; }
    public double getMachineCapacity() { return machineCapacity; }
}
