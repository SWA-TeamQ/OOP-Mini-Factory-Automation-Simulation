package org.automation.entities;

import org.automation.core.EventBus;
import org.automation.entities.enums.StationStatus;
import org.automation.entities.enums.StationType;

public class ProductionStation extends Station {

    public ProductionStation(String id, EventBus eventBus) {
        super(id, StationType.PROCESSING, eventBus);
        this.status = StationStatus.ACTIVE;
    }

    @Override
    public void processItems() {
        processQueue();
    }
}
