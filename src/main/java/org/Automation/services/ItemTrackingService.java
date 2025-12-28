package org.Automation.services;

import org.Automation.entities.ProductItem;
import org.Automation.repositories.ProductItemRepository;
import org.Automation.core.EventBus;

public class ItemTrackingService implements IItemTrackingService {

    private final ProductItemRepository productRepo;
    private final EventBus eventBus;

    public ItemTrackingService(ProductItemRepository productRepo, EventBus eventBus) {
        this.productRepo = productRepo;
        this.eventBus = eventBus;
    }

    @Override
    public void registerItem(ProductItem item) {
        productRepo.save(item);
        eventBus.publish("item_registered", item);
    }

    @Override
    public void markCompleted(ProductItem item) {
        item.setCompleted(true);
        eventBus.publish("item_completed", item);
    }
}
