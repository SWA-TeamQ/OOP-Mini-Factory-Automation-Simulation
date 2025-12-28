package org.Automation.repositories;

import org.Automation.entities.Machine;

public class MachineRepository extends Repository<Machine> {

    @Override
    public void save(Machine machine) { add(machine.getId(), machine); }

    @Override
    public void delete(String id) { remove(id); }
}
