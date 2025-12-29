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
 * 
 * WHAT THIS SERVICE DOES:
 * 1. Receives new products and starts them at the first station
 * 2. Tracks which station each product is currently at
 * 3. When a machine finishes processing, moves the product to the next station
 * via conveyor
 * 4. When a product reaches the last station and finishes, marks it as
 * completed
 * 
 * FLOW:
 * Product -> Station 1 -> Conveyor -> Station 2 -> Conveyor -> Station 3 ->
 * Completed
 */
public class ProductionLineService implements IProductionLineService {

    private final StationRepository stationRepo;
    private final ConveyorRepository conveyorRepo;
    private final ItemTrackingService itemTrackingService;
    private final IMachineService machineService;
    private final ProductItemRepository productRepo;

    // Tracks which station each product is currently at
    private final Map<String, String> itemLocationMap = new HashMap<>();

    public ProductionLineService(
            StationRepository stationRepo,
            ProductItemRepository productRepo,
            ConveyorRepository conveyorRepo,
            ItemTrackingService itemTrackingService,
            SensorService sensorService,
            IMachineService machineService,
            IConveyorService conveyorService,
            EventBus eventBus) {
        this.stationRepo = stationRepo;
        this.productRepo = productRepo;
        this.conveyorRepo = conveyorRepo;
        this.itemTrackingService = itemTrackingService;
        this.machineService = machineService;

        setupConveyorCallbacks();
        setupMachineCallbacks();
    }

    /**
     * Sets up conveyor delivery callbacks.
     * When a conveyor delivers an item, we handle the arrival at the next station.
     */
    private void setupConveyorCallbacks() {
        for (ConveyorBelt conveyor : conveyorRepo.findAll()) {
            conveyor.setOnItemDelivered(item -> handleArrival(item));
        }
    }

    /**
     * Sets up machine completion callbacks.
     * When a machine finishes, we transfer the product to the next station.
     */
    private void setupMachineCallbacks() {
        for (Station station : stationRepo.findAll()) {
            for (Machine machine : station.getMachines()) {
                final Station currentStation = station;
                machine.setOnProcessingComplete(() -> {
                    ProductItem item = machine.getCurrentItem();
                    if (item == null) {
                        // Item was already handled (completed processing)
                        // Check waiting queue for this station
                        if (!currentStation.getItems().isEmpty()) {
                            handleTransferFromStation(currentStation);
                        }
                    }
                });
            }
        }
    }

    /**
     * Entry point: Start processing a new product.
     * Registers the item, places it at the first station.
     */
    @Override
    public void process(ProductItem item) {
        itemTrackingService.registerItem(item);

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

    /**
     * Handles transfer when a station signals a product is ready to move.
     */
    private void handleTransferFromStation(Station currentStation) {
        List<ProductItem> items = currentStation.getItems();
        if (items.isEmpty())
            return;

        for (ProductItem item : items) {
            handleTransfer(item);
        }
    }

    /**
     * Moves a product from its current station to the next via conveyor.
     */
    private void handleTransfer(ProductItem item) {
        String currentStationId = itemLocationMap.get(item.getId());
        if (currentStationId == null)
            return;

        Station currentStation = stationRepo.findById(currentStationId);
        if (currentStation == null)
            return;

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
            // Last station reached - mark completed
            itemTrackingService.markCompleted(item);
            itemLocationMap.remove(item.getId());
            Logger.info("Item " + item.getId() + " has completed the production line.");
        }
    }

    /**
     * Places an item on a conveyor to move it to the next station.
     */
    private void moveItemViaConveyor(ProductItem item, Station from, Station to) {
        List<ConveyorBelt> conveyors = conveyorRepo.findAll();
        if (!conveyors.isEmpty()) {
            ConveyorBelt conveyor = conveyors.get(0); // Simplified: use first conveyor
            from.removeItem(item);
            itemLocationMap.put(item.getId(), "CONVEYOR_TO_" + to.getId());
            conveyor.addItem(item);
            Logger.info("Moving item " + item.getId() + " from " + from.getId() + " to " + to.getId());
        }
    }

    /**
     * Handles item arrival at next station after conveyor delivery.
     */
    private void handleArrival(ProductItem item) {
        String transitId = itemLocationMap.get(item.getId());
        if (transitId != null && transitId.contains("CONVEYOR_TO_")) {
            String nextStationId = transitId.replace("CONVEYOR_TO_", "");
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
