package org.Automation.services;

import org.Automation.entities.ProductItem;
import org.Automation.events.ItemMovedEvent;
import org.Automation.events.ItemRegisteredEvent;
import org.Automation.events.ItemCompletedEvent;
import org.Automation.repositories.ProductItemRepository;
import org.Automation.core.EventBus;

public class ItemTrackingService implements IItemTrackingService {

    private final ProductItemRepository productRepo;
    private final EventBus eventBus;

    public ItemTrackingService(ProductItemRepository productRepo, EventBus eventBus) {
        this.productRepo = productRepo;
        this.eventBus = eventBus;

        // Subscribe once: whenever ProductionLineService publishes item_moved, we
        // update memory state.
        this.eventBus.subscribe("item_moved", payload -> {
            if (payload instanceof ItemMovedEvent moved) {
                onItemMoved(moved);
            }
        });
    }

    @Override
    public void registerItem(ProductItem item) {
        productRepo.save(item);
        eventBus.publish("item_registered", new ItemRegisteredEvent(item.getId()));
    }

    @Override
    public void markCompleted(ProductItem item) {
        item.setCompleted(true);
        productRepo.save(item);
        eventBus.publish("item_completed", new ItemCompletedEvent(item.getId()));
    }

    private void onItemMoved(ItemMovedEvent moved) {
        // Optional: keep an in-memory history too (not persisted in ProductItem table
        // in this schema)
        ProductItem item = productRepo.findById(moved.getItemId());
        if (item != null) {
            item.addHistory("Moved to station " + moved.getStationId() + " via machine " + moved.getMachineId()
                    + " at " + moved.getTimestamp());
        }
    }
}
