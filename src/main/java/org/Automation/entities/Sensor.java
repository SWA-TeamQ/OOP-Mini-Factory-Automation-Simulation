package org.Automation.entities;

import org.Automation.core.EventBus;

public class Sensor {
    private String id;
    private final EventBus eventBus;

    public Sensor(String id, EventBus eventBus) {
        this.id = id;
        this.eventBus = eventBus;
    }
    public String getId(){
        return id;
    }

    public void trigger(String event, Object payload) {
        eventBus.publish(event, payload);
    }
}
