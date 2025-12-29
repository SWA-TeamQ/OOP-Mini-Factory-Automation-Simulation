package org.Automation.entities;

import org.Automation.entities.enums.StationType;
import org.Automation.entities.enums.StationStatus;
import org.Automation.core.EventBus;

public class InputStation extends Station {

    public InputStation(String id, EventBus eventBus) {
        super(id, StationType.INPUT, eventBus);
        this.status = StationStatus.ACTIVE;
    }

    // Produces new items into the production line
    public ProductItem createItem(String productId) {
        ProductItem newItem = new ProductItem(productId);
        onProductArrived(newItem);
        return newItem;
    }

    @Override
    public void processItems() {
        // InputStation just produces items, then signals they are ready for transfer
        // Note: waitingQueue is protected in base class
        for (ProductItem item : new java.util.ArrayList<>(waitingQueue)) {
            onProductReadyForTransfer(item);
            waitingQueue.remove(item);
        }
    }
}
