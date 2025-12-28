package org.Automation.entities;

import org.Automation.core.EventBus;
import org.Automation.entities.enums.MachineStatus;
import org.Automation.entities.enums.MachineType;

import java.util.Random;

public class Machine {

    private final String id;
    private final MachineType type;
    private MachineStatus status;
    private final Actuator actuator;
    private final EventBus eventBus;
    private final Random random = new Random();

    public Machine(String id, MachineType type, Actuator actuator, EventBus eventBus) {
        this.id = id;
        this.type = type;
        this.actuator = actuator;
        this.eventBus = eventBus;
        this.status = MachineStatus.IDLE;
    }

    public String getId() { return id; }
    public MachineType getType() { return type; }
    public MachineStatus getStatus() { return status; }
    public Actuator getActuator() { return actuator; }

    public void start() { if (status != MachineStatus.ERROR) status = MachineStatus.RUNNING; }
    public void stop() { if (status != MachineStatus.ERROR) status = MachineStatus.STOPPED; }

    public void process(ProductItem item) {
        if (status != MachineStatus.RUNNING) return;
        try { Thread.sleep(500 + random.nextInt(1000)); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        if (random.nextInt(100) < 5) {
            status = MachineStatus.ERROR;
            eventBus.publish("machine_error", this);
            return;
        }
        item.addHistory("Processed by " + type + " [" + id + "]");
    }

    public void repair() {
        if (status == MachineStatus.ERROR) {
            status = MachineStatus.IDLE;
            eventBus.publish("machine_repaired", this);
        }
    }

    @Override
    public String toString() {
        return "Machine{" + "id='" + id + '\'' + ", type=" + type + ", status=" + status + '}';
    }
}
