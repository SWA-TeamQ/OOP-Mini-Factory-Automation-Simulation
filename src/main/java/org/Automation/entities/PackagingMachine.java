package org.Automation.entities;

import org.Automation.entities.enums.MachineType;
import org.Automation.core.EventBus;

public class PackagingMachine extends Machine {

    public PackagingMachine(String id, EventBus eventBus) {
        super(id, MachineType.PACKAGING, eventBus);
    }
}
