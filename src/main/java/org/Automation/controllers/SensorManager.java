package org.Automation.controllers;

import org.Automation.entities.Sensor;
import java.util.ArrayList;
import java.util.List;

/**
 * SensorManager - Manages all sensors in the factory
 * Responsibilities:
 * - Add/remove sensors
 * - Update all sensor values
 * - Monitor sensor status
 * - Find sensors by ID or type
 */
public class SensorManager {
    private Map<Integer, Sensor> sensors;
    private int totalSensors;
    private int activeSensors;
    
    // Simulation clock for sensor manager
    private SimulationClock simulationClock;

    public SensorManager() {
        this.sensors = new HashMap<>();
        
        // Create simulation clock for sensor manager
        this.simulationClock = new SimulationClock("SensorManager", 2000) {
            @Override
            protected void tick() {
                super.tick();
                readValue();
            }
        };
        
        System.out.println("SensorManager initialized with empty sensor list");
    }

    // SimulationClock delegation
    public void start() { simulationClock.start(); }
    public void stop() { simulationClock.stop(); }
    public boolean isRunning() { return simulationClock.isRunning(); }
    public int getCurrentTime() { return simulationClock.getCurrentTime(); }
    public void setInterval(int intervalMs) { simulationClock.setInterval(intervalMs); }

    // Utility methods
    public int getSensorCount() {
        return totalSensors;
    }

    public Map<Integer, Sensor> getAllSensors() {
        return new HashMap<>(sensors);
    }

    private void updateCounts() {
        totalSensors = sensors.size();
        activeSensors = 0;
        for (Sensor sensor : sensors.values()) {
            if (sensor.isActive()) {
                activeSensors++;
            }
        }
    }

    public void updateValue() {
        readValue();
    }
    
    public Object getValue() {
        return getAllSensors();
    }


    // Core management methods
    public void addSensor(Sensor sensor) {
        if (sensor == null) {
            System.out.println("Error: Cannot add null sensor");
            return;
        }
        
        Integer key = sensor.getSensorId(); 
        
        if (sensors.containsKey(key)) {
            System.out.println("Sensor with ID " + key + " already exists");
            return;
        }
        
        sensors.put(key, sensor);
        totalSensors++;
        
        if (sensor.isActive()) {
            activeSensors++;
        }
        System.out.println("Added sensor: " + sensor);
    }

    public boolean removeSensor(int sensorId) {
        if (!sensors.containsKey(sensorId)) {
            System.out.println("Sensor with ID " + sensorId + " not found");
            return false;
        }
        Sensor removed = sensors.remove(sensorId);
        totalSensors--;
        if (removed.isActive()) {
            activeSensors--;
        }
        System.out.println("Removed sensor: " + removed);
        return true;
    }

    public void activateAllSensors() {
        System.out.println("\n=== Activating All Sensors ===");
        sensors.values().forEach(sensor -> {
            sensor.activateSensor();
            if (sensor.isActive()) {
                activeSensors++;
            }
        });
    }

    public void deactivateAllSensors() {
        System.out.println("\n=== Deactivating All Sensors ===");
        sensors.values().forEach(sensor -> {
            sensor.deactivateSensor();
            if (!sensor.isActive()) {
                activeSensors--;
            }
        });
    }

    // Search methods
    public Sensor findSensorById(int sensorId) {
        return sensors.get(sensorId); 
    }

    public List<Sensor> findSensorsByType(String sensorType) {
        return sensors.get(sensorType);
    }

    // Operations methods
    public void readValue() {
        System.out.println("\n=== Reading All Sensor Data ===");
        sensors.values().forEach(sensor -> {
            sensor.readSensorData();
            System.out.println("  " + sensor);
        });
        updateCounts();
    }

    public void updateAllSensors() {
        System.out.println("\n=== Updating All Sensors ===");
        sensors.forEach((id,sensor) -> {
            sensor.updateValue();
            System.out.println("  " + sensor);
        });
        updateCounts();
    }

    public void calibrateAllSensors() {
        System.out.println("\n=== Calibrating All Sensors ===");
        sensors.values().forEach(Sensor::calibrateSensor);
    }

    public void calibrateSensor(int sensorId) {
        Sensor sensor = findSensorById(sensorId);
        if (sensor != null) {
            sensor.calibrateSensor();
        } else {
            System.out.println("Sensor with ID " + sensorId + " not found for calibration");
        }
    }

    // Status monitoring
    public List<Sensor> getAlertSensors() {
        return sensors.values().stream()
                .filter(sensor -> sensor.getStatus().contains("Alert"))
                .toList();
    }

    public List<Sensor> getActiveSensors() {
        return sensors.values().stream()
                .filter(Sensor::isActive)
                .toList();
    }

    public List<Sensor> getInactiveSensors() {
        return sensors.values().stream()
                .filter(sensor -> !sensor.isActive())
                .toList();
    }

    // Display methods
    public void displayAllSensors() {
        System.out.println("\n=== All Sensors ===");
        if (sensors.isEmpty()) {
            System.out.println("  No sensors registered");
        } else {
            sensors.values().forEach(sensor -> System.out.println("  " + sensor));
        }
    }

    public void printSensorStatus() {
        updateCounts();
        System.out.println("\n=== Sensor Status Summary ===");
        System.out.println("Total sensors: " + totalSensors);
        System.out.println("Active sensors: " + activeSensors);
        System.out.println("Inactive sensors: " + (totalSensors - activeSensors));
        System.out.println("Alert sensors: " + getAlertSensors().size());
    }


    // Temperature control methods
    public void enableTemperatureControl(int sensorId, double targetTemp, double threshold) {
        Sensor sensor = findSensorById(sensorId);
        if (sensor instanceof TemperatureSensor) {
            ((TemperatureSensor) sensor).enableTemperatureControl(targetTemp, threshold);
        } else {
            System.out.println("Sensor " + sensorId + " is not a temperature sensor");
        }
    }
    
    public void enableAllTemperatureControl(double targetTemp, double threshold) {
        List<Sensor> tempSensors = findSensorsByType("Temperature");
        for (Sensor sensor : tempSensors) {
            ((TemperatureSensor) sensor).enableTemperatureControl(targetTemp, threshold);
        }
    }
    
    // Weight control methods
    public void enableWeightControl(int sensorId, double capacity) {
        Sensor sensor = findSensorById(sensorId);
        if (sensor instanceof WeightSensor) {
            ((WeightSensor) sensor).enableWeightControl(capacity);
        } else {
            System.out.println("Sensor " + sensorId + " is not a weight sensor");
        }
    }
    
    public boolean addWeightToSensor(int sensorId, double weight) {
        Sensor sensor = findSensorById(sensorId);
        if (sensor instanceof WeightSensor) {
            return ((WeightSensor) sensor).addWeight(weight);
        }
        System.out.println("Sensor " + sensorId + " is not a weight sensor");
        return false;
    }
    
    public void printControlStatus() {
        System.out.println("\n=== Control Status ===");
        for (Sensor sensor : sensors.values()) {
            if (sensor instanceof TemperatureSensor) {
                TemperatureSensor temp = (TemperatureSensor) sensor;
                System.out.println("Temp Sensor " + sensor.getSensorId() + ": " + 
                                 temp.getTemperatureStatus() + " (" + temp.getCurrentValue() + "°C)");
            } else if (sensor instanceof WeightSensor) {
                WeightSensor weight = (WeightSensor) sensor;
                System.out.println("Weight Sensor " + sensor.getSensorId() + ": " + 
                                 weight.getWeightStatus() + " (" + weight.getCurrentValue() + "kg)");
            }
        }
    }
}
