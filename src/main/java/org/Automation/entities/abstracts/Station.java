package org.automation.entities.abstracts;

import java.util.*;

import org.automation.core.*;
import org.automation.core.interfaces.EventSubscriber;
import org.automation.entities.ProductItem;
import org.automation.entities.enums.*;
import org.automation.events.*;

/**
 * Represents a physical location in the production line.
 * Orchestrates machines and publishes product events.
 */
public abstract class Station implements EventSubscriber {
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

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public void addMachine(Machine machine) {
        if (machine.getType().getStationType() != this.type) {
            Logger.error("Station " + id + " (" + type + ") cannot accept machine " + machine.getType());
            throw new IllegalArgumentException(
                    "Station " + id + " (" + type + ") cannot accept machine " + machine.getType());
        }
        machines.add(machine);
    }

    public List<ProductItem> getItems() {
        return new ArrayList<>(waitingQueue);
    }

    public void removeItem(ProductItem item) {
        waitingQueue.remove(item);
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

    public void onEvent(ProductArrivedEvent event) {
        String productId = event.getProductId();
        waitingQueue.add(ProductItemRepository.getProductById(productId));

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
