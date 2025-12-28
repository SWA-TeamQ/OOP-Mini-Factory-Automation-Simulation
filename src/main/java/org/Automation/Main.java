package org.Automation;

import org.Automation.Controllers.WorkFlowController;
import org.Automation.core.DatabaseManager;
import org.Automation.core.EventBus;
import org.Automation.core.Logger;
import org.Automation.engine.SimulationClock;
import org.Automation.engine.SimulationEngine;
import org.Automation.repositories.*;
import org.Automation.services.*;
import org.Automation.ui.ConsoleApp;

/**
 * Entry point of the Mini Factory Simulation Automation system.
 * 
 * This class is responsible for:
 *  - Bootstrapping core infrastructure
 *  - Wiring dependencies manually (no framework)
 *  - Starting the UI
 *  - Graceful shutdown
 */
public class Main {

    public static void main(String[] args) {

        // ==============================
        // 1️⃣ SYSTEM BOOTSTRAP
        // ==============================
        Logger.info("Starting Mini Factory Simulation Automation...");

        // ==============================
        // 2️⃣ CORE INFRASTRUCTURE
        // ==============================
        EventBus eventBus = new EventBus();
        DatabaseManager databaseManager = new DatabaseManager();

        // ==============================
        // 3️⃣ REPOSITORIES (DATA LAYER)
        // ==============================
        StationRepository stationRepository = new StationRepository();
        MachineRepository machineRepository = new MachineRepository();
        ProductItemRepository productItemRepository = new ProductItemRepository();
        ConveyorRepository conveyorRepository = new ConveyorRepository();
        SensorRepository sensorRepository = new SensorRepository();

        // ==============================
        // 4️⃣ SERVICES (BUSINESS LOGIC)
        // ==============================
        ItemTrackingService itemTrackingService =
                new ItemTrackingService(productItemRepository, eventBus);

        SensorService sensorService =
                new SensorService(sensorRepository, eventBus);

        ActuatorService actuatorService =
                new ActuatorService(machineRepository, eventBus);

        ProductionLineService productionLineService =
                new ProductionLineService(
                        stationRepository,
                        conveyorRepository,
                        itemTrackingService,
                        sensorService,
                        actuatorService,
                        eventBus
                );

        // ==============================
        // 5️⃣ SIMULATION ENGINE
        // ==============================
        SimulationClock simulationClock = new SimulationClock();
        SimulationEngine simulationEngine =
                new SimulationEngine(simulationClock, productionLineService);

        // ==============================
        // 6️⃣ CONTROLLER (ORCHESTRATOR)
        // ==============================
        WorkFlowController workFlowController =
                new WorkFlowController(
                        simulationEngine,
                        productionLineService,
                        stationRepository,
                        machineRepository
                );

        // ==============================
        // 7️⃣ USER INTERFACE
        // ==============================
        ConsoleApp consoleApp =
                new ConsoleApp();

        // ==============================
        // 8️⃣ SHUTDOWN HOOK
        // ==============================
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.info("Shutting down system...");
            simulationEngine.stop();
            databaseManager.shutdown();
            Logger.info("System shutdown complete.");
        }));

        // ==============================
        // 9️⃣ START APPLICATION
        // ==============================
        consoleApp.start();

        Logger.info("Mini Factory Simulation Automation started successfully.");
    }
}
