package org.automations.ui;

import org.automations.Controllers.WorkFlowController;
import org.automations.core.DatabaseManager;
import org.automations.core.Logger;
import org.automations.engine.SimulationEngine;
import org.automations.repositories.*;
import org.automations.services.*;

public class ConsoleApp extends ConsoleUI {
  // Instance Variables
  private SimulationEngine simulationEngine;
  private WorkFlowController controller;
  private Logger logger;
  private boolean isRunning = true;

  public ConsoleApp(DatabaseManager db) {
    this.logger = new Logger(db);

    StationRepository stationRepo = new StationRepository(db);
    MachineRepository machineRepo = new MachineRepository(db);
    SensorRepository sensorRepo = new SensorRepository(db);
    ProductItemRepository itemRepo = new ProductItemRepository(db);

    IProductionLineService lineService = new ProductionLineService();
    IItemTrackingService trackingService = new ItemTrackingService();
    ISensorService sensorService = new SensorService();
    IActuatorService actuatorService = new ActuatorService();

    this.controller = new WorkFlowController(lineService, sensorService, actuatorService, trackingService);
    this.simulationEngine = new SimulationEngine();
  }

  // Instance Methods
  public void start() {
    simulationEngine.startSimulation();
    printWelcomeMessage();
    runMainMenu();
  }

  public void runMainMenu() {
    while (isRunning) {
      printHeader("MINI-FACTORY CONTROL CENTER");
      System.out.println("1. Start Simulation");
      System.out.println("2. Stop Simulation");
      System.out.println("3. View Factory Status");
      System.out.println("4. Exit System");
      System.out.println("\nSelect an Option: ");

      handleUserInput(getUserInput());
    }
  }

  public void handleUserInput(String input) {
    switch (input) {
      case "1":
        simulationEngine.startSimulation();
        System.out.println("\n[SYSTEM]: Simulation is now RUNNING.");
        break;
      case "2":
        simulationEngine.stopSimulation();
        System.out.println("\n[SYSTEM]: Simulation has been PAUSED.");
        break;
      case "3":
        // This would call a view helper to show current items/machines
        System.out.println("\n--- Current Factory Snapshot ---");
        simulationEngine.printSimulationStatus();
        break;
      case "4":
        exit();
        break;
      default:
        System.out.println("\n[ERROR]: Invalid option. Please try again.");
        break;
    }
  }

  public void printWelcomeMessage() {
    printHeader("AUTOMATION SIMULATION v1.0");
    System.out.println("System initialized successfully.");
    System.out.println("Connected to Database: factory_sim.db");
    System.out.println("Logger active: simulation.log");
    System.out.println("---------------------------------------");
  }

  public void exit() {
    System.out.println("\nShutting down system...");
    simulationEngine.stopSimulation();
    isRunning = false;
    System.out.println("System shutdown complete. Goodbye!");
  }
}
