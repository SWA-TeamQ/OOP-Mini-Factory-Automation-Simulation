package org.Automation.entities.enums;

public enum MachineType {
    CUTTER(StationType.PROCESSING),
    ASSEMBLER(StationType.PROCESSING),
    PACKAGER(StationType.PACKAGING),
    INSPECTOR(StationType.PROCESSING);

    private final StationType stationType;

    MachineType(StationType stationType) {
        this.stationType = stationType;
    }

    public StationType getStationType() {
        return stationType;
    }
}
