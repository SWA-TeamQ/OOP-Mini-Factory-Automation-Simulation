package org.Automation.entities;

import java.util.LinkedList;
import java.util.Queue;

public class ConveyorBelt {
    private String id;
    private Queue<ProductItem> beltItems = new LinkedList<>();
    private int capacity;
    private double speed;

    public ConveyorBelt(String id, int capacity, double speed) {
        this.id = id;
        this.capacity = capacity;
        this.speed = speed;
    }

    public ConveyorBelt(String id, double speed) {
        this.id = id;
        this.capacity = 10; // Default capacity
        this.speed = speed;
    }

    public String getId(){
        return id;
    }

    public double getSpeed() {
        return speed;
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
