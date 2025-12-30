package org.automation.events.abstracts;

import org.automation.entities.ProductItem;

public class ProductSensorEvent extends Event {
    protected final ProductItem productItem;

    public ProductSensorEvent(String type, ProductItem productItem) {
        super(type);
        this.productItem = productItem;
    }

    public ProductItem getProductItem() {
        return productItem;
    }

    public String getProductId() {
        return productItem.getId();
    }
}