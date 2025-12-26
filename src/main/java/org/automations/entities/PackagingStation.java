package org.automations.entities;

import org.automations.entities.enums.StationStatus;

public class PackagingStation extends Station {
    public PackagingStation(String id, String name) {
        super(id, name);
    }

    @Override
    public String process(String item) {
        this.status = StationStatus.PROCESSING;
        System.out.println("📦 " + name + " is boxing: " + item);
        this.status = StationStatus.IDLE;
        return "FINAL_PRODUCT(" + item + ")";
    }
}
