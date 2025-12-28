package org.Automation.services;

import org.Automation.entities.*;
import org.Automation.repositories.*;
import org.Automation.core.EventBus;

import java.util.List;

public class ProductionLineService implements IProductionLineService {

    private final StationRepository stationRepo;
    private final ConveyorRepository conveyorRepo;
    private final ItemTrackingService itemTrackingService;
    private final SensorService sensorService;
    private final IMachineService machineService;
    private final IConveyorService conveyorService;
    private final EventBus eventBus;
    private ProductItemRepository productRepo;

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
    }

    public void startProductionCycle() {
        productRepo.findAll().forEach(this::process);
    }

    @Override
    public void process(ProductItem item) {
        itemTrackingService.registerItem(item);

        List<Station> stations = stationRepo.findAll();

        for (Station station : stations) {
            Machine machine = station.getMachine();
            Sensor sensor = station.getSensor();

            if (machine != null) {
                machineService.startMachine(machine.getId());
                sensorService.readSensor(sensor);
                machineService.processItem(machine.getId(), item);
                machineService.stopMachine(machine.getId());
            }

            eventBus.publish("item_moved", item);
        }

        itemTrackingService.markCompleted(item);
    }
}
