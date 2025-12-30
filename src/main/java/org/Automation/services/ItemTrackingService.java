package org.automation.services;

import org.automation.core.*;
import org.automation.core.interfaces.EventSubscriber;
import org.automation.engine.SimulationClock;
import org.automation.entities.*;
import org.automation.events.*;
import org.automation.events.abstracts.Event;
import org.automation.repositories.*;
import org.automation.services.interfaces.IItemTrackingService;

/**
 * Tracks product items through the production line.
 * Listens to ProductArrivedEvent and ProductFinishedEvent.
 * Manages lifecycle data (startTick, endTick) in the database.
 */
public class ItemTrackingService implements IItemTrackingService {

    private final ProductItemRepository productRepo;
    private final EventBus eventBus;

    public ItemTrackingService(ProductItemRepository productRepo, EventBus eventBus) {
        this.productRepo = productRepo;
        this.eventBus = eventBus;

        subscribeToEvents();
    }

    private void subscribeToEvents() {
        // Listen for product arrivals to track movements
        eventBus.subscribe("ProductArrivedEvent", new EventSubscriber() {
            @Override
            public void onEvent(Event event) {
                if (event instanceof ProductArrivedEvent arrivedEvent) {
                    onProductArrived(arrivedEvent);
                }
            }
        });

        // Listen for product finished to update completion data
        eventBus.subscribe("ProductFinishedEvent", new EventSubscriber() {
            @Override
            public void onEvent(Event event) {
                if (event instanceof ProductFinishedEvent finishedEvent) {
                    onProductFinished(finishedEvent);
                }
            }
        });
    }

    /**
     * Registers a new item and sets its start tick.
     * Called when an item first enters the production line.
     */
    @Override
    public void registerItem(ProductItem item) {
        long currentTick = SimulationClock.getInstance().getLogicalTick();
        item.setStartTick(currentTick);
        productRepo.save(item);
        Logger.info("Item " + item.getId() + " registered at tick " + currentTick);

        // Display initial sensor values (pre-production)
        Logger.info(String.format("[Pre-Production] Product %s | Initial Temperature: %.2f C",
                item.getId(), item.getTemperature()));
        Logger.info(String.format("[Pre-Production] Product %s | Initial Weight: %.2f kg",
                item.getId(), item.getWeight()));
    }

    /**
     * Marks an item as completed, sets end tick and saves to DB.
     */
    @Override
    public void markCompleted(ProductItem item) {
        long currentTick = SimulationClock.getInstance().getLogicalTick();
        item.setEndTick(currentTick);
        item.setCompleted(true);
        productRepo.save(item);

        // Publish the finished event
        eventBus.publish(new ProductFinishedEvent(
                item.getId(),
                currentTick,
                item.getTotalDuration()));

        Logger.info(String.format("[Tick %d] [UI] Product %s finished full production cycle. Total Duration: %d ticks.",
                currentTick, item.getId(), item.getTotalDuration()));
    }

    /**
     * Marks an item as defective (for machine failure scenarios).
     */
    public void markDefective(ProductItem item) {
        item.setDefective(true);
        item.setEndTick(SimulationClock.getInstance().getLogicalTick());
        productRepo.save(item);
        Logger.error("Item " + item.getId() + " marked as DEFECTIVE");
    }

    private void onProductArrived(ProductArrivedEvent event) {
        ProductItem item = productRepo.findById(event.getProductId());
        if (item != null) {
            item.addHistory("Arrived at station " + event.getStationId() + " at tick " + event.getTickTimestamp());
            Logger.info("Tracking: Item " + event.getProductId() + " arrived at " + event.getStationId());
        }
    }

    private void onProductFinished(ProductFinishedEvent event) {
        Logger.info("Tracking: Item " + event.getProductId() + " finished. Total duration: " + event.getTotalDuration()
                + " ticks");
    }
}
