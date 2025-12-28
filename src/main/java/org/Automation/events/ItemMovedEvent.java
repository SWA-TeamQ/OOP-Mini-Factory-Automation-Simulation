package org.Automation.events;

import java.time.Instant;

/**
 * Payload published on the EventBus when an item moves through the workflow.
 * Used by tracking/logging systems to record movement history without tightly
 * coupling to ProductionLineService.
 */
public class ItemMovedEvent {
    private final String itemId;
    private final String stationId;
    private final String machineId;
    private final String timestamp;

    public ItemMovedEvent(String itemId, String stationId, String machineId) {
        this(itemId, stationId, machineId, Instant.now().toString());
    }

    public ItemMovedEvent(String itemId, String stationId, String machineId, String timestamp) {
        this.itemId = itemId;
        this.stationId = stationId;
        this.machineId = machineId;
        this.timestamp = timestamp;
    }

    public String getItemId() {
        return itemId;
    }

    public String getStationId() {
        return stationId;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ItemMovedEvent{itemId='" + itemId + "', stationId='" + stationId + "', machineId='" + machineId + "', timestamp='" + timestamp + "'}";
    }
}


