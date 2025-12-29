package org.Automation.entities;

import org.Automation.entities.enums.StationStatus;
import org.Automation.core.EventBus;

public class PackagingStation extends Station {

    public PackagingStation(String id, EventBus eventBus) {
        super(id, eventBus);
        this.status = StationStatus.ACTIVE;
        
        // Listen for when a machine in this station completes packaging
        this.eventBus.subscribe("processing_completed", payload -> {
            if (payload instanceof ProductItem item) {
                // If we were the ones processing it (this is a bit simplified)
                // In a real system, we'd check if the machine belongs to this station
                item.setCompleted(true);
                eventBus.publish("item_completed", item);
            }
        });
    }

    @Override
    public void processItems() {
        for (ProductItem item : new java.util.ArrayList<>(itemsInStation)) {
            for (Machine machine : machines) {
                if (machine.assignItem(item)) {
                    itemsInStation.remove(item);
                    break;
                }
            }
        }
    }
}
