package org.Automation.entities;

import org.Automation.entities.enums.StationStatus;
import org.Automation.entities.enums.MachineStatus;

public class ProductionStation extends Station {
    private Machine machine;

    public ProductionStation(String id, Machine machine) {
        super(id);
        this.machine = machine;
        this.status = StationStatus.ACTIVE;
    }

    @Override
    public void processItems() {
        if (machine.getStatus() == MachineStatus.RUNNING) {
            for (ProductItem item : itemsInStation) {
                machine.process(item);
            }
        }
    }
}
