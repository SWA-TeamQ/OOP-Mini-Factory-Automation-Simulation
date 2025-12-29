package org.Automation.entities;

import org.Automation.entities.enums.*;
import org.Automation.core.*;
import org.Automation.events.*;
import java.util.*;

/**
 * Represents a physical location in the production line.
 * Orchestrates machines and sensors (Event-Driven).
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

        // Subscribe to machine availability events to drive the queue
        this.eventBus.subscribe("ProductReadyForTransferEvent", new EventSubscriber() {
            @Override
            public void onEvent(Event payload) {
                // A machine finished. Since we don't strictly know if it was *our* machine
                // without lookup, we just trigger processQueue() if we have waiting items.
                if (!waitingQueue.isEmpty()) {
                    processQueue();
                }
            }
        });
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
     */
    public void onProductArrived(ProductItem item) {
        eventBus.publish(new org.Automation.events.ProductArrivedAtStationEvent(id, item.getId()));
        waitingQueue.add(item); // Always enqueue first
        processQueue(); // Then try to process
    }

    /**
     * Output Gate: Called when a product is ready to leave the station.
     */
    public void onProductReadyForTransfer(ProductItem item) {
        eventBus.publish(new org.Automation.events.ProductReadyForTransferEvent(item, id));
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

    protected void processQueue() {
        if (waitingQueue.isEmpty())
            return;

        // Try to assign head of queue to any IDLE machine
        // Limit: process one item per free machine
        java.util.Iterator<Machine> machineIt = machines.iterator();
        while (machineIt.hasNext() && !waitingQueue.isEmpty()) {
            Machine m = machineIt.next();
            if (m.getStatus() == MachineStatus.IDLE) {
                ProductItem item = waitingQueue.peek(); // Start with peek logic, or just poll if we are sure
                if (m.assignItem(item)) {
                    waitingQueue.poll(); // Actually remove now
                }
            }
        }
    }

    public void processItems() {
        processQueue();
    }
}
