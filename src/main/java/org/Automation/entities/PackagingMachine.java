package org.Automation.entities;

import org.Automation.entities.enums.MachineType;

public class PackagingMachine extends Machine {

    public PackagingMachine(String id) {
        super(id, MachineType.PACKAGING);
    }
}
