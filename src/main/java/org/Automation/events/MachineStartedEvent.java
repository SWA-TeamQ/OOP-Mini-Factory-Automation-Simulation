package org.Automation.events;

public class MachineStartedEvent extends Event {
    private final String machineId;

    public MachineStartedEvent(String machineId) {
        super("MachineStartedEvent", machineId);
        this.machineId = machineId;
    }

    public String getMachineId() {
        return machineId;
    }
}
