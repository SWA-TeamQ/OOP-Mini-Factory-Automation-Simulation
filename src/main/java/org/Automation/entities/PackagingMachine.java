package org.automation.entities;

import org.automation.entities.enums.MachineType;

public class PackagingMachine extends Machine {

    public PackagingMachine(String id) {
        super(id, MachineType.PACKAGING);
    }
}
