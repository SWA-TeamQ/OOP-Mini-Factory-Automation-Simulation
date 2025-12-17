package org.automation.controllers;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.automation.utils.Logger;
import org.automation.entities.ProductItem;

public class ItemTracker {
    private final Map<Integer, ProductItem> items = new ConcurrentHashMap<>();
    private int nextId = 1;
    public ItemTracker(){
    };

    public synchronized ProductItem addItem(ProductItem item) {
        int id = nextId++;
        item.setId(id);
        items.put(id, item);
        Logger.log("ProductItem", id, "created", "Item created: " + item.getName());
        return item;
    }

    public ProductItem getItem(int id) {
        return items.get(id);
    }

    public Collection<ProductItem> getAllItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public boolean updateItemStatus(int id, String status) {
        ProductItem it = items.get(id);
        if (it == null) return false;
        String prev = it.getStatus();
        it.setStatus(status);
        Logger.log("ProductItem", id, "status_update", "Status changed: " + prev + " -> " + status);
        return true;
    }

    public boolean trackMovement(int id, int stationId) {
        ProductItem it = items.get(id);
        if (it == null) return false;
        int prev = it.getCurrentStationId();
        it.setCurrentStationId(stationId);
        Logger.log("ProductItem", id, "moved", "Moved from station " + prev + " to " + stationId);
        return true;
    }
}
