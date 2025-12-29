package org.Automation.events;

public class ConveyorStoppedEvent extends Event {
    private final String conveyorId;

    public ConveyorStoppedEvent(String conveyorId) {
        super("ConveyorStoppedEvent", conveyorId);
        this.conveyorId = conveyorId;
    }

    public String getConveyorId() {
        return conveyorId;
    }
}
