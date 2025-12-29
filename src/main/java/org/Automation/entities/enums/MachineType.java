package org.Automation.entities.enums;

public enum MachineType {
    INPUT(StationType.INPUT),
    PROCESSING(StationType.PROCESSING),
    PACKAGING(StationType.PACKAGING);

    private final StationType stationType;

    MachineType(StationType stationType) {
        this.stationType = stationType;
    }

    public StationType getStationType() {
        return stationType;
    }
}
