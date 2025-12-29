package org.Automation;

import org.Automation.Controllers.WorkFlowController;
import org.Automation.core.DatabaseManager;
import org.Automation.core.EventBus;
import org.Automation.core.Logger;
import org.Automation.engine.SimulationEngine;
import org.Automation.repositories.*;
import org.Automation.services.*;
import org.Automation.ui.ConsoleApp;

/**
 * Entry point of the Mini Factory Simulation Automation system.
 */
public class Main {

        public static void main(String[] args) {

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
                        databaseManager.clearDatabase();
                }

                // ==============================
                // 3️⃣ REPOSITORIES (DATA LAYER)
                // ==============================
                StationRepository stationRepository = new StationRepository(databaseManager, eventBus);
                MachineRepository machineRepository = new MachineRepository(databaseManager, eventBus);
                ProductItemRepository productItemRepository = new ProductItemRepository(databaseManager);
                ConveyorRepository conveyorRepository = new ConveyorRepository(databaseManager, eventBus);
                SensorRepository sensorRepository = new SensorRepository(databaseManager, eventBus);

                // ==============================
                // 4️⃣ SERVICES (BUSINESS LOGIC)
                // ==============================
                ItemTrackingService itemTrackingService = new ItemTrackingService(productItemRepository, eventBus);

                SensorService sensorService = new SensorService(sensorRepository, eventBus);

                IMachineService machineService = new MachineService(machineRepository, eventBus);

                IConveyorService conveyorService = new ConveyorService(conveyorRepository, eventBus);

                ProductionLineService productionLineService = new ProductionLineService(
                                stationRepository,
                                productItemRepository,
                                conveyorRepository,
                                itemTrackingService,
                                sensorService,
                                machineService,
                                conveyorService,
                                eventBus);

                // ==============================
                // 6️⃣ CONTROLLER (ORCHESTRATOR)
                // ==============================
                WorkFlowController controller = new WorkFlowController(
                                productionLineService,
                                stationRepository,
                                machineRepository,
                                productItemRepository,
                                conveyorRepository,
                                sensorRepository,
                                eventBus,
                                databaseManager);

                // 2. Create engine USING controller
                SimulationEngine simulationEngine = new SimulationEngine(controller);

                // 3. Inject engine back into controller
                controller.setSimulationEngine(simulationEngine);

                // ==============================
                // 7️⃣ USER INTERFACE
                // ==============================
                ConsoleApp consoleApp = new ConsoleApp(controller);

                // ==============================
                // 8️⃣ SHUTDOWN HOOK
                // ==============================
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        Logger.info("Shutting down system...");
                        simulationEngine.stopSimulation();
                        databaseManager.disconnect();
                        Logger.info("System shutdown complete.");
                }));

                // ==============================
                // 9️⃣ START APPLICATION
                // ==============================
                Logger.info("Mini Factory Simulation Automation started successfully.");
                consoleApp.start();
        }
}
