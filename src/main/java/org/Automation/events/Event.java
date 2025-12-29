package org.automation.events;

import org.automation.engine.SimulationClock;

/**
 * Base class for all events in the system.
 * Contains type identifier and timestamp.
 */
public abstract class Event {
    private final String type;
    private final long tickTimestamp;

    protected Event(String type) {
        this.type = type;
        this.tickTimestamp = SimulationClock.getInstance().getLogicalTick();
    }

    public String getType() {
        return type;
    }

    public long getTickTimestamp() {
        return tickTimestamp;
    }
}
