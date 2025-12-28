package org.automation.entities;

import org.automation.engine.SimulationClock;
import org.automation.engine.ClockObserver;

import java.time.LocalDateTime;

/**
 * TemperatureSensor
 * - Implements baseline increment and cooling baseline
 * - Registers/unregisters with SimulationClock in onStart/onStop hooks
 * - Uses primaryIncrement and coolingRate set by Sensor.setSimulationInterval(...)
 * - Uses Sensor's automaticMode and controlEnabled flags; provides a convenience wrapper
 */
public class TemperatureSensor extends Sensor implements ClockObserver {

    private double currentTemperature;
    private final String temperatureUnit;
    private double calibrationOffset;
    private boolean calibrated = false;

    private double targetTemperature;
    private double temperatureTolerance;

    private final double startThreshold;
    private LocalDateTime lastActionTime;

    public TemperatureSensor(String sensorType, String location, String status,
                             double startThreshold, double temperatureTolerance,
                             double targetTemperature, String temperatureUnit) {
        super(sensorType, location, status);
        this.startThreshold = startThreshold;
        this.temperatureTolerance = temperatureTolerance;
        this.targetTemperature = targetTemperature;
        this.temperatureUnit = temperatureUnit;
        this.currentTemperature = startThreshold;
        // initialize local targets only
    }

    /**
     * Construct a TemperatureSensor with an explicit sensor id (used when loading from DB).
     */
    public TemperatureSensor(int sensorId, String sensorType, String location, String status,
                             double startThreshold, double temperatureTolerance,
                             double targetTemperature, String temperatureUnit) {
        super(sensorId, sensorType, location, status);
        this.startThreshold = startThreshold;
        this.temperatureTolerance = temperatureTolerance;
        this.targetTemperature = targetTemperature;
        this.temperatureUnit = temperatureUnit;
        this.currentTemperature = startThreshold;
        // initialize local targets only
    }

    public TemperatureSensor(String sensorType, int location, String status,
                             double startThreshold, double temperatureTolerance,
                             double targetTemperature) {
        this(sensorType, String.valueOf(location), status, startThreshold, temperatureTolerance, targetTemperature, "°C");
    }

    @Override
    protected double getBaselineIncrement() { return 0.5; }

    @Override
    protected double getBaselineCooling() { return 0.25; }

    @Override
    public void onTick(LocalDateTime currentTime) {
        synchronized (this) {
            if (!isActive()) return;

            if (lastActionTime == null || currentTime.minusSeconds(1).isAfter(lastActionTime)) {
                try {
                    int intervalMs = SimulationClock.getInstance().getTickIntervalMs();
                    setSimulationInterval(intervalMs);
                    // Always run cycle; automatic mode removed
                    performCycle();
                } catch (Exception e) {
                    sendAlert(currentTemperature + calibrationOffset);
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
    public Object getValue() { return currentTemperature; }

    @Override
    public void readValue() {
        calibrateSensor();
        double calibratedTemp = currentTemperature + calibrationOffset;
        System.out.println(getSensorInfo() + " | Calibrated Temperature: " + String.format("%.2f%s", calibratedTemp, temperatureUnit));
        boolean valid = validateReading(calibratedTemp);
        if (!valid) sendAlert(calibratedTemp);
        updateStatusAfterRead(valid);
        System.out.println("⚡ Status: " + getTemperatureStatus(calibratedTemp));
        System.out.println("Current: " + currentTemperature + ", Calibrated: " + (currentTemperature + calibrationOffset) + ", Target: " + targetTemperature + ", Tolerance: " + temperatureTolerance);

    }

    @Override
    public void updateValue(double change) {
        simulateTemperature(change);
        readValue();
    }

    @Override
    public void calibrateSensor() {
        if (!calibrated) {
            setStatus("Calibrating");
            System.out.println("🔧 Calibrating temperature sensor " + getSensorId());
            this.calibrationOffset = 0.05 + Math.random() * 0.1;
            calibrated = true;
            setStatus("Calibrated");
        }
    }

    @Override
    public boolean validateReading() {
        return validateReading(currentTemperature + calibrationOffset);
    }

    public boolean validateReading(double calibratedTemp) {
        return Math.abs(calibratedTemp - targetTemperature) <= temperatureTolerance;
    }

    @Override
    public void sendAlert(double currentTemp) {
        double calibratedTemp = currentTemp + calibrationOffset;
        raiseAlert("Temperature out of range", temperatureUnit + " (Calibrated: " + String.format("%.2f", calibratedTemp) + ")");
    }

    @Override
    public String getSensorInfo() {
        return String.format(
            "TemperatureSensor[ID=%d, Type=%s, Location=%s, Status=%s, Current=%.2f%s, Start=%.2f%s, Target=%.2f%s, Tolerance=%.2f%s]",
            getSensorId(), getSensorType(), getLocation(), getStatus(),
            currentTemperature, temperatureUnit,
            startThreshold, temperatureUnit,
            targetTemperature, temperatureUnit,
            temperatureTolerance, temperatureUnit
        );
    }

    // -----------------------
    // Simulation logic
    // -----------------------
    public double simulateTemperature(double change) {
        currentTemperature += change;
        setCurrentValue(currentTemperature);
        return currentTemperature;
    }

public String getTemperatureStatus(double tempToCheck) {
    // Too cold only if below the minimum allowed by start threshold and tolerance
    if (tempToCheck < startThreshold - temperatureTolerance) {
        return "Too Cold";
    }

    // Too hot only if above the maximum allowed by target temperature and tolerance
    if (tempToCheck > targetTemperature + temperatureTolerance) {
        return "Too Hot";
    }

    // Otherwise, within range
    return "Within Target Range";
}

    // -----------------------
    // Heating / Cooling cycle (uses primaryIncrement and coolingRate)
    // -----------------------
private void startHeating() {
    updateValue(primaryIncrement);
    if (currentTemperature >= targetTemperature) {
        currentTemperature = targetTemperature;
        setStatus("WithinTarget");
    } else {
        setStatus("Heating");
    }
}

private void startCooling() {
    updateValue(-coolingRate);
    if (currentTemperature <= startThreshold) {
        currentTemperature = startThreshold;
        setStatus("WithinTarget");
    } else {
        setStatus("Cooling");
    }
}


@Override
public void performCycle() {
   double calibrated = currentTemperature + calibrationOffset;

    double lowerLimit = targetTemperature - temperatureTolerance; // heat below this
    double upperLimit = targetTemperature + temperatureTolerance; // cool above this

    if (calibrated < lowerLimit) {
        startHeating();
    } else if (calibrated > upperLimit) {
        startCooling();
    } else {
        setStatus("WithinDeadband"); // do nothing
    }

    // Safety alerts
    if (calibrated < startThreshold - temperatureTolerance || calibrated > targetTemperature + temperatureTolerance) {
        sendAlert(currentTemperature);
    }
}


    // getters
    public double getCurrentTemperature() { return currentTemperature; }
    public String getTemperatureUnit() { return temperatureUnit; }
    public double getTargetTemperature() { return targetTemperature; }
    public double getStartThreshold() { return startThreshold; }
    public double getTemperatureTolerance() { return temperatureTolerance; }
}
