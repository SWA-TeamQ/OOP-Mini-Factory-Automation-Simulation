package org.automation.services.interfaces;

import org.automation.entities.ProductItem;

public interface IItemTrackingService {
    void registerItem(ProductItem item);
    void markCompleted(ProductItem item);
}
