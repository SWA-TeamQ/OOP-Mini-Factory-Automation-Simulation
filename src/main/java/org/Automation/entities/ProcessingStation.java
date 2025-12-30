package org.automation.entities;

import org.automation.core.EventBus;
import org.automation.entities.abstracts.Station;
import org.automation.entities.enums.StationStatus;
import org.automation.entities.enums.StationType;
import org.automation.events.abstracts.Event;

public class ProcessingStation extends Station {

    public ProcessingStation(String id, EventBus eventBus) {
        super(id, StationType.PROCESSING, eventBus);
        this.status = StationStatus.ACTIVE;
    }

}
