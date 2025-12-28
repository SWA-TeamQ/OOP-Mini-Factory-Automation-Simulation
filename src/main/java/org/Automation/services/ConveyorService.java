package org.Automation.services;

import org.Automation.entities.ConveyorBelt;
import org.Automation.entities.ProductItem;
import org.Automation.repositories.ConveyorRepository;
import org.Automation.core.EventBus;
import org.Automation.core.Logger;

public class ConveyorService implements IConveyorService {
    private final ConveyorRepository conveyorRepo;
    private final EventBus eventBus;

    public ConveyorService(ConveyorRepository conveyorRepo, EventBus eventBus) {
        this.conveyorRepo = conveyorRepo;
        this.eventBus = eventBus;
    }

    @Override
    public void startConveyor(String conveyorId) {
        ConveyorBelt conveyor = conveyorRepo.findById(conveyorId);
        if (conveyor != null) {
            // Logic to start conveyor (e.g. setting a status if added later)
            eventBus.publish("conveyor_started", conveyorId);
            Logger.info("Conveyor " + conveyorId + " started.");
        }
    }

    @Override
    public void stopConveyor(String conveyorId) {
        ConveyorBelt conveyor = conveyorRepo.findById(conveyorId);
        if (conveyor != null) {
            eventBus.publish("conveyor_stopped", conveyorId);
            Logger.info("Conveyor " + conveyorId + " stopped.");
        }
    }

    public void moveItem(String conveyorId, ConveyorBelt conveyor, ProductItem item) {
        // Logic to move item on conveyor
        if (conveyor.addItem(item)) {
            eventBus.publish("item_on_conveyor", "Item " + item.getId() + " moved to conveyor " + conveyorId);
        }
    }

    @Override
    public void moveItem(String conveyorId, ProductItem item) {
        ConveyorBelt conveyor = conveyorRepo.findById(conveyorId);
        if (conveyor != null) {
            moveItem(conveyorId, conveyor, item);
        }
    }

    @Override
    public ConveyorBelt getConveyor(String conveyorId) {
        return conveyorRepo.findById(conveyorId);
    }
}
