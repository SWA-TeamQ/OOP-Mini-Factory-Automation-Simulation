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
import org.Automation.repositories.ItemTrackingEventRepository;

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
        Logger.clearLog();
        Logger.info("Starting Mini Factory Simulation Automation...");

        // ==============================
        // 2️⃣ CORE INFRASTRUCTURE
        // ==============================
        EventBus eventBus = new EventBus();
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();

        // Fresh Start Check
        if (args.length > 0 && args[0].equalsIgnoreCase("--fresh")) {
            Logger.warn("Fresh start requested. Clearing database...");
            try (java.sql.Statement stmt = databaseManager.getConnection().createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS Station");
                stmt.execute("DROP TABLE IF EXISTS Machine");
                stmt.execute("DROP TABLE IF EXISTS ProductItem");
                stmt.execute("DROP TABLE IF EXISTS Sensor");
                stmt.execute("DROP TABLE IF EXISTS ConveyorBelt");
            } catch (java.sql.SQLException e) {
                Logger.error("Failed to clear database: " + e.getMessage());
            }
        }

        // ==============================
        // 3️⃣ REPOSITORIES (DATA LAYER)
        // ==============================
        StationRepository stationRepository = new StationRepository(databaseManager);
        MachineRepository machineRepository = new MachineRepository(databaseManager, eventBus);
        ProductItemRepository productItemRepository = new ProductItemRepository(databaseManager);
        ConveyorRepository conveyorRepository = new ConveyorRepository(databaseManager);
        SensorRepository sensorRepository = new SensorRepository(databaseManager, eventBus);
        ItemTrackingEventRepository itemTrackingEventRepository = new ItemTrackingEventRepository(databaseManager);

        // ==============================
        // 4️⃣ SERVICES (BUSINESS LOGIC)
        // ==============================
        ItemTrackingService itemTrackingService =
                new ItemTrackingService(productItemRepository, itemTrackingEventRepository, eventBus);

        SensorService sensorService =
                new SensorService(sensorRepository, eventBus);

        ActuatorService actuatorService =
                new ActuatorService(machineRepository, eventBus);

        ProductionLineService productionLineService =
                new ProductionLineService(
                        stationRepository,
                        productItemRepository,
                        conveyorRepository,
                        itemTrackingService,
                        sensorService,
                        actuatorService,
                        eventBus
                );


        // ==============================
        // 6️⃣ CONTROLLER (ORCHESTRATOR)
        // ==============================
        // Create controller using already-wired services (single source of truth).
        WorkFlowController controller = new WorkFlowController(
                productionLineService,
                stationRepository,
                machineRepository,
                productItemRepository,
                conveyorRepository,
                sensorRepository,
                eventBus,
                databaseManager
        );

// 2. Create engine USING controller
SimulationEngine simulationEngine = new SimulationEngine(controller);

// 3. Inject engine back into controller
controller.setSimulationEngine(simulationEngine);

        
        // ==============================
        // 7️⃣ USER INTERFACE
        // ==============================
        ConsoleApp consoleApp =
                new ConsoleApp(controller);

        // ==============================
        // 8️⃣ SHUTDOWN HOOK
        // ==============================
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.info("Shutting down system...");
            simulationEngine.stopSimulation();
            databaseManager.shutdown();
            Logger.info("System shutdown complete.");
        }));

        // ==============================
        // 9️⃣ START APPLICATION
        // ==============================
        
        Logger.info("Mini Factory Simulation Automation started successfully.");

        consoleApp.start();
    }
}
