package org.Automation.events;

public class MachineStoppedEvent extends Event {
    private final String machineId;

    public MachineStoppedEvent(String machineId) {
        super("MachineStoppedEvent", machineId);
        this.machineId = machineId;
    }

    public String getMachineId() {
        return machineId;
    }
}
