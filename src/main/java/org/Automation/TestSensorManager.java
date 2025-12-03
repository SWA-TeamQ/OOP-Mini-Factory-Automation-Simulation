package org.Automation;

import org.Automation.controllers.SensorManager;
import org.Automation.entities.*;
import java.util.List;

public class TestSensorManager {
    public static void main(String[] args) {
        System.out.println("=== SensorManager Test Suite ===\n");
        
        // Step 1: Create SensorManager
        System.out.println("Step 1: Creating SensorManager");
        SensorManager manager = new SensorManager();
        System.out.println("Initial sensor count: " + manager.getSensorCount());
        
        // Step 2: Add Temperature Sensors
        System.out.println("\nStep 2: Adding Temperature Sensors");
        TemperatureSensor temp1 = new TemperatureSensor("Temperature", "Factory Floor A", "Active", 20.0, 80.0, "°C");
        TemperatureSensor temp2 = new TemperatureSensor("Temperature", "Factory Floor B", "Active", 15.0, 75.0, "°C");
        
        manager.addSensor(temp1);
        manager.addSensor(temp2);
        System.out.println("Sensor count after adding temperature sensors: " + manager.getSensorCount());
        
        // Step 3: Add Weight Sensors
        System.out.println("\nStep 3: Adding Weight Sensors");
        WeightSensor weight1 = new WeightSensor("Weight", "Conveyor Belt 1", "Active", 50.0, 100.0, "kg");
        WeightSensor weight2 = new WeightSensor("Weight", "Conveyor Belt 2", "Active", 10.0, 50.0, "kg");
        
        manager.addSensor(weight1);
        manager.addSensor(weight2);
        System.out.println("Sensor count after adding weight sensors: " + manager.getSensorCount());
        
        // Step 4: Display all sensors
        manager.displayAllSensors();
        
        // Step 5: Test sensor reading
        System.out.println("\nStep 5: Reading all sensor data (3 times)");
        for (int i = 1; i <= 3; i++) {
            System.out.println("\n--- Reading #" + i + " ---");
            manager.readAllSensorData();
            
            // Check for alerts
            List<Sensor> alerts = manager.getAlertSensors();
            if (!alerts.isEmpty()) {
                System.out.println("\n⚠️  ALERTS DETECTED:");
                for (Sensor sensor : alerts) {
                    System.out.println("    " + sensor);
                }
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Step 6: Test search by type
        System.out.println("\n\nStep 6: Testing search by type");
        List<Sensor> tempSensors = manager.findSensorsByType("Temperature");
        System.out.println("Temperature sensors found: " + tempSensors.size());
        tempSensors.forEach(s -> System.out.println("  " + s));
        
        List<Sensor> weightSensors = manager.findSensorsByType("Weight");
        System.out.println("Weight sensors found: " + weightSensors.size());
        weightSensors.forEach(s -> System.out.println("  " + s));
        
        // Step 7: Test search by location
        System.out.println("\nStep 7: Testing search by location");
        List<Sensor> floorASensors = manager.findSensorsByLocation("Factory Floor A");
        System.out.println("Sensors at Factory Floor A: " + floorASensors.size());
        floorASensors.forEach(s -> System.out.println("  " + s));
        
        // Step 8: Test find by ID
        System.out.println("\nStep 8: Testing find by ID");
        Sensor found = manager.findSensorById(2);
        if (found != null) {
            System.out.println("Found sensor with ID 2: " + found);
        } else {
            System.out.println("Sensor with ID 2 not found");
        }
        
        // Step 9: Test calibration
        System.out.println("\nStep 9: Testing sensor calibration");
        manager.calibrateSensor(1);
        manager.calibrateAllSensors();
        
        // Step 10: Test activation/deactivation
        System.out.println("\nStep 10: Testing activation/deactivation");
        manager.deactivateAllSensors();
        manager.printSensorStatus();
        
        manager.activateAllSensors();
        manager.printSensorStatus();
        
        // Step 11: Test sensor removal
        System.out.println("\nStep 11: Testing sensor removal");
        System.out.println("Removing sensor with ID 3");
        boolean removed = manager.removeSensor(3);
        System.out.println("Removal successful: " + removed);
        System.out.println("Final sensor count: " + manager.getSensorCount());
        
        manager.displayAllSensors();
        
        // Step 12: Test edge cases
        System.out.println("\nStep 12: Testing edge cases");
        manager.addSensor(null); // Should handle null
        manager.removeSensor(999); // Should handle non-existent ID
        manager.calibrateSensor(999); // Should handle non-existent ID
        
        System.out.println("\n=== Test Complete ===");
    }
}