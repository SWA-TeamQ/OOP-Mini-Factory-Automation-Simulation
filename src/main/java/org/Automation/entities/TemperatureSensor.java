package org.Automation.entities;

import org.Automation.Controllers.Simulators.SimulationClock;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureSensor extends Sensor implements SimulationClock.ClockObserver {
    
    // ========== CORE TEMPERATURE FIELDS ==========
    private double currentTemperature;
    private double minTemperature;
    private double maxTemperature;
    private String temperatureUnit;
    private double calibrationOffset;
    private double temperatureIncrement;
    
    // ========== CONTROL SYSTEM FIELDS ==========
    private double targetTemperature;
    private double temperatureThreshold;
    private boolean temperatureControlEnabled;
    
    // ========== SIMULATION FIELDS ==========
    private double startThreshold;
    private boolean maxReached;
    private double coolingRate;
    private LocalDateTime lastActionTime;

    // ========== CONSTRUCTOR ==========
    public TemperatureSensor(String sensorType, String location, String status, 
                           double minTemperature, double maxTemperature, String temperatureUnit) {
        super(sensorType, location, status);
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.temperatureUnit = temperatureUnit;
        this.currentTemperature = minTemperature;
        
        // Register with global simulation clock
        SimulationClock.getInstance().register(this);
    }


    @Override
    public void onTick(LocalDateTime currentTime) {
        if (lastActionTime == null || currentTime.minusSeconds(1).isAfter(lastActionTime)) {
            if (isActive() && !maxReached) {
                performTemperatureCycle();
            }
            lastActionTime = currentTime;
        }
    }

    // ========== GETTERS ==========
    public double getCurrentTemperature() { return currentTemperature; }
    public String getTemperatureUnit() { return temperatureUnit; }
    public double getTargetTemperature() { return targetTemperature; }
    public boolean isTemperatureControlEnabled() { return temperatureControlEnabled; }

    public String getSensorInfo() {
        return String.format("TemperatureSensor[ID=%d, Type=%s, Location=%s, Status=%s, " +
                           "Current=%.2f%s, Range=%.1f-%.1f%s, Target=%.1f%s, Control=%s]",
                           getSensorId(), getSensorType(), getLocation(), getStatus(),
                           currentTemperature, temperatureUnit,
                           minTemperature, maxTemperature, temperatureUnit,
                           targetTemperature, temperatureUnit,
                           temperatureControlEnabled ? "Enabled" : "Disabled");
    }

    // ========== ABSTRACT METHOD IMPLEMENTATIONS ==========
    @Override
    public void readValue() {
        readSensorData();
    }

    @Override
    public void updateValue() {
        simulateTemperature();
    }

    @Override
    public Object getValue() {
        return currentTemperature;
    }

    @Override
    public void calibrateSensor() {
        System.out.println("🔧 Calibrating temperature sensor " + getSensorId());
        this.calibrationOffset = 0.5;
    }

    @Override
    public boolean validateReading() {
        return currentTemperature >= minTemperature && currentTemperature <= maxTemperature;
    }

    @Override
    public void sendAlert() {
        System.out.println("🚨 TEMPERATURE ALERT - Sensor " + getSensorId() + 
                         ": " + currentTemperature + temperatureUnit);
        updateStatus("Alert");
    }

    // ========== CORE TEMPERATURE METHODS ==========
    public void readSensorData() {
        double temp = simulateTemperature();
        this.currentTemperature = temp + calibrationOffset;
        setCurrentValue(currentTemperature);
        
        System.out.println(getSensorInfo());
        
        if (!validateReading()) {
            sendAlert();
        }
    }

    public double simulateTemperature() {
       this.currentTemperature += temperatureIncrement;
        
        if (currentTemperature > maxTemperature) {
            currentTemperature = maxTemperature;
        } else if (currentTemperature < minTemperature) {
            currentTemperature = minTemperature;
        }
        
        this.currentTemperature = Math.round(currentTemperature);
        setCurrentValue(currentTemperature);
        return currentTemperature;
    }

    // ========== CONTROL SYSTEM METHODS ==========
    public void enableTemperatureControl(double targetTemp, double threshold) {
        this.targetTemperature = targetTemp;
        this.temperatureThreshold = threshold;
        this.temperatureControlEnabled = true;
    }

    public String getTemperatureStatus() {
        if (!temperatureControlEnabled) {
            return "Control Disabled";
        }
        
        double difference = Math.abs(currentTemperature - targetTemperature);
        
        if (difference <= temperatureThreshold) {
            return "Within Target Range";
        } else if (currentTemperature > targetTemperature + temperatureThreshold) {
            return "Too Hot";
        } else {
            return "Too Cold";
        }
    }

    // ========== SIMULATION CONTROL ==========
    public void start() {
        this.currentTemperature = startThreshold;
        setCurrentValue(currentTemperature);
        maxReached = false;
        activateSensor();
        System.out.println("🌡️ Starting temperature sensor " + getSensorId() + " simulation");
        System.out.println("Initial temperature: " + getCurrentTemperature() + temperatureUnit);
    }

    public void stop() {
        deactivateSensor();
    }
    
    public boolean isRunning() { 
        return isActive();
    }

    public void configureHeatingParameters(double startThreshold, double increment, double coolingRate) {
        this.startThreshold = startThreshold;
        this.coolingRate = coolingRate;
        this.temperatureIncrement = increment;
    }

    // ========== PRIVATE SIMULATION HELPERS ==========
    private void performTemperatureCycle() {
        if (isActive() && !maxReached) {
            this.currentTemperature += temperatureIncrement;
            setCurrentValue(currentTemperature);
            System.out.println("🌡️ Sensor " + getSensorId() + " heating: " + getCurrentTemperature() + temperatureUnit);
            
            if (currentTemperature >= maxTemperature) {
                this.currentTemperature = maxTemperature;
                setCurrentValue(currentTemperature);
                maxReached = true;
                deactivateSensor();
                System.out.println("🌡️ Sensor " + getSensorId() + " reached max: " + maxTemperature + "°C - DEACTIVATED");
                System.out.println(getSensorInfo());
                startCoolingMonitor();
            }
        }
    }
    
    private void startCoolingMonitor() {
        Timer coolingTimer = new Timer("Cooling-" + getSensorId(), true);
        coolingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double cooledTemp = currentTemperature - coolingRate;
                currentTemperature = Math.max(cooledTemp, startThreshold);
                setCurrentValue(currentTemperature);
                
                System.out.println("❄️ Sensor " + getSensorId() + " cooling: " + getCurrentTemperature() + temperatureUnit);
                
                if (currentTemperature <= startThreshold + 5.0) {
                    coolingTimer.cancel();
                    System.out.println("🔄 Sensor " + getSensorId() + " temperature dropped - RESTARTING");
                    maxReached = false;
                    activateSensor();
                    start();
    }
            }
        }, 2000, 2000);
    }
}


