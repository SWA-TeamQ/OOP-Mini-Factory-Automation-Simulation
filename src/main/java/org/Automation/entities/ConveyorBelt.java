package org.Automation.entities;

import org.Automation.engine.Tickable;
import org.Automation.engine.SimulationClock;
import org.Automation.core.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Represents a physical conveyor belt connecting two stations.
 * Moves products over a defined transfer duration (Time-Driven).
 * Uses callback for delivery notification instead of events.
 */
public class ConveyorBelt implements Tickable {
    private final String id;
    private final int capacity;
    private final int transferDurationTicks;

    // Callback for when an item is delivered
    private Consumer<ProductItem> onItemDelivered;

    // Internal class to track an item in transit
    private static class TransitItem {
        ProductItem item;
        long arrivalTick;

        TransitItem(ProductItem item, long arrivalTick) {
            this.item = item;
            this.arrivalTick = arrivalTick;
        }
    }

    private final List<TransitItem> itemsInTransit = new ArrayList<>();

    public ConveyorBelt(String id, int capacity, int transferDurationTicks) {
        this.id = id;
        this.capacity = capacity;
        this.transferDurationTicks = transferDurationTicks;

        // Register with the central clock
        SimulationClock.getInstance().registerTickable(this);
    }

    public String getId() {
        return id;
    }

    /**
     * Sets the callback for when an item is delivered.
     */
    public void setOnItemDelivered(Consumer<ProductItem> callback) {
        this.onItemDelivered = callback;
    }

    /**
     * Adds an item to the conveyor.
     * Calculates the arrival tick based on the current tick + transfer duration.
     */
    public boolean addItem(ProductItem item) {
        if (itemsInTransit.size() < capacity) {
            long currentTick = SimulationClock.getInstance().getLogicalTick();
            itemsInTransit.add(new TransitItem(item, currentTick + transferDurationTicks));
            Logger.info("Item " + item.getId() + " placed on conveyor " + id);
            return true;
        }
        return false;
    }

    @Override
    public void tick(long currentTick) {
        Iterator<TransitItem> iterator = itemsInTransit.iterator();
        while (iterator.hasNext()) {
            TransitItem transit = iterator.next();
            if (currentTick >= transit.arrivalTick) {
                deliverItem(transit.item);
                iterator.remove();
            }
        }
    }

    private void deliverItem(ProductItem item) {
        Logger.info("Item " + item.getId() + " delivered by conveyor " + id);
        if (onItemDelivered != null) {
            onItemDelivered.accept(item);
        }
    }

    public boolean isEmpty() {
        return itemsInTransit.isEmpty();
    }

    public int getInTransitCount() {
        return itemsInTransit.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTransferDurationTicks() {
        return transferDurationTicks;
    }
}
