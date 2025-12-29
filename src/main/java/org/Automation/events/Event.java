package org.Automation.events;

import org.Automation.engine.SimulationClock;

public abstract class Event {
    private final String type;
    private final long tickTimestamp;
    private final Object source;

    public Event(String type, Object source) {
        this.type = type;
        this.source = source;
        this.tickTimestamp = SimulationClock.getInstance().getLogicalTick();
    }

    public String getType() {
        return type;
    }

    public long getTickTimestamp() {
        return tickTimestamp;
    }

    public Object getSource() {
        return source;
    }
}
