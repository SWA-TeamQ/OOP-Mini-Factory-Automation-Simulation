package org.automation.entities;
import org.automation.entities.abstracts.Machine;
import org.automation.entities.enums.MachineType;

public class InputMachine extends Machine{

    public InputMachine(String id) {
        super(id, MachineType.INPUT);
    }
}
