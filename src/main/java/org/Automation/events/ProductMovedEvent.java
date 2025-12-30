package org.automation.events;

import org.automation.core.Logger;

public class ProductMovedEvent {
    private final String productId;
    private final String fromStationId;
    private final String toStationId;

    public ProductMovedEvent(String productId, String fromStationId, String toStationId) {
        this.productId = productId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
    }

    public String getProductId() {
        return productId;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public String getToStationId() {
        return toStationId;
    }

    @Override
    public String toString(){
        return "Product " + productId + " moved from " + fromStationId + " to " + toStationId;
    }
}
