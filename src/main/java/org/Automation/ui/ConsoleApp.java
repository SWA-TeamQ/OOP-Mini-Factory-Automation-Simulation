package org.Automation.ui;

import org.Automation.Controllers.WorkFlowController;
import org.Automation.repositories.*;
import org.Automation.core.EventBus;

public class ConsoleApp {
    private WorkFlowController controller;
     public ConsoleApp() {
        this.controller = new WorkFlowController();
    }

     MachineRepository machineRepo = new MachineRepository();
        StationRepository stationRepo = new StationRepository();
        ProductItemRepository productRepo = new ProductItemRepository();
        ConveyorRepository conveyorRepo = new ConveyorRepository();
        SensorRepository sensorRepo = new SensorRepository();

        // Create EventBus
        EventBus eventBus = new EventBus();

        // Initialize controller with all dependencies
        

     public void start() {
        ConsoleCoordinator coordinator = new ConsoleCoordinator();
        coordinator.launch(controller);
    }
}
