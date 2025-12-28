package org.Automation.Controllers;

import org.Automation.entities.*;
import org.Automation.repositories.*;
import org.Automation.services.*;
import org.Automation.core.EventBus;
import org.Automation.core.Logger;
import org.Automation.engine.SimulationClock;
import org.Automation.engine.SimulationEngine;
import org.Automation.core.DatabaseManager;
import org.Automation.repositories.ItemTrackingEventRepository;

import java.util.List;

/**
 * Coordinates the mini factory workflow.
 */
public class WorkFlowController {

    private  MachineRepository machineRepo;
    private  StationRepository stationRepo;
    private  ProductItemRepository productRepo;
    private  ConveyorRepository conveyorRepo;
    private  SensorRepository sensorRepo;
    private  EventBus eventBus;
    private  DatabaseManager db;

    private IMachineService machineService;
    private IConveyorService conveyorService;
    private ItemTrackingService itemTrackingService;
    private SensorService sensorService;
    private ProductionLineService productionLineService;
    private SimulationEngine simulationEngine;
    private SimulationClock simulationClock;
    private boolean productionRunning = false;

    public WorkFlowController(
            ProductionLineService productionLineService,
            StationRepository stationRepository,
            MachineRepository machineRepository,
            ProductItemRepository productItemRepository,
            ConveyorRepository conveyorRepository,
            SensorRepository sensorRepository,
            EventBus eventBus,
            DatabaseManager db
    ) {
        this.productionLineService = productionLineService;
        this.stationRepo = stationRepository;
        this.machineRepo = machineRepository;
        this.productRepo = productItemRepository;
        this.conveyorRepo = conveyorRepository;
        this.sensorRepo = sensorRepository;
        this.eventBus = eventBus;
        this.db = db;
        subscribeEvents();
    }

    public void resetFactory() {
        if (db != null) {
            db.clearDatabase();
            // Re-create tables
            stationRepo.ensureTableExists();
            machineRepo.ensureTableExists();
            productRepo.ensureTableExists();
            conveyorRepo.ensureTableExists();
            sensorRepo.ensureTableExists();
            Logger.warn("Factory reset complete. All data cleared.");
        }
    }

    public WorkFlowController(
            MachineRepository machineRepo,
            StationRepository stationRepo,
            ProductItemRepository productRepo,
            ConveyorRepository conveyorRepo,
            SensorRepository sensorRepo,
            EventBus eventBus,
            DatabaseManager db
    ) {
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
                eventBus
        );

        subscribeEvents();
    }

    private void subscribeEvents() {
        eventBus.subscribe("machine_error", payload -> Logger.error("Machine error: " + payload));
        eventBus.subscribe("machine_started", payload -> Logger.info("Machine started: " + payload));
        eventBus.subscribe("machine_stopped", payload -> Logger.info("Machine stopped: " + payload));
        eventBus.subscribe("item_completed", payload -> Logger.info("Item completed: " + payload));
    }

    /**
     * Starts the production workflow for a list of product items.
     */
    public void startProduction(List<ProductItem> items) {
        Logger.info("Starting production of " + items.size() + " items");

        for (ProductItem item : items) {
            productionLineService.process(item);
            Logger.info("Finished processing item: " + item.getId());
        }

        Logger.info("Production run completed");
    }

    /**
     * Add a station to the factory
     */
    public void addStation(Station station) {
        stationRepo.save(station);
    }

    /**
     * Add a machine to the factory
     */
    public void addMachine(Machine machine) {
        machineRepo.save(machine);
    }

    /**
     * Add a product item
     */
    public void addProductItem(ProductItem item) {
        productRepo.save(item);
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

public void startProduction() {
    if (productionRunning) {
        System.out.println("Production already running.");
        return;
    }

    productionRunning = true;
    simulationEngine.startSimulation();
    Logger.info("Production started.");
}





/**
 * Stop the simulation.
 */
public void stopProduction() {
    if (!productionRunning) {
        System.out.println("No production is running.");
        return;
    }

    simulationEngine.stopSimulation();
    productionRunning = false;

    Logger.info("Production stopped.");
}


public boolean isProductionRunning() {
    return productionRunning;
}

public void runProductionStep() {
    if (!productionRunning) {
        return;
    }

    String id = "ITEM-" + System.currentTimeMillis();

    ProductItem item = new ProductItem(id);
    productRepo.save(item);

    productionLineService.process(item);

    Logger.info("Simulation step processed item: " + item.getId());
}

public void setSimulationEngine(SimulationEngine simulationEngine) {
    this.simulationEngine = simulationEngine;
}

}
