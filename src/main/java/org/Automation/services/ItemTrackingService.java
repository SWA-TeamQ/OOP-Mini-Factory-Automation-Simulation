package org.Automation.services;

import org.Automation.core.events.ItemMovedEvent;
import org.Automation.entities.ProductItem;
import org.Automation.entities.ItemTrackingEvent;
import org.Automation.repositories.ItemTrackingEventRepository;
import org.Automation.repositories.ProductItemRepository;
import org.Automation.core.EventBus;

public class ItemTrackingService implements IItemTrackingService {

    private final ProductItemRepository productRepo;
    private final ItemTrackingEventRepository trackingRepo;
    private final EventBus eventBus;

    public ItemTrackingService(ProductItemRepository productRepo, ItemTrackingEventRepository trackingRepo, EventBus eventBus) {
        this.productRepo = productRepo;
        this.trackingRepo = trackingRepo;
        this.eventBus = eventBus;

        // Subscribe once: whenever ProductionLineService publishes item_moved, we persist a tracking record.
        this.eventBus.subscribe("item_moved", payload -> {
            if (payload instanceof ItemMovedEvent moved) {
                onItemMoved(moved);
            }
        });
    }

    @Override
    public void registerItem(ProductItem item) {
        productRepo.save(item);
        eventBus.publish("item_registered", item);
        trackingRepo.save(new ItemTrackingEvent(
                java.time.Instant.now().toString(),
                item.getId(),
                "REGISTERED",
                null,
                null,
                "Item registered into system"
        ));
    }

    @Override
    public void markCompleted(ProductItem item) {
        item.setCompleted(true);
        productRepo.save(item);
        eventBus.publish("item_completed", item);
        trackingRepo.save(new ItemTrackingEvent(
                java.time.Instant.now().toString(),
                item.getId(),
                "COMPLETED",
                null,
                null,
                "Item marked completed"
        ));
    }

    private void onItemMoved(ItemMovedEvent moved) {
        // Optional: keep an in-memory history too (not persisted in ProductItem table in this schema)
        ProductItem item = productRepo.findById(moved.getItemId());
        if (item != null) {
            item.addHistory("Moved to station " + moved.getStationId() + " via machine " + moved.getMachineId()
                    + " at " + moved.getTimestamp());
        }

        trackingRepo.save(new ItemTrackingEvent(
                moved.getTimestamp(),
                moved.getItemId(),
                "MOVED",
                moved.getStationId(),
                moved.getMachineId(),
                "Item moved through station"
        ));
    }
}
