package org.Automation.events;

import java.time.Instant;

/**
 * Payload published on the EventBus when a new ProductItem is registered.
 */
public class ItemRegisteredEvent {
    private final String itemId;
    private final String timestamp;

    public ItemRegisteredEvent(String itemId) {
        this(itemId, Instant.now().toString());
    }

    public ItemRegisteredEvent(String itemId, String timestamp) {
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
        return "ItemRegisteredEvent{itemId='" + itemId + "', timestamp='" + timestamp + "'}";
    }
}
