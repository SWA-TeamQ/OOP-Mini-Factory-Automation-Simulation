// package org.automation;

// import org.automation.entities.TemperatureSensor;

// public class TestInitialization {
//     public static void main(String[] args) {
//         System.out.println("=== Testing Initialization ===\n");
        
//         Create sensor with minTemp=20, maxTemp=80
//         TemperatureSensor sensor = new TemperatureSensor("Temperature", "Factory Floor A", "Active", 20.0, 5,80.0, "°C");
        
//         // System.out.println("After constructor:");
//         // System.out.println("  minTemp = " + sensor.getMinTemp());  // Should be 20.0
//         // System.out.println("  maxTemp = " + sensor.getMaxTemp());  // Should be 80.0
        
//         System.out.println("\nCalling updateValue() 5 times:");
//         for (int i = 1; i <= 5; i++) {
//             sensor.updateValue(0.5);
//             double temp = (Double) sensor.getValue();
//             System.out.println("  Reading #" + i + ": " + temp + "°C");
            
//             // Verify temperature is using minTemp and maxTemp correctly
//             if (temp >= 20.0 && temp <= 80.0) {
//                 System.out.println("    ✅ Within range [20.0 - 80.0]");
//             } else {
//                 System.out.println("    ⚠️ Outside range (testing alert condition)");
//             }
//         }
        
//         System.out.println("\n=== Conclusion ===");
//         System.out.println("minTemp and maxTemp ARE initialized in the constructor!");
//         System.out.println("They are used by simulateTemperature() to generate values.");
//     }
// }

