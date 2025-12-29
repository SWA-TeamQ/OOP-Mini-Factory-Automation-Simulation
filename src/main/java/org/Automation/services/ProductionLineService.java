package org.Automation.services;

import org.Automation.entities.*;
import org.Automation.repositories.*;
import org.Automation.core.EventBus;
import org.Automation.core.Logger;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Orchestrates the flow of products through the production line.
 * Hybrid: Time-Driven (via Clock) and Event-Driven (via EventBus).
 */
public class ProductionLineService implements IProductionLineService {

    private final StationRepository stationRepo;
    private final ConveyorRepository conveyorRepo;
    private final ItemTrackingService itemTrackingService;
    private final SensorService sensorService;
    private final IMachineService machineService;
    private final IConveyorService conveyorService;
    private final EventBus eventBus;
    private final ProductItemRepository productRepo;

    // Mapping to define the physical flow: Station ID -> Next Station ID
    private final Map<String, String> flowMap = new HashMap<>();
    
    // Mapping to track which item is currently where (for logical routing)
    private final Map<String, String> itemLocationMap = new HashMap<>();

    public ProductionLineService(
            StationRepository stationRepo,
            ProductItemRepository productRepo,
            ConveyorRepository conveyorRepo,
            ItemTrackingService itemTrackingService,
            SensorService sensorService,
            IMachineService machineService,
            IConveyorService conveyorService,
            EventBus eventBus
    ) {
        this.stationRepo = stationRepo;
        this.productRepo = productRepo;
        this.conveyorRepo = conveyorRepo;
        this.itemTrackingService = itemTrackingService;
        this.sensorService = sensorService;
        this.machineService = machineService;
        this.conveyorService = conveyorService;
        this.eventBus = eventBus;

        setupEventHandlers();
    }

    private void setupEventHandlers() {
        // When a machine finishes, the station signals the product is ready for transfer
        eventBus.subscribe("product_ready_for_transfer", payload -> {
            if (payload instanceof ProductItem item) {
                handleTransfer(item);
            }
        });

        // When a conveyor delivers an item, it enters the next station
        eventBus.subscribe("product_delivered", payload -> {
            if (payload instanceof ProductItem item) {
                handleArrival(item);
            }
        });
    }

    @Override
    public void process(ProductItem item) {
        itemTrackingService.registerItem(item);
        
        // Start the item at the first station
        List<Station> stations = stationRepo.findAll();
        if (!stations.isEmpty()) {
            Station firstStation = stations.get(0);
            itemLocationMap.put(item.getId(), firstStation.getId());
            
            // Ensure machines are running
            for (Machine m : firstStation.getMachines()) {
                machineService.startMachine(m.getId());
            }
            
            firstStation.onProductArrived(item);
            Logger.info("Item " + item.getId() + " entered the production line at " + firstStation.getId());
        }
    }

    private void handleTransfer(ProductItem item) {
        String currentStationId = itemLocationMap.get(item.getId());
        if (currentStationId == null) return;

        Station currentStation = stationRepo.findById(currentStationId);
        if (currentStation == null) return;

        // Determine next station
        List<Station> stations = stationRepo.findAll();
        int currentIndex = -1;
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getId().equals(currentStationId)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex >= 0 && currentIndex < stations.size() - 1) {
            Station nextStation = stations.get(currentIndex + 1);
            moveItemViaConveyor(item, currentStation, nextStation);
        } else {
            // Last station reached
            itemTrackingService.markCompleted(item);
            itemLocationMap.remove(item.getId());
            Logger.info("Item " + item.getId() + " has completed the production line.");
        }
    }

    private void moveItemViaConveyor(ProductItem item, Station from, Station to) {
        List<ConveyorBelt> conveyors = conveyorRepo.findAll();
        if (!conveyors.isEmpty()) {
            ConveyorBelt conveyor = conveyors.get(0); // Simplified: use first conveyor
            from.removeItem(item);
            itemLocationMap.put(item.getId(), "CONVEYOR_" + conveyor.getId() + "_TO_" + to.getId());
            conveyor.addItem(item);
            Logger.info("Moving item " + item.getId() + " from " + from.getId() + " to " + to.getId() + " via conveyor " + conveyor.getId());
        }
    }

    private void handleArrival(ProductItem item) {
        // Find where this item was supposed to go
        String transitId = itemLocationMap.get(item.getId());
        if (transitId != null && transitId.contains("_TO_")) {
            String nextStationId = transitId.substring(transitId.lastIndexOf("_TO_") + 4);
            Station nextStation = stationRepo.findById(nextStationId);
            if (nextStation != null) {
                itemLocationMap.put(item.getId(), nextStation.getId());
                
                // Ensure machines are running
                for (Machine m : nextStation.getMachines()) {
                    machineService.startMachine(m.getId());
                }
                
                nextStation.onProductArrived(item);
                Logger.info("Item " + item.getId() + " arrived at station " + nextStation.getId());
            }
        }
    }
}
