package org.automation.events;

import org.automation.entities.ProductItem;
import org.automation.events.abstracts.ProductEvent;

/**
 * Raised when a product completes the entire production line.
 */
public class ProductFinishedEvent extends ProductEvent {
    private final long completionTick;
    private final long totalDuration;

    public ProductFinishedEvent(ProductItem productItem, long completionTick, long totalDuration) {
        super("ProductFinishedEvent", productItem);
        this.completionTick = completionTick;
        this.totalDuration = totalDuration;
    }

    public long getCompletionTick() {
        return completionTick;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    @Override
    public String toString() {
        return "Product " + productItem.getId() + " finished at tick " + completionTick +
                " with total duration " + totalDuration;
    }
}
