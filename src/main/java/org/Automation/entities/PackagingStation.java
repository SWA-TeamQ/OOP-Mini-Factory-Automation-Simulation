package org.Automation.entities;

import org.Automation.entities.enums.*;
import org.Automation.core.*;

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
