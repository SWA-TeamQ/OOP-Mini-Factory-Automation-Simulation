package org.Automation.entities;

/**
 * Persistent tracking record for an item movement/lifecycle event.
 * Stored in SQLite via ItemTrackingEventRepository.
 */
public class ItemTrackingEvent {
    private final long id;
    private final String timestamp;
    private final String itemId;
    private final String eventType;
    private final String stationId;
    private final String machineId;
    private final String details;

    public ItemTrackingEvent(long id, String timestamp, String itemId, String eventType,
                             String stationId, String machineId, String details) {
        this.id = id;
        this.timestamp = timestamp;
        this.itemId = itemId;
        this.eventType = eventType;
        this.stationId = stationId;
        this.machineId = machineId;
        this.details = details;
    }

    public ItemTrackingEvent(String timestamp, String itemId, String eventType,
                             String stationId, String machineId, String details) {
        this(0L, timestamp, itemId, eventType, stationId, machineId, details);
    }

    public long getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getItemId() {
        return itemId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getStationId() {
        return stationId;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "ItemTrackingEvent{id=" + id
                + ", timestamp='" + timestamp + '\''
                + ", itemId='" + itemId + '\''
                + ", eventType='" + eventType + '\''
                + ", stationId='" + stationId + '\''
                + ", machineId='" + machineId + '\''
                + ", details='" + details + "'}";
    }
}


