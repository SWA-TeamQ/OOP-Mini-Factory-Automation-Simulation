package org.Automation.events;

public class ProductArrivedAtStationEvent extends Event {
    private final String stationId;
    private final String itemId;

    public ProductArrivedAtStationEvent(String stationId, String itemId) {
        super("ProductArrivedAtStationEvent", stationId);
        this.stationId = stationId;
        this.itemId = itemId;
    }

    public String getStationId() {
        return stationId;
    }

    public String getItemId() {
        return itemId;
    }
}
