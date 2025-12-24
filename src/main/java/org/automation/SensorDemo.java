// package org.automation;

// import org.automation.entities.*;

// public class SensorDemo {
//     public static void main(String[] args) {
//         System.out.println("=== Factory Sensor Monitoring System ===\n");

//         // Create Temperature and Weight sensors
//         TemperatureSensor tempSensor = new TemperatureSensor("Temperature","Factory Floor A", "Active", 20.0,5, 80.0, "°C");
//         WeightSensor weightSensor = new WeightSensor("Weight", "Conveyor Belt 1", "Active", 50.0, 100.0, "kg");

//         System.out.println("Initial State:");
//         System.out.println(tempSensor);
//         System.out.println(weightSensor);

//         System.out.println("\n=== Updating Sensor Values ===\n");

//         // Simulate 5 readings
//         for (int i = 1; i <= 5; i++) {
//             System.out.println("Reading #" + i + ":");

//             tempSensor.updateValue(0.5);
//             weightSensor.updateValue(1.0);

//             System.out.println("  " + tempSensor);
//             System.out.println("  " + weightSensor);
//             System.out.println();

//             try {
//                 Thread.sleep(1000); // Wait 1 second between readings
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         }

//         System.out.println("=== Monitoring Complete ===");
//     }
// }

