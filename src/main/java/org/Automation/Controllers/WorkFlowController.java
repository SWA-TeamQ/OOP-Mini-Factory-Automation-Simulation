package org.Automation.Controllers;

import org.Automation.entities.*;
import org.Automation.repositories.*;
import org.Automation.services.*;
import org.Automation.core.EventBus;
import org.Automation.core.Logger;
import org.Automation.engine.SimulationEngine;
import org.Automation.core.DatabaseManager;
import org.Automation.core.EntityFactory;

/**
 * Coordinates the mini factory workflow.
 * Acts as the central "Brain" that manages repositories, services, and the
 * simulation state.
 */
public class WorkFlowController {

    private final MachineRepository machineRepo;
    private final StationRepository stationRepo;
    private final ProductItemRepository productRepo;
    private final ConveyorRepository conveyorRepo;
    private final SensorRepository sensorRepo;
    private final EventBus eventBus;
    private final DatabaseManager db;

    private final IMachineService machineService;
    private final IConveyorService conveyorService;
    private final ItemTrackingService itemTrackingService;
    private final SensorService sensorService;
    private final ProductionLineService productionLineService;

    private SimulationEngine simulationEngine;
    private boolean productionRunning = false;

    /**
     * Constructor for pre-wired dependencies (used by Main).
     */
    public WorkFlowController(
            ProductionLineService productionLineService,
            StationRepository stationRepository,
            MachineRepository machineRepository,
            ProductItemRepository productItemRepository,
            ConveyorRepository conveyorRepository,
            SensorRepository sensorRepository,
            EventBus eventBus,
            DatabaseManager db) {
        this.productionLineService = productionLineService;
        this.stationRepo = stationRepository;
        this.machineRepo = machineRepository;
        this.productRepo = productItemRepository;
        this.conveyorRepo = conveyorRepository;
        this.sensorRepo = sensorRepository;
        this.eventBus = eventBus;
        this.db = db;

        this.machineService = new MachineService(machineRepo, eventBus);
        this.conveyorService = new ConveyorService(conveyorRepo, eventBus);
        this.itemTrackingService = new ItemTrackingService(productRepo, eventBus);
        this.sensorService = new SensorService(sensorRepo, eventBus);

        subscribeEvents();
        seedDataIfEmpty();
    }

    /**
     * Constructor that handles its own internal wiring (used for testing or
     * standalone).
     */
    public WorkFlowController(
            MachineRepository machineRepo,
            StationRepository stationRepo,
            ProductItemRepository productRepo,
            ConveyorRepository conveyorRepo,
            SensorRepository sensorRepo,
            EventBus eventBus,
            DatabaseManager db) {
        this.machineRepo = machineRepo;
        this.stationRepo = stationRepo;
        this.productRepo = productRepo;
        this.conveyorRepo = conveyorRepo;
        this.sensorRepo = sensorRepo;
        this.eventBus = eventBus;
        this.db = db;

        this.machineService = new MachineService(machineRepo, eventBus);
        this.conveyorService = new ConveyorService(conveyorRepo, eventBus);
        this.itemTrackingService = new ItemTrackingService(productRepo, eventBus);
        this.sensorService = new SensorService(sensorRepo, eventBus);

        this.productionLineService = new ProductionLineService(
                stationRepo,
                productRepo,
                conveyorRepo,
                itemTrackingService,
                sensorService,
                machineService,
                conveyorService,
                eventBus);

        subscribeEvents();
        seedDataIfEmpty();
    }

    private void subscribeEvents() {
        eventBus.subscribe("machine_error", payload -> Logger.error("Machine error: " + payload));
        eventBus.subscribe("machine_started", payload -> Logger.info("Machine started: " + payload));
        eventBus.subscribe("machine_stopped", payload -> Logger.info("Machine stopped: " + payload));
        eventBus.subscribe("item_completed", payload -> Logger.info("Item completed: " + payload));
    }

    private void seedDataIfEmpty() {
        if (stationRepo.findAll().isEmpty()) {
            Logger.info("Database empty. Seeding default factory layout...");

            // 1. Create Stations
            Station input = EntityFactory.createStation("INPUT", "ST-01", "ACTIVE", eventBus);
            Station prod = EntityFactory.createStation("PRODUCTION", "ST-02", "ACTIVE", eventBus);
            Station pack = EntityFactory.createStation("PACKAGING", "ST-03", "ACTIVE", eventBus);

            // 2. Create Machines
            Machine m1 = EntityFactory.createMachine("CUTTER", "M-01", "Drill Press", "IDLE", eventBus);
            Machine m2 = EntityFactory.createMachine("PACKAGER", "M-02", "Boxer", "IDLE", eventBus);

            // 3. Create Sensors
            Sensor s1 = EntityFactory.createSensor("S-01", "Temperature", 30.0, 10, eventBus);

            // 4. Create Conveyors
            ConveyorBelt c1 = EntityFactory.createConveyor("C-01", 5, 20, eventBus);
            ConveyorBelt c2 = EntityFactory.createConveyor("C-02", 5, 20, eventBus);

            // 5. Wire them up
            prod.addMachine(m1);
            prod.addSensor(s1);
            pack.addMachine(m2);

            // 6. Save to Repositories
            stationRepo.save(input);
            stationRepo.save(prod);
            stationRepo.save(pack);
            machineRepo.save(m1);
            machineRepo.save(m2);
            sensorRepo.save(s1);
            conveyorRepo.save(c1);
            conveyorRepo.save(c2);

            Logger.info("Factory seeded with 3 stations, 2 machines, 1 sensor, and 2 conveyors.");
        }
    }

    public void resetFactory() {
        if (db != null) {
            db.clearDatabase();
            stationRepo.ensureTableExists();
            machineRepo.ensureTableExists();
            productRepo.ensureTableExists();
            conveyorRepo.ensureTableExists();
            sensorRepo.ensureTableExists();
            Logger.warn("Factory reset complete. All data cleared.");
        }
    }

    public void startProduction() {
        if (productionRunning) {
            System.out.println("Production already running.");
            return;
        }
        productionRunning = true;
        simulationEngine.startSimulation();
        Logger.info("Production started.");
    }

    public void stopProduction() {
        if (!productionRunning) {
            System.out.println("No production is running.");
            return;
        }
        simulationEngine.stopSimulation();
        productionRunning = false;
        Logger.info("Production stopped.");
    }

    public void runProductionStep() {
        if (!productionRunning)
            return;

        String id = "ITEM-" + System.currentTimeMillis();
        ProductItem item = new ProductItem(id);
        productRepo.save(item);

        productionLineService.process(item);
        Logger.info("Simulation step processed item: " + item.getId());
    }

    public StationRepository getStationRepository() {
        return stationRepo;
    }

    public MachineRepository getMachineRepository() {
        return machineRepo;
    }

    public ProductItemRepository getProductItemRepository() {
        return productRepo;
    }

    public ConveyorRepository getConveyorRepository() {
        return conveyorRepo;
    }

    public SensorRepository getSensorRepository() {
        return sensorRepo;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public boolean isProductionRunning() {
        return productionRunning;
    }

    public void setSimulationEngine(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
    }
}
