package org.Automation.entities;

import org.Automation.engine.Tickable;
import org.Automation.engine.SimulationClock;
import org.Automation.core.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Represents a physical conveyor belt connecting two stations.
 * Moves products over a defined transfer duration (Time-Driven).
 */
public class ConveyorBelt implements Tickable {
    private final String id;
    private final int capacity;
    private final int transferDurationTicks; // How many ticks to move from start to end

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
    private final EventBus eventBus;

    public ConveyorBelt(String id, int capacity, int transferDurationTicks, EventBus eventBus) {
        this.id = id;
        this.capacity = capacity;
        this.transferDurationTicks = transferDurationTicks;
        this.eventBus = eventBus;

        // Register with the central clock
        SimulationClock.getInstance().registerTickable(this);
    }

    public String getId() {
        return id;
    }

    /**
     * Adds an item to the conveyor.
     * Calculates the arrival tick based on the current tick + transfer duration.
     */
    public boolean addItem(ProductItem item) {
        if (itemsInTransit.size() < capacity) {
            long currentTick = SimulationClock.getInstance().getLogicalTick();
            itemsInTransit.add(new TransitItem(item, currentTick + transferDurationTicks));
            eventBus.publish(new org.Automation.events.ConveyorItemAddedEvent(id, item.getId()));
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
                // Item has reached the end of the conveyor
                deliverItem(transit.item);
                iterator.remove();
            }
        }
    }

    private void deliverItem(ProductItem item) {
        eventBus.publish(new org.Automation.events.ProductDeliveredEvent(item));
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
