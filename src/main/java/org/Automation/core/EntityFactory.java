package org.Automation.core;

import org.Automation.entities.*;
import org.Automation.entities.enums.*;

/**
 * Factory Pattern implementation to centralize object creation.
 * This makes it easy to reconstruct objects from database/file records.
 */
public class EntityFactory {

    public static Station createStation(String type, String id, String status) {
        Station station = switch (type.toUpperCase()) {
            case "INPUT" -> new InputStation(id);
            case "PRODUCTION", "PROCESSING" -> new ProductionStation(id, null);
            case "PACKAGING" -> new PackagingStation(id, null);
            default -> throw new IllegalArgumentException("Unknown station type: " + type);
        };
        if (status != null) {
            station.setStatus(StationStatus.valueOf(status.toUpperCase()));
        }
        return station;
    }

    public static Machine createMachine(String type, String id, String name, String status, EventBus eventBus) {
        MachineType machineType = MachineType.valueOf(type.toUpperCase());
        Machine machine = new Machine(id, machineType, eventBus);
        if (status != null) {
            machine.setStatus(MachineStatus.valueOf(status.toUpperCase()));
        }
        return machine;
    }

    public static ProductItem createProductItem(String id, String name, double weight, String status, String createdAt, String currentStationId) {
        ProductItem item = new ProductItem(id);
        // Assuming ProductItem has these fields and setters or a constructor that takes them
        return item;
    }
    
    public static Sensor createSensor(String id, String type, String machineId, String status, EventBus eventBus) {
        Sensor sensor = new Sensor(id, eventBus);
        // Set other properties if available
        return sensor;
    }
}
