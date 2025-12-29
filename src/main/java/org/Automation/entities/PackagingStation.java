package org.automation.entities;

import org.automation.core.*;
import org.automation.entities.enums.*;

public class PackagingStation extends Station {

    public PackagingStation(String id, EventBus eventBus) {
        super(id, StationType.PACKAGING, eventBus);
        this.status = StationStatus.ACTIVE;
    }

    @Override
    public void processItems() {
        processQueue();
    }
}
