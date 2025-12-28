package org.Automation.entities;

import java.util.LinkedList;
import java.util.Queue;

public class ConveyorBelt {
    private String id;
    private Queue<ProductItem> beltItems = new LinkedList<>();
    private int capacity;

    public ConveyorBelt(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public String getId(){
        return id;
    }

    public boolean addItem(ProductItem item) {
        if (beltItems.size() < capacity) {
            beltItems.offer(item);
            return true;
        }
        return false;
    }

    public ProductItem removeItem() {
        return beltItems.poll();
    }

    public boolean isEmpty() {
        return beltItems.isEmpty();
    }
}
