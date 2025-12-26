package org.automations.entities;

import org.automations.entities.enums.StationStatus;

public class ProductionStation extends Station {
    public ProductionStation(String id, String name) {
        super(id, name);
    }

    @Override
    public String process(String item) {
        this.status = StationStatus.PROCESSING;
        System.out.println("⚙️ " + name + " is machining: " + item);
        // Simulate work
        String processedItem = item + " [Machined]";
        this.status = StationStatus.IDLE;
        return processedItem;
    }
}
