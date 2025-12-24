// package org.automation.ui;

// import org.automation.engine.SimulationEngine;
// import org.automation.controllers.WorkflowController;
// import org.automation.controllers.SensorManager;
// import org.automation.entities.Sensor;
// import org.automation.entities.TemperatureSensor;
// import org.automation.entities.WeightSensor;
// import org.automation.utils.Logger;

// import java.util.List;
// import java.util.Scanner;

// public class ConsoleApp extends ConsoleUI {
// 	//Instance Variables
// 	private SimulationEngine simulationEngine;
// 	private WorkflowController controller;
// 	private SensorManager sensorManager;
// 	private Logger logger;
// 	private Scanner scanner;
// 	private boolean running;
	
// 	public ConsoleApp(){
// 		simulationEngine = new SimulationEngine();
// 		sensorManager = new SensorManager();
// 		logger = new Logger();
// 		scanner = new Scanner(System.in);
// 		running = false;
// 	}
	
// 	//Instance Methods
// 	public void start() {
// 		running = true;
// 		printWelcomeMessage();
// 		runMainMenu();
// 	}
	
// 	public void runMainMenu() {
// 		while (running) {
// 			printMainMenu();
// 			String input = scanner.nextLine().trim();
// 			handleUserInput(input);
// 		}
// 	}
	
// 	private void printMainMenu() {
// 		System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
// 		System.out.println("║         FACTORY AUTOMATION - SENSOR MANAGEMENT SYSTEM         ║");
// 		System.out.println("╠═══════════════════════════════════════════════════════════════╣");
// 		System.out.println("║  [1] Add New Sensor                                           ║");
// 		System.out.println("║  [2] Remove Sensor                                            ║");
// 		System.out.println("║  [3] List All Sensors                                         ║");
// 		System.out.println("║  [4] View Sensor Details                                      ║");
// 		System.out.println("║  [5] Start Sensor                                             ║");
// 		System.out.println("║  [6] Stop Sensor                                              ║");
// 		System.out.println("║  [7] Start All Sensors                                        ║");
// 		System.out.println("║  [8] Stop All Sensors                                         ║");
// 		System.out.println("║  [9] Enable Automatic Mode (Single Sensor)                    ║");
// 		System.out.println("║  [10] Disable Automatic Mode (Single Sensor)                  ║");
// 		System.out.println("║  [11] Enable Control Mode (Single Sensor)                     ║");
// 		System.out.println("║  [12] Disable Control Mode (Single Sensor)                    ║");
// 		System.out.println("║  [13] Enable Automatic Mode (All Sensors)                     ║");
// 		System.out.println("║  [14] Disable Automatic Mode (All Sensors)                    ║");
// 		System.out.println("║  [0] Exit                                                     ║");
// 		System.out.println("╚═══════════════════════════════════════════════════════════════╝");
// 		System.out.print("Select an option: ");
// 	}
	
// 	public void handleUserInput(String input) {
// 		switch (input) {
// 			case "1" -> addSensorMenu();
// 			case "2" -> removeSensorMenu();
// 			case "3" -> listAllSensors();
// 			case "4" -> viewSensorDetails();
// 			case "5" -> startSensorMenu();
// 			case "6" -> stopSensorMenu();
// 			case "7" -> startAllSensors();
// 			case "8" -> stopAllSensors();
// 			case "9" -> enableAutomaticModeMenu();
// 			case "10" -> disableAutomaticModeMenu();
// 			case "11" -> enableControlModeMenu();
// 			case "12" -> disableControlModeMenu();
// 			case "13" -> enableAutomaticModeAll();
// 			case "14" -> disableAutomaticModeAll();
// 			case "0" -> exit();
// 			default -> System.out.println("❌ Invalid option. Please try again.");
// 		}
// 	}
	
// 	// ===========================
// 	// Menu Options Implementation
// 	// ===========================
	
// 	private void addSensorMenu() {
// 		System.out.println("\n--- Add New Sensor ---");
// 		System.out.println("Select sensor type:");
// 		System.out.println("[1] Temperature Sensor");
// 		System.out.println("[2] Weight Sensor");
// 		System.out.print("Choice: ");
// 		String choice = scanner.nextLine().trim();
		
// 		try {
// 			System.out.print("Enter Sensor ID: ");
// 			int id = Integer.parseInt(scanner.nextLine().trim());
			
// 			System.out.print("Enter Location: ");
// 			String location = scanner.nextLine().trim();
			
// 			Sensor sensor = null;
			
// 			if (choice.equals("1")) {
// 				// Temperature Sensor
// 				System.out.print("Enter Start Threshold: ");
// 				double startThreshold = Double.parseDouble(scanner.nextLine().trim());
				
// 				System.out.print("Enter Tolerance: ");
// 				double tolerance = Double.parseDouble(scanner.nextLine().trim());
				
// 				System.out.print("Enter Target Temperature: ");
// 				double targetTemp = Double.parseDouble(scanner.nextLine().trim());
				
// 				System.out.print("Enter Temperature Unit (°C/°F): ");
// 				String unit = scanner.nextLine().trim();
				
// 				sensor = new TemperatureSensor(id, "Temperature", location, "inactive",
// 					startThreshold, tolerance, targetTemp, unit);
// 			} else if (choice.equals("2")) {
// 				// Weight Sensor
// 				System.out.print("Enter Initial Weight: ");
// 				double initialWeight = Double.parseDouble(scanner.nextLine().trim());
				
// 				System.out.print("Enter Capacity: ");
// 				double capacity = Double.parseDouble(scanner.nextLine().trim());
				
// 				System.out.print("Enter Weight Unit (kg/lb): ");
// 				String unit = scanner.nextLine().trim();
				
// 				sensor = new WeightSensor(id, "Weight", location, "inactive",
// 					initialWeight, capacity, unit);
// 			} else {
// 				System.out.println("❌ Invalid sensor type.");
// 				return;
// 			}
			
// 			sensorManager.addSensor(sensor);
// 			System.out.println("✅ Sensor added successfully and saved to database!");
			
// 		} catch (NumberFormatException e) {
// 			System.out.println("❌ Invalid input. Please enter valid numbers.");
// 		} catch (Exception e) {
// 			System.out.println("❌ Error adding sensor: " + e.getMessage());
// 		}
// 	}
	
// 	private void removeSensorMenu() {
// 		System.out.println("\n--- Remove Sensor ---");
// 		System.out.print("Enter Sensor ID to remove: ");
// 		try {
// 			int id = Integer.parseInt(scanner.nextLine().trim());
// 			if (sensorManager.removeSensor(id)) {
// 				System.out.println("✅ Sensor removed successfully from database!");
// 			} else {
// 				System.out.println("❌ Sensor not found.");
// 			}
// 		} catch (NumberFormatException e) {
// 			System.out.println("❌ Invalid ID format.");
// 		}
// 	}
	
// 	private void listAllSensors() {
// 		System.out.println("\n╔═════════════════════════════════════════════════════════╗");
// 		System.out.println("║                   ALL SENSORS LIST                      ║");
// 		System.out.println("╚═════════════════════════════════════════════════════════╝");
		
// 		List<String> sensorInfo = sensorManager.listSensorInfo();
// 		if (sensorInfo.isEmpty()) {
// 			System.out.println("📭 No sensors found in the system.");
// 		} else {
// 			for (int i = 0; i < sensorInfo.size(); i++) {
// 				System.out.println((i + 1) + ". " + sensorInfo.get(i));
// 			}
// 		}
// 	}
	
// 	private void viewSensorDetails() {
// 		System.out.println("\n--- View Sensor Details ---");
// 		System.out.print("Enter Sensor ID: ");
// 		try {
// 			int id = Integer.parseInt(scanner.nextLine().trim());
// 			String info = sensorManager.getSensorInfo(id);
// 			if (info != null) {
// 				System.out.println("\n📊 Sensor Details:");
// 				System.out.println(info);
// 			} else {
// 				System.out.println("❌ Sensor not found.");
// 			}
// 		} catch (NumberFormatException e) {
// 			System.out.println("❌ Invalid ID format.");
// 		}
// 	}
	
// 	private void startSensorMenu() {
// 		System.out.println("\n--- Start Sensor ---");
// 		System.out.print("Enter Sensor ID: ");
// 		try {
// 			int id = Integer.parseInt(scanner.nextLine().trim());
// 			if (sensorManager.startSensor(id)) {
// 				System.out.println("✅ Sensor started successfully!");
// 			} else {
// 				System.out.println("❌ Failed to start sensor. It may already be running or not exist.");
// 			}
// 		} catch (NumberFormatException e) {
// 			System.out.println("❌ Invalid ID format.");
// 		}
// 	}
	
// 	private void stopSensorMenu() {
// 		System.out.println("\n--- Stop Sensor ---");
// 		System.out.print("Enter Sensor ID: ");
// 		try {
// 			int id = Integer.parseInt(scanner.nextLine().trim());
// 			if (sensorManager.stopSensor(id)) {
// 				System.out.println("✅ Sensor stopped successfully!");
// 			} else {
// 				System.out.println("❌ Failed to stop sensor. It may already be stopped or not exist.");
// 			}
// 		} catch (NumberFormatException e) {
// 			System.out.println("❌ Invalid ID format.");
// 		}
// 	}
	
// 	private void startAllSensors() {
// 		System.out.println("\n⚙️  Starting all sensors...");
// 		sensorManager.startAll();
// 		System.out.println("✅ All sensors have been started!");
// 	}
	
// 	private void stopAllSensors() {
// 		System.out.println("\n⚙️  Stopping all sensors...");
// 		sensorManager.stopAll();
// 		System.out.println("✅ All sensors have been stopped!");
// 	}
	
// 	private void enableAutomaticModeMenu() {
// 		System.out.println("\n--- Enable Automatic Mode ---");
// 		System.out.print("Enter Sensor ID: ");
// 		try {
// 			int id = Integer.parseInt(scanner.nextLine().trim());
// 			if (sensorManager.setSensorAutomaticMode(id, true)) {
// 				System.out.println("✅ Automatic mode enabled for sensor!");
// 			} else {
// 				System.out.println("❌ Sensor not found.");
// 			}
// 		} catch (NumberFormatException e) {
// 			System.out.println("❌ Invalid ID format.");
// 		}
// 	}
	
// 	private void disableAutomaticModeMenu() {
// 		System.out.println("\n--- Disable Automatic Mode ---");
// 		System.out.print("Enter Sensor ID: ");
// 		try {
// 			int id = Integer.parseInt(scanner.nextLine().trim());
// 			if (sensorManager.setSensorAutomaticMode(id, false)) {
// 				System.out.println("✅ Automatic mode disabled for sensor!");
// 			} else {
// 				System.out.println("❌ Sensor not found.");
// 			}
// 		} catch (NumberFormatException e) {
// 			System.out.println("❌ Invalid ID format.");
// 		}
// 	}
	
// 	private void enableControlModeMenu() {
// 		System.out.println("\n--- Enable Control Mode ---");
// 		System.out.print("Enter Sensor ID: ");
// 		try {
// 			int id = Integer.parseInt(scanner.nextLine().trim());
// 			System.out.print("Enter Target Value: ");
// 			double target = Double.parseDouble(scanner.nextLine().trim());
// 			System.out.print("Enter Tolerance: ");
// 			double tolerance = Double.parseDouble(scanner.nextLine().trim());
			
// 			if (sensorManager.setSensorControl(id, true, target, tolerance)) {
// 				System.out.println("✅ Control mode enabled for sensor!");
// 			} else {
// 				System.out.println("❌ Sensor not found.");
// 			}
// 		} catch (NumberFormatException e) {
// 			System.out.println("❌ Invalid input format.");
// 		}
// 	}
	
// 	private void disableControlModeMenu() {
// 		System.out.println("\n--- Disable Control Mode ---");
// 		System.out.print("Enter Sensor ID: ");
// 		try {
// 			int id = Integer.parseInt(scanner.nextLine().trim());
// 			if (sensorManager.setSensorControl(id, false, 0, 0)) {
// 				System.out.println("✅ Control mode disabled for sensor!");
// 			} else {
// 				System.out.println("❌ Sensor not found.");
// 			}
// 		} catch (NumberFormatException e) {
// 			System.out.println("❌ Invalid ID format.");
// 		}
// 	}
	
// 	private void enableAutomaticModeAll() {
// 		System.out.println("\n⚙️  Enabling automatic mode for all sensors...");
// 		sensorManager.setAllAutomaticMode(true);
// 		System.out.println("✅ Automatic mode enabled for all sensors!");
// 	}
	
// 	private void disableAutomaticModeAll() {
// 		System.out.println("\n⚙️  Disabling automatic mode for all sensors...");
// 		sensorManager.setAllAutomaticMode(false);
// 		System.out.println("✅ Automatic mode disabled for all sensors!");
// 	}
	
// 	public void printWelcomeMessage() {
// 		System.out.println("\n");
// 		System.out.println("╔════════════════════════════════════════════════════════════════════╗");
// 		System.out.println("║                                                                    ║");
// 		System.out.println("║     🏭  FACTORY AUTOMATION - SENSOR MANAGEMENT SYSTEM  🏭          ║");
// 		System.out.println("║                                                                    ║");
// 		System.out.println("║        Integrated Database-Driven Sensor Control Interface        ║");
// 		System.out.println("║                                                                    ║");
// 		System.out.println("╚════════════════════════════════════════════════════════════════════╝");
// 		System.out.println("\n✨ Welcome! All sensors are automatically loaded from the database.");
// 		System.out.println("💾 All changes are persisted to the database in real-time.\n");
// 	}
	
// 	public void exit() {
// 		System.out.println("\n🛑 Shutting down system...");
// 		System.out.println("⏳ Stopping all sensors...");
// 		sensorManager.shutdown(5);
// 		System.out.println("💾 Database disconnected.");
// 		System.out.println("✅ System shutdown complete. Goodbye! 👋\n");
// 		scanner.close();
// 		running = false;
// 	}
// }
