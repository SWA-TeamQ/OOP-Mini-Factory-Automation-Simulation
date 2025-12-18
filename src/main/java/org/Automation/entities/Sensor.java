package org.automation.entities;

public abstract class Sensor {
    public int id;
    public String type;
    public int machineId;
    public String status;

    public Sensor(int id, String type, int machineId, String status) {
        this.id = id;
        this.type = type;
        this.machineId = machineId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Sensor{id=" + id + ", type=" + type + ", machineId=" + machineId + ", status=" + status + "}";
    }
}
