package org.automation.entities;

import org.automation.entities.enums.*;

public abstract class Machine extends Actuator {
    protected MachineType machineType;
    protected MachineStatus status;
    protected int latency; // based on clock ticks

    public Machine(int id, String name, MachineType machineType, double latency) {
        super(id, name, latency);
        this.machineType = machineType;
        this.status = MachineStatus.IDLE;
    }

    public Machine(int id, String name, MachineType machineType, double latency, MachineStatus status) {
        super(id, name, latency);
        this.machineType = machineType;
        this.status = status;
    }

    public boolean isAvailable() {
        return active && status == MachineStatus.IDLE;
    }

    public void start() {
        
        if (!this.isAvailable())
            return;
        status = MachineStatus.RUNNING;
    }

    public abstract void onTick(int secondsPassed);

    public void stop() {
        status = MachineStatus.STOPPED;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        stop();
    }

    @Override
    public String toString() {
        return "Machine{id=" + id + ", name=" + name + ", type=" + machineType + ", status=" + status
                + "}";
    }

    @Override
    public String toShortString() {
        return "Machine( " + name + " " + id + " )";
    }
}
