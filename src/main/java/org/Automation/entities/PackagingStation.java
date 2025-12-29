package org.Automation.entities;

import org.Automation.entities.enums.*;
import org.Automation.core.*;
import org.Automation.events.*;

public class PackagingStation extends Station {

    public PackagingStation(String id, EventBus eventBus) {
        super(id, StationType.PACKAGING, eventBus);
        this.status = StationStatus.ACTIVE;

        // Listen for when a machine in this station completes packaging
        this.eventBus.subscribe("processing_completed", new EventSubscriber() {
            @Override
            public void onEvent(Event event) {
                if (event.getSource() instanceof ProductItem item) {
                    // Logic preserved: mark item completed if processed here.
                    // Note: ProductionLineService also handles this at the global level.
                    item.setCompleted(true);
                    eventBus.publish(new ProductEvent("item_completed", item));
                }
            }
        });
    }

    @Override
    public void processItems() {
        processQueue();
    }
}
