package org.Automation.events;

import org.Automation.entities.ProductItem;

public class ProductReadyForTransferEvent extends Event {
    private final ProductItem productItem;
    // Optional: source id (machine or station)
    private final String sourceId;

    public ProductReadyForTransferEvent(ProductItem productItem, String sourceId) {
        super("ProductReadyForTransferEvent", sourceId);
        this.productItem = productItem;
        this.sourceId = sourceId;
    }

    public ProductItem getProductItem() {
        return productItem;
    }

    public String getSourceId() {
        return sourceId;
    }
}
