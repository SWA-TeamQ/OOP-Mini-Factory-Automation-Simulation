package org.Automation.events;

import java.time.Instant;

/**
 * Payload published on the EventBus when a ProductItem is marked as completed.
 */
public class ItemCompletedEvent {
    private final String itemId;
    private final String timestamp;

    public ItemCompletedEvent(String itemId) {
        this(itemId, Instant.now().toString());
    }

    public ItemCompletedEvent(String itemId, String timestamp) {
        this.itemId = itemId;
        this.timestamp = timestamp;
    }

    public String getItemId() {
        return itemId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ItemCompletedEvent{itemId='" + itemId + "', timestamp='" + timestamp + "'}";
    }
}
