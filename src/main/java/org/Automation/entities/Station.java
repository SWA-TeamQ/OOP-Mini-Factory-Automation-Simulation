package org.Automation.entities;

import org.Automation.entities.enums.StationStatus;
import org.Automation.core.EventBus;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical location in the production line.
 * Orchestrates machines and sensors (Event-Driven).
 */
public abstract class Station {
    protected final String id;
    protected StationStatus status;
    protected final List<ProductItem> itemsInStation = new ArrayList<>();
    protected final List<Machine> machines = new ArrayList<>();
    protected final List<Sensor> sensors = new ArrayList<>();
    protected final EventBus eventBus;

    public Station(String id, EventBus eventBus) {
        this.id = id;
        this.eventBus = eventBus;
        this.status = StationStatus.INACTIVE;
    }

    public String getId() {
        return id;
    }

    public void addMachine(Machine machine) {
        machines.add(machine);
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }

    /**
     * Input Gate: Called when a product enters the station.
     */
    public void onProductArrived(ProductItem item) {
        itemsInStation.add(item);
        eventBus.publish("product_arrived", "Item " + item.getId() + " arrived at station " + id);
        processItems();
    }

    /**
     * Output Gate: Called when a product is ready to leave the station.
     */
    public void onProductReadyForTransfer(ProductItem item) {
        eventBus.publish("product_ready_for_transfer", item);
    }

    public List<ProductItem> getItems() {
        return itemsInStation;
    }

    public void removeItem(ProductItem item) {
        itemsInStation.remove(item);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id='" + id + '\'' +
                ", machines=" + machines.size() +
                ", sensors=" + sensors.size() +
                ", status=" + status +
                '}';
    }

    public abstract void processItems();
}
