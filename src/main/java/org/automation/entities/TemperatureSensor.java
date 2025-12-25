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
    private boolean temperatureControlEnabled;

    private final double startThreshold;
    private LocalDateTime lastActionTime;

    private SimulationClock simulationClock;

    public TemperatureSensor(String sensorType, String location, String status,
                             double startThreshold, double temperatureTolerance,
                             double targetTemperature, String temperatureUnit) {
        super(sensorType, location, status);
        this.startThreshold = startThreshold;
        this.temperatureTolerance = temperatureTolerance;
        this.targetTemperature = targetTemperature;
        this.temperatureUnit = temperatureUnit;
        this.currentTemperature = startThreshold;
        // initialize control target/tolerance in base
        this.controlTarget = targetTemperature;
        this.controlTolerance = temperatureTolerance;
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
        this.controlTarget = targetTemperature;
        this.controlTolerance = temperatureTolerance;
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
    protected void onStart() {
        this.simulationClock = SimulationClock.getInstance();
        this.simulationClock.register(this);
        // initialize increments based on current clock interval so first cycle is aligned
        try { setSimulationInterval(this.simulationClock.getTickIntervalMs()); } catch (Exception ignored) {}
        calibrateSensor();
        activateSensor();
        lastActionTime = null;
        // sync subclass target with base control fields
        this.controlTarget = this.targetTemperature;
        this.controlTolerance = this.temperatureTolerance;
        enableTemperatureControl(this.targetTemperature, this.temperatureTolerance);
        updateValue(0);
        setStatus("Active");
        System.out.println("🌡️ TemperatureSensor " + getSensorId() + " started at " + currentTemperature + temperatureUnit);
    }

    @Override
    protected void onStop() {
        try { simulationClock.unregister(this); } catch (Exception ignored) {}
        deactivateSensor();
        temperatureControlEnabled = false;
        lastActionTime = null;
        calibrated = false;
       updateValue(0);
        setStatus("Stopped");
        System.out.println("🛑 TemperatureSensor " + getSensorId() + " stopped at " + currentTemperature + temperatureUnit);
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
                    if (controlEnabled && temperatureControlEnabled) {
                        performCycle();
                    } else {
                        // passive behavior: small drift or no-op; here we apply primaryIncrement as passive growth
                        updateValue(primaryIncrement);
                    }
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
                "TemperatureSensor[ID=%d, Type=%s, Location=%s, Status=%s, Current=%.2f%s, Start=%.2f%s, Target=%.2f%s, Tolerance=%.2f%s, Control=%s, Auto=%s]",
                getSensorId(), getSensorType(), getLocation(), getStatus(),
                currentTemperature, temperatureUnit,
                startThreshold, temperatureUnit,
                targetTemperature, temperatureUnit,
                temperatureTolerance, temperatureUnit,
                temperatureControlEnabled ? "Enabled" : "Disabled",
                automaticMode ? "On" : "Off"
        );
    }

    // -----------------------
    // Simulation logic
    // -----------------------
    public double simulateTemperature(double change) {
        currentTemperature += change;
        if (currentTemperature > targetTemperature) currentTemperature = targetTemperature;
        if (currentTemperature < startThreshold) currentTemperature = startThreshold;
        setCurrentValue(currentTemperature);
        return currentTemperature;
    }

    public void enableTemperatureControl(double targetTemp, double tolerance) {
        this.targetTemperature = targetTemp;
        this.temperatureTolerance = tolerance;
        this.temperatureControlEnabled = true;
        // sync base control fields
        enableControl(targetTemp, tolerance);
    }

    public void disableTemperatureControl() {
        this.temperatureControlEnabled = false;
        disableControl();
    }

  public String getTemperatureStatus(double tempToCheck) {
    if (!temperatureControlEnabled) return "Control Disabled";

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
    if (!temperatureControlEnabled) return;

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
    public boolean isTemperatureControlEnabled() { return temperatureControlEnabled; }
    public double getStartThreshold() { return startThreshold; }
    public double getTemperatureTolerance() { return temperatureTolerance; }
}
