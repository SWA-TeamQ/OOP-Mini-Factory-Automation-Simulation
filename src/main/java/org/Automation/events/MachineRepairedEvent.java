package org.Automation.events;

/**
 * Event published when a machine has been repaired and is back to IDLE status.
 */
public class MachineRepairedEvent extends Event {
    private final String machineId;

    public MachineRepairedEvent(String machineId) {
        super("MachineRepairedEvent", machineId);
        this.machineId = machineId;
    }

    public String getMachineId() {
        return machineId;
    }
}
