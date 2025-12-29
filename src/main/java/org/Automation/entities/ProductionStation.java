package org.Automation.entities;

import org.Automation.entities.enums.StationStatus;
import org.Automation.core.EventBus;

public class ProductionStation extends Station {

    public ProductionStation(String id, EventBus eventBus) {
        super(id, eventBus);
        this.status = StationStatus.ACTIVE;
    }

    @Override
    public void processItems() {
        // Try to assign items to available machines
        for (ProductItem item : new java.util.ArrayList<>(itemsInStation)) {
            for (Machine machine : machines) {
                if (machine.assignItem(item)) {
                    itemsInStation.remove(item);
                    break; // Item assigned, move to next item
                }
            }
        }
    }
}
