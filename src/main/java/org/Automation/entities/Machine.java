package org.automation.entities;

public abstract class Machine {
    public int id;
    public String name;
    public String type;
    public String status;
    public String lastMaintenanceDate;

    public Machine(int id, String name, String type, String status, String lastMaintenanceDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    @Override
    public String toString() {
        return "Machine{id=" + id + ", name=" + name + ", type=" + type + ", status=" + status + ", lastMaintenanceDate=" + lastMaintenanceDate + "}";
    }
}
