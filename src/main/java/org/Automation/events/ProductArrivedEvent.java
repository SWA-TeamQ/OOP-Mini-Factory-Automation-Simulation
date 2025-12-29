package org.Automation.events;

/**
 * Raised when a product arrives at a station.
 */
public class ProductArrivedEvent extends Event {
    private final String productId;
    private final String stationId;

    public ProductArrivedEvent(String productId, String stationId) {
        super("ProductArrivedEvent");
        this.productId = productId;
        this.stationId = stationId;
    }

    public String getProductId() {
        return productId;
    }

    public String getStationId() {
        return stationId;
    }
}
