package org.Automation.entities;

import org.Automation.entities.enums.StationStatus;
import java.util.ArrayList;
import java.util.List;

public abstract class Station {
    protected String id;
    protected StationStatus status;
    protected List<ProductItem> itemsInStation = new ArrayList<>();
    private Machine machine;
    private Sensor sensor;



    public Station(String id, Machine machine, Sensor sensor) {
        this.id = id;
        this.machine = machine;
        this.sensor = sensor;
        this.status = StationStatus.INACTIVE;
    }
    public Station(String id) {
        this.id = id;
        this.status = StationStatus.INACTIVE;
    }

    public String getId() {
        return id;
    }

    public Machine getMachine() {
        return machine;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }

    public List<ProductItem> getItems() {
        return itemsInStation;
    }

    public void addItem(ProductItem item) {
        itemsInStation.add(item);
    }

    public void removeItem(ProductItem item) {
        itemsInStation.remove(item);
    }

       public String toString() {
        return "Station{" +
                "id='" + id + '\'' +
                ", machine=" + machine +
                ", sensor=" + sensor +
                ", status=" + status +
                '}';
    }

    // Abstract method for processing products
    public abstract void processItems();
}
