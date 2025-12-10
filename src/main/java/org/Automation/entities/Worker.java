package org.automation.entities;

public class Worker {
    public int id;
    public String name;
    public String status;
    public String role;
    public int assignedMachineId;
    public String shiftStatus;

    public Worker(int id, String name, String status, String role, int assignedMachineId, String shiftStatus) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.role = role;
        this.assignedMachineId = assignedMachineId;
        this.shiftStatus = shiftStatus;
    }

    @Override
    public String toString() {
        return "Worker{id=" + id + ", name=" + name + ", status=" + status + ", role=" + role + ", assignedMachineId=" + assignedMachineId + ", shiftStatus=" + shiftStatus + "}";
    }
}
