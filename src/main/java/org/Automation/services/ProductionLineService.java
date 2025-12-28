package org.Automation.services;

import org.Automation.entities.*;
import org.Automation.repositories.*;
import org.Automation.core.EventBus;
import org.Automation.core.events.ItemMovedEvent;

import java.util.List;

public class ProductionLineService implements IProductionLineService {

    private final StationRepository stationRepo;
    private final ConveyorRepository conveyorRepo;
    private final ItemTrackingService itemTrackingService;
    private final SensorService sensorService;
    private final ActuatorService actuatorService;
    private final EventBus eventBus;
    private ProductItemRepository productRepo;

    public ProductionLineService(
            StationRepository stationRepo,
            ProductItemRepository productRepo,
            ConveyorRepository conveyorRepo,
            ItemTrackingService itemTrackingService,
            SensorService sensorService,
            ActuatorService actuatorService,
            EventBus eventBus
    ) {
        this.stationRepo = stationRepo;
        this.productRepo = productRepo;
        this.conveyorRepo = conveyorRepo;
        this.itemTrackingService = itemTrackingService;
        this.sensorService = sensorService;
        this.actuatorService = actuatorService;
        this.eventBus = eventBus;
    }
    public void startProductionCycle() {
        // For simplicity, iterate over all items in productRepo (you may extend as needed)
        productRepo.findAll().forEach(this::process);
    }
    @Override
    public void process(ProductItem item) {
        itemTrackingService.registerItem(item);

        List<Station> stations = (List<Station>) stationRepo.findAll();

        for (Station station : stations) {
            Machine machine = station.getMachine();
            Sensor sensor = station.getSensor();

            actuatorService.startMachine(machine);
            sensorService.readSensor(sensor);
            machine.process(item);
            actuatorService.stopMachine(machine);

            eventBus.publish("item_moved", new ItemMovedEvent(item.getId(), station.getId(), machine.getId()));
        }

        itemTrackingService.markCompleted(item);
    }
}
