package org.Automation.entities;

import org.Automation.entities.enums.StationType;
import org.Automation.entities.enums.StationStatus;
import org.Automation.core.EventBus;

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
