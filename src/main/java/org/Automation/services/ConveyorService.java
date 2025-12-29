package org.Automation.services;

import org.Automation.entities.*;
import org.Automation.repositories.*;
import org.Automation.core.*;
import org.Automation.events.*;

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
            eventBus.publish(new ConveyorStartedEvent(conveyorId));
            Logger.info("Conveyor " + conveyorId + " started.");
        }
    }

    @Override
    public void stopConveyor(String conveyorId) {
        ConveyorBelt conveyor = conveyorRepo.findById(conveyorId);
        if (conveyor != null) {
            eventBus.publish(new ConveyorStoppedEvent(conveyorId));
            Logger.info("Conveyor " + conveyorId + " stopped.");
        }
    }

    public void moveItem(String conveyorId, ConveyorBelt conveyor, ProductItem item) {
        // Logic to move item on conveyor
        if (conveyor.addItem(item)) {
            // Event is already published in ConveyorBelt.addItem()
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
