// package org.automation;

// import org.automation.controllers.SensorManager;
// import org.automation.entities.*;
// import java.util.List;

// /**
//  * Demo showing SensorManager initialization and usage
//  */
// public class SensorManagerDemo {
//     public static void main(String[] args) {
//         System.out.println("=== SensorManager Initialization Demo ===\n");
        
//         // Step 1: Create SensorManager (initializes empty list)
//         System.out.println("Step 1: Creating SensorManager");
//         SensorManager manager = new SensorManager();
//         // System.out.println("  Sensor count: " + manager.getSensorCount());
        
//         // Step 2: Add Temperature Sensors
//         System.out.println("\nStep 2: Adding Temperature Sensors");
//         TemperatureSensor temp1 = new TemperatureSensor("Temperature", "Factory Floor A", "Active", 20.0, 5, 80.0, "°C");
//         TemperatureSensor temp2 = new TemperatureSensor("Temperature", "Factory Floor B", "Active", 15.0,6, 75.0, "°C");
        
//         manager.addSensor(temp1);
//         manager.addSensor(temp2);
//         // System.out.println("  Sensor count: " + manager.getSensorCount());
        
//         // Step 3: Add Weight Sensors
//         System.out.println("\nStep 3: Adding Weight Sensors");
//         WeightSensor weight1 = new WeightSensor("Weight", "Conveyor Belt 1", "Active", 50.0, 100.0, "kg");
//         WeightSensor weight2 = new WeightSensor("Weight", "Conveyor Belt 2", "Active", 10.0, 50.0, "kg");
        
//         manager.addSensor(weight1);
//         manager.addSensor(weight2);
//         // System.out.println("  Sensor count: " + manager.getSensorCount());
        
//         // Step 4: Display all sensors
//         manager.displayAllSensors();
        
//         // Step 5: Update all sensors (simulate readings)
//         // System.out.println("\nStep 5: Updating all sensors (3 readings)");
//         // for (int i = 1; i <= 3; i++) {
//         //     System.out.println("\n--- Reading #" + i + " ---");
//         //     manager.updateAllSensors();
            
//         //     // Check for alerts
//         //     List<Sensor> alerts = manager.getAlertSensors();
//         //     if (!alerts.isEmpty()) {
//         //         System.out.println("\n⚠️  ALERTS DETECTED:");
//         //         for (Sensor sensor : alerts) {
//         //             System.out.println("    " + sensor);
//         //         }
//         //     }
            
//         //     try {
//         //         Thread.sleep(1000);
//         //     } catch (InterruptedException e) {
//         //         e.printStackTrace();
//         //     }
//         // }
        
//         // Step 6: Find sensors by type
//         // System.out.println("\n\nStep 6: Finding sensors by type");
//         // List<Sensor> tempSensors = manager.findSensorsByType("Temperature");
//         // System.out.println("  Temperature sensors: " + tempSensors.size());
//         // for (Sensor s : tempSensors) {
//         //     System.out.println("    " + s);
//         // }
        
//         // List<Sensor> weightSensors = manager.findSensorsByType("Weight");
//         // System.out.println("  Weight sensors: " + weightSensors.size());
//         // for (Sensor s : weightSensors) {
//         //     System.out.println("    " + s);
//         // }
        
//         // Step 7: Find sensor by ID
//         System.out.println("\nStep 7: Finding sensor by ID");
//         Sensor found = manager.findSensorById(2);
//         if (found != null) {
//             System.out.println("  Found: " + found);
//         }
        
//         // Step 8: Remove a sensor
//         System.out.println("\nStep 8: Removing sensor with ID 3");
//         manager.removeSensor(3);
//         // System.out.println("  Sensor count: " + manager.getSensorCount());
        
//         manager.displayAllSensors();
        
//         System.out.println("\n=== Demo Complete ===");
//     }
// }

