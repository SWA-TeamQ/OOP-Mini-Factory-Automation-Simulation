package org.automations.entities;// StationStatus.java

import org.automations.entities.enums.StationStatus;

// Station.java (The "ABC" equivalent)
public abstract class Station {
    protected String stationId;
    protected String name;
    protected StationStatus status;

    public Station(String stationId, String name) {
        this.stationId = stationId;
        this.name = name;
        this.status = StationStatus.IDLE;
    }

    // This is the "Abstract Method" - subclasses MUST implement this
    public abstract String process(String item);

    public String getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
    }

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }
}