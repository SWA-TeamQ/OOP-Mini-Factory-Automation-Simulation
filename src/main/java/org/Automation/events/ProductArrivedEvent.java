package org.automation.events;

import org.automation.entities.ProductItem;
import org.automation.events.abstracts.ProductEvent;

/**
 * Raised when a product arrives at a station.
 */
public class ProductArrivedEvent extends ProductEvent {
    private final String stationId;

    public ProductArrivedEvent(ProductItem productItem, String stationId) {
        super("ProductArrivedEvent", productItem);
        this.stationId = stationId;
    }

    public String getStationId() {
        return stationId;
    }

    @Override
    public String toString() {
        return "Product " + getProductId() + " arrived at station " + stationId;
    }
}
