package org.automation.events.abstracts;
import org.automation.entities.ProductItem;

public class ProductEvent extends Event{
    protected final ProductItem productItem;

    public ProductEvent(String type, ProductItem productItem) {
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