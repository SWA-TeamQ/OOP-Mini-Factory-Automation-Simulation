package org.automation.events;

import org.automation.entities.ProductItem;
import org.automation.events.abstracts.ProductEvent;

public class ProductMovedEvent extends ProductEvent {
    private final String fromStationId;
    private final String toStationId;

    public ProductMovedEvent(ProductItem productItem, String fromStationId, String toStationId) {
        super("ProductMovedEvent", productItem);
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public String getToStationId() {
        return toStationId;
    }

    @Override
    public String toString() {
        return "Product " + getProductId() + " moved from " + fromStationId + " to " + toStationId;
    }
}
