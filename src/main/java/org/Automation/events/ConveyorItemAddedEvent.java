package org.Automation.events;

public class ConveyorItemAddedEvent extends Event {
    private final String conveyorId;
    private final String itemId;

    public ConveyorItemAddedEvent(String conveyorId, String itemId) {
        super("ConveyorItemAddedEvent", conveyorId);
        this.conveyorId = conveyorId;
        this.itemId = itemId;
    }

    public String getConveyorId() {
        return conveyorId;
    }

    public String getItemId() {
        return itemId;
    }
}
