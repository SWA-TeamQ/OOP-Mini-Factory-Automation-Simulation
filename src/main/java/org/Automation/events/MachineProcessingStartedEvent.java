package org.Automation.events;

public class MachineProcessingStartedEvent extends Event {
    private final String machineId;
    private final String itemId;

    public MachineProcessingStartedEvent(String machineId, String itemId) {
        super("MachineProcessingStartedEvent", machineId);
        this.machineId = machineId;
        this.itemId = itemId;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getItemId() {
        return itemId;
    }
}
