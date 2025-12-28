package org.Automation.entities;

import org.Automation.core.EventBus;

public class Sensor {
    private String id;

    public Sensor(String id) {
        this.id = id;
    }
    public String getId(){
        return id;
    }

    public void trigger(String event, Object payload) {
        EventBus.publish(event, payload);
    }
}
