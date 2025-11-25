package org.Automation.entities;

public class ConveyorBelt {
    public int id;
    public String name;
    public int length;
    public String status;
    public int startMachineId;
    public int endMachineId;
    public double speed;

    public ConveyorBelt(int id, String name, int length, String status, double speed, int startMachineId,
            int endMachineId) {
        this.id = id;
        this.name = name;
        this.length = length;
        this.status = status;
        this.speed = speed;
        this.startMachineId = startMachineId;
        this.endMachineId = endMachineId;
    }

    @Override
    public String toString() {
        return "ConveyorBelt{id=" + id + ", name=" + name + ", status=" + status + ", speed=" + speed + "}";
    }

}
