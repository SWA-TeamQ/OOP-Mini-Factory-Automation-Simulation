package org.Automation.entities;

import org.Automation.entities.enums.StationStatus;


public class PackagingStation extends Station {
    private Machine packagingMachine;

    public PackagingStation(String id, Machine packagingMachine) {
        super(id);
        this.packagingMachine = packagingMachine;
        this.status = StationStatus.ACTIVE;
    }

    @Override
    public void processItems() {
        for (ProductItem item : itemsInStation) {
            packagingMachine.process(item);
            item.setCompleted(true);
        }
    }
}
