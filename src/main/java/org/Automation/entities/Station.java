package org.Automation.entities;

import org.Automation.entities.enums.StationStatus;
import org.Automation.entities.enums.StationType;
import org.Automation.core.EventBus;
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
        this.eventBus.subscribe("product_ready_for_transfer", payload -> {
            // A machine finished. Since we don't strictly know if it was *our* machine
            // without lookup, we just trigger processQueue() if we have waiting items.
            // Optimization: We could check if the machine ID belongs to this station,
            // but relying on the fact that a machine finishing *might* free up capacity
            // is enough to trigger a check.
            if (!waitingQueue.isEmpty()) {
                processQueue();
            }
        });
    }

    public String getId() {
        return id;
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
        eventBus.publish("product_arrived", "Item " + item.getId() + " arrived at station " + id);
        waitingQueue.add(item); // Always enqueue first
        processQueue(); // Then try to process
    }

    /**
     * Output Gate: Called when a product is ready to leave the station.
     */
    public void onProductReadyForTransfer(ProductItem item) {
        eventBus.publish("product_ready_for_transfer", item);
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
            if (m.getStatus() == org.Automation.entities.enums.MachineStatus.IDLE) {
                ProductItem item = waitingQueue.peek(); // Start with peek logic, or just poll if we are sure
                if (m.assignItem(item)) {
                    waitingQueue.poll(); // Actually remove now
                }
            }
        }
    }

    public abstract void processItems(); // Keep abstract for legacy compatibility or simplify?
    // The prompt says "Refactor processItems". usage in Subclasses invokes logic.
    // I should probably implement the logic IN THE BASE CLASS if it's universal
    // now.
    // "Station Queue & Machine Assignment Rule... Each station must maintain a
    // product queue... When a product arrives... If any machine is IDLE...".
    // This looks like universal logic.
    // However, InputStation just creates stuff.
    // I'll keep abstract but rename helper to avoid clash? Or remove abstract?
    // Let's keep abstract and call super.processQueue() in subclasses if needed, OR
    // just move logic here.

}
