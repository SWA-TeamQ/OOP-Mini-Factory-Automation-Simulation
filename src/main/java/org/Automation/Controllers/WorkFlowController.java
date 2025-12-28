package org.Automation.Controllers;

import org.Automation.entities.ProductItem;
import org.Automation.entities.Station;
import org.Automation.entities.Machine;
import org.Automation.repositories.*;
import org.Automation.services.*;
import org.Automation.core.EventBus;
import org.Automation.core.Logger;
import org.Automation.engine.SimulationEngine;
import org.Automation.core.DatabaseManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Coordinates the mini factory workflow with queue-based production.
 */
public class WorkFlowController {

    private final MachineRepository machineRepo;
    private final StationRepository stationRepo;
    private final ProductItemRepository productRepo;
    private final ConveyorRepository conveyorRepo;
    private final SensorRepository sensorRepo;
    private final EventBus eventBus;
    private final DatabaseManager db;

    private final ActuatorService actuatorService;
    private final ItemTrackingService itemTrackingService;
    private final SensorService sensorService;
    private final ProductionLineService productionLineService;
    private SimulationEngine simulationEngine;

    private final Queue<ProductItem> productionQueue = new LinkedList<>();
    private boolean productionRunning = false;

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

        this.actuatorService = new ActuatorService(machineRepo, eventBus);
        this.itemTrackingService = new ItemTrackingService(productRepo, eventBus);
        this.sensorService = new SensorService(sensorRepo, eventBus);

        this.productionLineService = new ProductionLineService(
                stationRepo,
                productRepo,
                conveyorRepo,
                itemTrackingService,
                sensorService,
                actuatorService,
                eventBus
        );

        subscribeEvents();
        // Create default sensors
    sensorService.createSensor("TEMP_SENSOR_01", "Temperature Sensor");
    sensorService.createSensor("WEIGHT_SENSOR_01", "Weight Sensor");

    }


    private void subscribeEvents() {
        eventBus.subscribe("machine_error", payload -> Logger.error("Machine error: " + payload));
        eventBus.subscribe("machine_started", payload -> Logger.info("Machine started: " + payload));
        eventBus.subscribe("machine_stopped", payload -> Logger.info("Machine stopped: " + payload));
        eventBus.subscribe("item_completed", payload -> Logger.info("Item completed: " + payload));
    }

    // -------------------- Production Methods --------------------

    /**
     * Start production with a list of user-provided items.
     */
   public void startProduction() {
    if (productionRunning) {
        System.out.println("Production already running.");
        return;
    }

    enqueueUserProducts();

    if (productionQueue.isEmpty()) {
        System.out.println("No products to process. Add items first.");
        return;
    }

    productionRunning = true;

    if (simulationEngine != null) simulationEngine.startSimulation();

    Logger.info("Production started with " + productionQueue.size() + " items.");
}



    /**
     * Stop production manually.
     */
    public void stopProduction() {
        if (!productionRunning) {
            System.out.println("No production is running.");
            return;
        }

        if (simulationEngine != null) {
            simulationEngine.stopSimulation();
        }
        productionRunning = false;
        productionQueue.clear();

        Logger.info("Production stopped.");
    }

    /**
     * Returns whether production is currently running.
     */
    public boolean isProductionRunning() {
        return productionRunning;
    }

    /**
     * Process one item from the queue (called per simulation tick).
     */
    /**
 * Process one item from the queue (called per simulation tick).
 */
public void runProductionStep() {
    if (!productionRunning) return;

    ProductItem item = productionQueue.poll();
if (item == null) { stopProduction(); return; }

productionLineService.process(item);

// Read sensors for this product
double temp = sensorService.readSensor(item.getTempSensorId());
double weight = sensorService.readSensor(item.getWeightSensorId());

System.out.println(
    "Product " + item.getId() +
    " processed: Temp=" + String.format("%.2f", temp) +
    "°C, Weight=" + String.format("%.2f", weight) + "kg"
);

Logger.info("Simulation step processed item: " + item.getId());
eventBus.publish("item_completed", item);

    System.out.println("Product " + item.getId() + " processed: Temp=" + temp + "°C, Weight=" + weight + "kg");

    Logger.info("Simulation step processed item: " + item.getId());
    eventBus.publish("item_completed", item);
}




    /**
     * Set the simulation engine instance.
     */
    public void setSimulationEngine(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
    }

    // -------------------- Utility Methods --------------------

    public void addStation(Station station) {
        stationRepo.save(station);
    }

    public void addMachine(Machine machine) {
        machineRepo.save(machine);
    }

    public void addProductItem(ProductItem item) {
        productRepo.save(item);
    }

    // Add these back inside WorkFlowController
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

public void addProductItemFromUser(String id) {
    // Assign default sensors to new product
    ProductItem item = new ProductItem(id, "TEMP_SENSOR_01", "WEIGHT_SENSOR_01");
    productRepo.save(item);
    Logger.info("User added item: " + id);
}


public void enqueueUserProducts() {
    List<ProductItem> items = productRepo.findAll(); // get all user-added products
    productionQueue.addAll(items);
}



}
