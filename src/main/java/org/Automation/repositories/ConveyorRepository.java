package org.Automation.repositories;

import org.Automation.entities.ConveyorBelt;

public class ConveyorRepository extends Repository<ConveyorBelt> {

    @Override
    public void save(ConveyorBelt conveyor) { add(conveyor.getId(), conveyor); }

    @Override
    public void delete(String id) { remove(id); }
}
