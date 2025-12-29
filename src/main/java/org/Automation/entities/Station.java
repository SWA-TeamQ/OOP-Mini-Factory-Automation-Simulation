package org.Automation.entities;

import org.Automation.entities.enums.*;
import org.Automation.core.*;
import org.Automation.events.*;
import java.util.*;

/**
 * Represents a physical location in the production line.
 * Orchestrates machines and publishes product events.
 */
public abstract class Station {
    protected final String id;
    protected final StationType type;
    protected StationStatus status;
    protected final Queue<ProductItem> waitingQueue = new LinkedList<>();
    protected final List<Machine> machines = new ArrayList<>();
    protected final EventBus eventBus;

    public Station(String id, StationType type, EventBus eventBus) {
        this.id = id;
        this.type = type;
        this.eventBus = eventBus;
        this.status = StationStatus.INACTIVE;
    }

    public String getId() {
        return id;
    }

    public StationType getType() {
        return type;
    }

    public void addMachine(Machine machine) {
        if (machine.getType().getStationType() != this.type) {
            throw new IllegalArgumentException(
                    "Station " + id + " (" + type + ") cannot accept machine " + machine.getType());
        }
        machines.add(machine);
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }

    /**
     * Input Gate: Called when a product enters the station.
     * Publishes ProductArrivedEvent and tries to process queue.
     */
    public void onProductArrived(ProductItem item) {
        eventBus.publish(new ProductArrivedEvent(item.getId(), id));
        waitingQueue.add(item);
        processQueue();
    }

    public List<ProductItem> getItems() {
        return new ArrayList<>(waitingQueue);
    }

    public void removeItem(ProductItem item) {
        waitingQueue.remove(item);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", machines=" + machines.size() +
                ", waiting=" + waitingQueue.size() +
                ", status=" + status +
                '}';
    }

    /**
     * Tries to assign waiting items to idle machines.
     * Publishes ProductAssignedToMachineEvent when assignment happens.
     */
    protected void processQueue() {
        if (waitingQueue.isEmpty())
            return;

        for (Machine m : machines) {
            if (waitingQueue.isEmpty())
                break;

            if (m.getStatus() == MachineStatus.IDLE) {
                ProductItem item = waitingQueue.peek();
                if (m.assignItem(item)) {
                    waitingQueue.poll();
                    // Station publishes the assignment event
                    eventBus.publish(new ProductAssignedToMachineEvent(item.getId(), m.getId()));
                }
            }
        }
    }

    public void processItems() {
        processQueue();
    }
}
