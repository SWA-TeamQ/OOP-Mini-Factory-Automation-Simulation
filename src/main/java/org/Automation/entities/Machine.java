package org.automation.entities;
import org.automation.entities.enums.*;

public abstract class Machine extends Actuator {
    public String type;
    public MachineStatus status;
    public String lastMaintenanceDate;

    public Machine(int id, String name, String type, MachineStatus status, String lastMaintenanceDate) {
        super(id, name);
        this.type = type;
        this.status = status;
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public abstract void onTick(int time);

    public abstract boolean isAvailable();

    @Override
    public String toString() {
        return "Machine{id=" + id + ", name=" + name + ", type=" + type + ", status=" + status + ", lastMaintenanceDate=" + lastMaintenanceDate + "}";
    }

    @Override
    public String toShortString(){
        return "Machine( " + name + " " + id + " )";
    }
}
