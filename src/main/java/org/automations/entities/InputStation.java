package org.automations.entities;

import org.automations.entities.enums.StationStatus;

public class InputStation extends Station {
    public InputStation(String id, String name) {
        super(id, name);
    }

    @Override
    public String process(String item) {
        this.status = StationStatus.PROCESSING;
        System.out.println("📥 " + name + " receiving: " + item);
        this.status = StationStatus.IDLE;
        return item;
    }
}
