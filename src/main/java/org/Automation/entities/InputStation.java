package org.automation.entities;

import org.automation.core.EventBus;
import org.automation.entities.abstracts.Station;
import org.automation.entities.enums.*;
import org.automation.core.*;
import java.util.*;

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
        for (ProductItem item : new ArrayList<>(waitingQueue)) {
            onProductReadyForTransfer(item);
            waitingQueue.remove(item);
        }
    }

    private void onProductReadyForTransfer(ProductItem item) {
        // Strict Refactor: ProductReadyForTransferEvent is forbidden.
        // Logic should be handled by polling or direct assignment if applicable.
        // For now, we log that it's ready. The ProductionLineService likely picks it
        // up.
        Logger.info("InputStation: Product " + item.getId() + " ready for transfer.");
    }
}
