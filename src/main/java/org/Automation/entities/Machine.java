package org.Automation.entities;

import org.Automation.core.*;
import org.Automation.engine.*;
import org.Automation.entities.enums.*;
import org.Automation.repositories.ProductItemRepository;

import java.util.Random;

/**
 * Represents a physical machine in the factory.
 * Driven by the SimulationClock (Time-Driven).
 * On failure: stops simulation, marks product defective, terminates program.
 */
public class Machine implements Tickable {

    private final String id;
    private final MachineType type;
    private MachineStatus status;
    private final Random random = new Random();

    // State for time-driven processing
    private ProductItem currentItem;
    private int processingTicksRemaining = 0;
    private int totalProcessingTicks = 20; // Default: 1 second (20 ticks * 50ms)

    // Callback for when processing completes (set by Station/Service)
    private Runnable onProcessingComplete;

    // Reference to repository for marking defective items (injected via setter)
    private static ProductItemRepository productItemRepository;

    public Machine(String id, MachineType type) {
        this.id = id;
        this.type = type;
        this.status = MachineStatus.IDLE;

        // Register with the central clock
        SimulationClock.getInstance().registerTickable(this);
    }

    public static void setProductItemRepository(ProductItemRepository repo) {
        productItemRepository = repo;
    }

    public String getId() {
        return id;
    }

    public MachineType getType() {
        return type;
    }

    public MachineStatus getStatus() {
        return status;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public ProductItem getCurrentItem() {
        return currentItem;
    }

    public void setOnProcessingComplete(Runnable callback) {
        this.onProcessingComplete = callback;
    }

    public void start() {
        if (status == MachineStatus.STOPPED) {
            status = MachineStatus.IDLE;
            Logger.info("Machine " + id + " started.");
        }
    }

    public void stop() {
        if (status != MachineStatus.ERROR) {
            status = MachineStatus.STOPPED;
            Logger.info("Machine " + id + " stopped.");
        }
    }

    /**
     * Assigns an item to the machine to begin processing.
     * Does not publish events - Station handles that.
     */
    public boolean assignItem(ProductItem item) {
        if (status == MachineStatus.IDLE && currentItem == null) {
            MachineStatus oldStatus = this.status;
            this.currentItem = item;
            this.status = MachineStatus.PROCESSING;
            this.processingTicksRemaining = totalProcessingTicks + random.nextInt(10);

            // UI Feedback: Status Change & Start Processing
            long tick = SimulationClock.getInstance().getLogicalTick();
            Logger.info(String.format("[Tick %d] Machine %s status changed: %s -> %s (Product: %s)",
                    tick, id, oldStatus, this.status, item.getId()));

            return true;
        }
        return false;
    }

    @Override
    public void tick(long currentTick) {
        if (status != MachineStatus.PROCESSING || currentItem == null) {
            return;
        }

        // Simulate potential failure (0.2% chance per tick)
        if (random.nextInt(1000) < 2) {
            handleFailure();
            return;
        }

        processingTicksRemaining--;

        if (processingTicksRemaining <= 0) {
            completeProcessing();
        }
    }

    /**
     * Machine failure: stop simulation, mark product defective, terminate.
     */
    private void handleFailure() {
        MachineStatus oldStatus = this.status;
        this.status = MachineStatus.ERROR;
        // ... logging ...
        String errorMessage = "MACHINE FAILURE: Machine " + id + " failed during processing of " + currentItem.getId();

        // 1. Log error to file
        Logger.error(errorMessage);
        Logger.error(String.format("[Tick %d] Machine %s status changed: %s -> %s (Product: %s)",
                SimulationClock.getInstance().getLogicalTick(), id, oldStatus, this.status, currentItem.getId()));

        System.err.println(errorMessage);

        // 2. Mark current product as DEFECTIVE
        if (productItemRepository != null && currentItem != null) {
            currentItem.setDefective(true);
            currentItem.setEndTick(SimulationClock.getInstance().getLogicalTick());
            productItemRepository.save(currentItem);
            Logger.error("Product " + currentItem.getId() + " marked as DEFECTIVE in database.");
        }

        // 3. Stop the simulation clock
        SimulationClock.getInstance().stop();

        // 4. Terminate the program cleanly
        Logger.error("Simulation terminated due to machine failure.");
        System.err.println("Simulation terminated due to machine failure. Check simulation.log for details.");
        System.exit(1);
    }

    private void completeProcessing() {
        ProductItem finishedItem = this.currentItem;
        long currentTick = SimulationClock.getInstance().getLogicalTick();
        MachineStatus oldStatus = this.status;

        // State update FIRST
        this.status = MachineStatus.IDLE;
        this.currentItem = null;

        // UI Feedback: Status Change & Late Log
        Logger.info(String.format("[Tick %d] Machine %s status changed: %s -> %s (Product: %s)",
                currentTick, id, oldStatus, this.status, finishedItem.getId()));

        finishedItem.addHistory(
                "Processed by " + type + " [" + id + "] at tick " + currentTick);

        // Notify via callback (no events)
        if (onProcessingComplete != null) {
            onProcessingComplete.run();
        }
    }

    @Override
    public String toString() {
        return "Machine{" + "id='" + id + '\'' + ", type=" + type + ", status=" + status +
                (currentItem != null ? ", processing=" + currentItem.getId() : "") + '}';
    }
}
