package org.automation.entities;

import org.automation.entities.abstracts.Machine;

public class ProcessingMachine extends Machine {

    public ProcessingMachine(String id) {
        super(id, org.automation.entities.enums.MachineType.PROCESSING);
    }
    
}
