package org.Automation.entities;

import org.Automation.core.EventBus;

public class Sensor {
    private String id;
    private String type;
    private String machineId; // optional: if sensor is tied to a machine
    private String status; // optional: active, inactive
    private double currentValue; // stores current reading

    public Sensor(String id, String type) {
        this.id = id;
        this.type = type;
        this.status = "active";
        this.currentValue = 0.0;
    }

    // getters & setters
    public String getId() { return id; }
    public String getType() { return type; }
    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double value) { this.currentValue = value; }
    public void setStatus(String Status){this.status=Status;}
}
