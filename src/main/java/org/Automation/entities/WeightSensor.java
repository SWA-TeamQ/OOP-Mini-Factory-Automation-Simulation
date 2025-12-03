package org.Automation.entities;

import org.Automation.controllers.SimulationClock;

public class WeightSensor extends Sensor {
    // ========== CORE WEIGHT FIELDS ==========
    private double minWeight;
    private double maxWeight;
    private double currentWeight;
    private String weightUnit;
    private double calibrationFactor;
    private double weightIncrement;
    
    // ========== CONTROL SYSTEM FIELDS ==========
    private double machineCapacity;
    private boolean weightControlEnabled;
    
    // ========== SIMULATION FIELDS ==========
    private SimulationClock simulationClock;
    private double startWeight;
    private boolean maxReached;

    // ========== CONSTRUCTOR ==========
    public WeightSensor(String sensorType, String location, String status, 
                       double minWeight, double maxWeight, String weightUnit) {
        super(sensorType, location, status);
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.weightUnit = weightUnit;
        this.currentWeight = minWeight;
        this.startWeight = minWeight;
        
        this.simulationClock = new SimulationClock("WeightSensor-" + getSensorId(), 1000) {
            @Override
            protected void tick() {
                super.tick();
                performWeightCycle();
            }
        };
    }

    // ========== GETTERS ==========
    public double getCurrentWeight() { return currentWeight; }
    public String getWeightUnit() { return weightUnit; }
    public double getMachineCapacity() { return machineCapacity; }
    public boolean isWeightControlEnabled() { return weightControlEnabled; }

    public String getSensorInfo() {
        return String.format("WeightSensor[ID=%d, Type=%s, Location=%s, Status=%s, " +
                           "Current=%.2f%s, Range=%.1f-%.1f%s, Capacity=%.1f%s, Control=%s]",
                           getSensorId(), getSensorType(), getLocation(), getStatus(),
                           currentWeight, weightUnit,
                           minWeight, maxWeight, weightUnit,
                           machineCapacity, weightUnit,
                           weightControlEnabled ? "Enabled" : "Disabled");
    }

    // ========== ABSTRACT METHOD IMPLEMENTATIONS ==========
    @Override
    public void readValue() {
        readSensorData();
    }

    @Override
    public void updateValue() {
        simulateWeight();
    }

    @Override
    public Object getValue() {
        return currentWeight;
    }

    @Override
    public void calibrateSensor() {
        System.out.println("🔧 Calibrating weight sensor " + getSensorId());
        this.calibrationFactor = 0.1;
    }

    @Override
    public boolean validateReading() {
        return currentWeight >= minWeight && currentWeight <= maxWeight;
    }

    @Override
    public void sendAlert() {
        System.out.println("🚨 WEIGHT ALERT - Sensor " + getSensorId() + 
                         ": " + currentWeight + weightUnit);
        updateStatus("Alert");
    }

    // ========== CORE WEIGHT METHODS ==========
    public void readSensorData() {
        double weight = simulateWeight();
        this.currentWeight = weight + calibrationFactor;
        setCurrentValue(currentWeight);
        
        System.out.println(getSensorInfo());
        
        if (!validateReading()) {
            sendAlert();
        }
    }

    public double simulateWeight() {
        this.currentWeight += weightIncrement;
        
        if (currentWeight > maxWeight) {
            currentWeight = maxWeight;
        } else if (currentWeight < minWeight) {
            currentWeight = minWeight;
        }
        
        this.currentWeight = Math.round(currentWeight);
        setCurrentValue(currentWeight);
        return currentWeight;
    }

    // ========== CONTROL SYSTEM METHODS ==========
    public void enableWeightControl(double capacity) {
        this.machineCapacity = capacity;
        this.weightControlEnabled = true;
    }

    public boolean addWeight(double weight) {
        double currentWeightValue = getCurrentWeight();
        if (currentWeightValue + weight <= machineCapacity) {
            this.currentWeight += weight;
            setCurrentValue(currentWeight);
            System.out.println("⚖️ Added " + weight + weightUnit + " to sensor " + getSensorId() + 
                             " (Total: " + getCurrentWeight() + weightUnit + ")");
            System.out.println(getSensorInfo());
            return true;
        } else {
            System.out.println("🚨 Cannot add " + weight + weightUnit + " - would exceed capacity!");
            System.out.println("Current: " + getCurrentWeight() + weightUnit + ", Capacity: " + machineCapacity + weightUnit);
            return false;
        }
    }

    // ========== SIMULATION CONTROL ==========
    public void start() {
        this.currentWeight = startWeight;
        setCurrentValue(currentWeight);
        maxReached = false;
        activateSensor();
        simulationClock.start();
        System.out.println("⚖️ Starting weight sensor " + getSensorId() + " simulation");
        System.out.println("Initial weight: " + getCurrentWeight() + weightUnit);
    }

    public void stop() {
        simulationClock.stop();
    }

    public boolean isRunning() {
        return simulationClock.isRunning();
    }

    public void setSimulationInterval(int intervalMs) {
        calculateIncrementForInterval(intervalMs);
        simulationClock.setInterval(intervalMs);
    }

    public void configureWeightParameters(double startWeight, double increment, int intervalMs) {
        this.startWeight = startWeight;
        setSimulationInterval(intervalMs);
        this.weightIncrement = increment;
    }

    // ========== PRIVATE SIMULATION HELPERS ==========
    private void performWeightCycle() {
        if (isActive() && !maxReached) {
            double currentWeightValue = getCurrentWeight();
            
            while (currentWeightValue < machineCapacity && !maxReached) {
                this.currentWeight += weightIncrement;
                setCurrentValue(currentWeight);
                currentWeightValue = getCurrentWeight();
                System.out.println("⚖️ Sensor " + getSensorId() + " loading: " + currentWeightValue + weightUnit);
            }
            
            this.currentWeight = machineCapacity;
            setCurrentValue(currentWeight);
            maxReached = true;
            deactivateSensor();
            simulationClock.stop();
            System.out.println("⚖️ Sensor " + getSensorId() + " reached capacity: " + machineCapacity + 
                             "kg - DEACTIVATED & STOPPED");
            System.out.println(getSensorInfo());
        }
    }

    private void calculateIncrementForInterval(int intervalMs) {
        if (intervalMs <= 1000) this.weightIncrement = 5.0;
        else if (intervalMs <= 2000) this.weightIncrement = 10.0;
        else this.weightIncrement = 15.0;
    }
}





