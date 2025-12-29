package org.Automation.entities;

import org.Automation.entities.enums.StationStatus;
import org.Automation.core.EventBus;

public class InputStation extends Station {

    public InputStation(String id, EventBus eventBus) {
        super(id, eventBus);
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
        for (ProductItem item : new java.util.ArrayList<>(itemsInStation)) {
            onProductReadyForTransfer(item);
            itemsInStation.remove(item);
        }
    }
}
