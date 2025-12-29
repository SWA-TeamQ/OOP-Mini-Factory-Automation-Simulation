package org.Automation.events;

public class MachineErrorEvent extends Event {
    private final String machineId;
    private final String errorMessage;

    public MachineErrorEvent(String machineId, String errorMessage) {
        super("MachineErrorEvent", machineId);
        this.machineId = machineId;
        this.errorMessage = errorMessage;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
