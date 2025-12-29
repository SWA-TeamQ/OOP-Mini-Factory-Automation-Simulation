package org.Automation.entities;

import org.Automation.core.EventBus;
import org.Automation.core.Tickable;
import org.Automation.entities.enums.MachineStatus;
import org.Automation.entities.enums.MachineType;
import org.Automation.engine.SimulationClock;

import java.util.Random;

/**
 * Represents a physical machine in the factory.
 * Driven by the SimulationClock (Time-Driven) and controlled via commands (Event-Driven).
 */
public class Machine implements Tickable {

    private final String id;
    private final MachineType type;
    private MachineStatus status;
    private final EventBus eventBus;
    private final Random random = new Random();

    // State for time-driven processing
    private ProductItem currentItem;
    private int processingTicksRemaining = 0;
    private int totalProcessingTicks = 20; // Default: 1 second (20 ticks * 50ms)

    public Machine(String id, MachineType type, EventBus eventBus) {
        this.id = id;
        this.type = type;
        this.eventBus = eventBus;
        this.status = MachineStatus.IDLE;
        
        // Register with the central clock
        SimulationClock.getInstance().registerTickable(this);
    }

    public String getId() { return id; }
    public MachineType getType() { return type; }
    public MachineStatus getStatus() { return status; }
    public void setStatus(MachineStatus status) { this.status = status; }

    // Commands (Event-Driven)
    public void start() { 
        if (status == MachineStatus.STOPPED || status == MachineStatus.IDLE) {
            status = MachineStatus.RUNNING; 
            eventBus.publish("machine_started", id);
        }
    }

    public void stop() { 
        if (status != MachineStatus.ERROR) {
            status = MachineStatus.STOPPED; 
            eventBus.publish("machine_stopped", id);
        }
    }

    /**
     * Assigns an item to the machine to begin processing.
     */
    public boolean assignItem(ProductItem item) {
        if (status == MachineStatus.RUNNING && currentItem == null) {
            this.currentItem = item;
            this.processingTicksRemaining = totalProcessingTicks + random.nextInt(10); // Add some variability
            eventBus.publish("processing_started", "Machine " + id + " started processing " + item.getId());
            return true;
        }
        return false;
    }

    @Override
    public void tick(long currentTick) {
        if (status != MachineStatus.RUNNING || currentItem == null) {
            return;
        }

        // Simulate potential failure
        if (random.nextInt(1000) < 2) { // 0.2% chance per tick
            status = MachineStatus.ERROR;
            eventBus.publish("machine_error", "Machine " + id + " failed during processing of " + currentItem.getId());
            return;
        }

        processingTicksRemaining--;

        if (processingTicksRemaining <= 0) {
            completeProcessing();
        }
    }

    private void completeProcessing() {
        currentItem.addHistory("Processed by " + type + " [" + id + "] at tick " + SimulationClock.getInstance().getLogicalTick());
        eventBus.publish("processing_completed", currentItem);
        eventBus.publish("product_ready_for_transfer", id); // Signal to station/conveyor
        this.currentItem = null;
    }

    public void repair() {
        if (status == MachineStatus.ERROR) {
            status = MachineStatus.IDLE;
            eventBus.publish("machine_repaired", id);
        }
    }

    @Override
    public String toString() {
        return "Machine{" + "id='" + id + '\'' + ", type=" + type + ", status=" + status + 
               (currentItem != null ? ", processing=" + currentItem.getId() : "") + '}';
    }
}
