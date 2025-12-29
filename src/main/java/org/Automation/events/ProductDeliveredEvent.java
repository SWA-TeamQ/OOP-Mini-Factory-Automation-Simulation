package org.Automation.events;

import org.Automation.entities.ProductItem;

public class ProductDeliveredEvent extends Event {
    private final ProductItem productItem;

    public ProductDeliveredEvent(ProductItem productItem) {
        super("ProductDeliveredEvent", productItem);
        this.productItem = productItem;
    }

    public ProductItem getProductItem() {
        return productItem;
    }
}
