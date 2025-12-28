package org.Automation.services;

import org.Automation.entities.ProductItem;

public interface IItemTrackingService {
    void registerItem(ProductItem item);
    void markCompleted(ProductItem item);
}
