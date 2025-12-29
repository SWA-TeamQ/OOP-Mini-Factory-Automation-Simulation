package org.automation.events;

/**
 * Raised when a product completes the entire production line.
 */
public class ProductFinishedEvent extends Event {
    private final String productId;
    private final long completionTick;
    private final long totalDuration;

    public ProductFinishedEvent(String productId, long completionTick, long totalDuration) {
        super("ProductFinishedEvent");
        this.productId = productId;
        this.completionTick = completionTick;
        this.totalDuration = totalDuration;
    }

    public String getProductId() {
        return productId;
    }

    public long getCompletionTick() {
        return completionTick;
    }

    public long getTotalDuration() {
        return totalDuration;
    }
}
