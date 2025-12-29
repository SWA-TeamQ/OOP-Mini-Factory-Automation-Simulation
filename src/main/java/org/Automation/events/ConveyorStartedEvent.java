package org.Automation.events;

public class ConveyorStartedEvent extends Event {
    private final String conveyorId;

    public ConveyorStartedEvent(String conveyorId) {
        super("ConveyorStartedEvent", conveyorId);
        this.conveyorId = conveyorId;
    }

    public String getConveyorId() {
        return conveyorId;
    }
}
