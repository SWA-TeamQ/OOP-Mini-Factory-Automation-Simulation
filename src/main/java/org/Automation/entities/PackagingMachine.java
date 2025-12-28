package org.Automation.entities;

import org.Automation.entities.enums.MachineType;

public class PackagingMachine extends Machine {

    public PackagingMachine(String id, Actuator actuator, org.Automation.core.EventBus eventBus) {
        super(id, MachineType.PACKAGER, actuator, eventBus);
    }

    @Override
    public void process(ProductItem item) {
        super.process(item);
        item.addHistory("Packaged by " + getId());
    }
}
