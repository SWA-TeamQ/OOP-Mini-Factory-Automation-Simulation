package org.Automation.core;

import org.Automation.entities.*;
import org.Automation.entities.enums.*;

/**
 * Factory Pattern implementation to centralize object creation.
 * This makes it easy to reconstruct objects from database/file records.
 */
public class EntityFactory {

    public static Station createStation(String type, String id, String status, EventBus eventBus) {
        Station station = switch (type.toUpperCase()) {
            case "INPUT" -> new InputStation(id, eventBus);
            case "PRODUCTION", "PROCESSING" -> new ProductionStation(id, eventBus);
            case "PACKAGING" -> new PackagingStation(id, eventBus);
            default -> throw new IllegalArgumentException("Unknown station type: " + type);
        };
        if (status != null) {
            station.setStatus(StationStatus.valueOf(status.toUpperCase()));
        }
        return station;
    }

    public static Machine createMachine(String type, String id, String name, String status, EventBus eventBus) {
        MachineType machineType = MachineType.valueOf(type.toUpperCase());
        Machine machine = (machineType == MachineType.PACKAGING) ? new PackagingMachine(id, eventBus)
                : new Machine(id, machineType, eventBus);

        if (status != null) {
            machine.setStatus(MachineStatus.valueOf(status.toUpperCase()));
        }
        return machine;
    }

    public static ProductItem createProductItem(String id) {
        return new ProductItem(id);
    }

    public static Sensor createSensor(String id, String locationId, String type, double threshold, int samplingRate,
            EventBus eventBus) {
        if ("Temperature".equalsIgnoreCase(type)) {
            return new TemperatureSensor(id, locationId, threshold, samplingRate, eventBus);
        } else if ("Weight".equalsIgnoreCase(type)) {
            return new WeightSensor(id, locationId, threshold, samplingRate, eventBus);
        } else {
            // Default fallback if type is unknown, but since Sensor is abstract we must
            // pick one or throw error
            // For safety in this prompt context, defaulting to Temperature or throwing
            // exception
            throw new IllegalArgumentException("Unknown sensor type: " + type);
        }
    }

    public static ConveyorBelt createConveyor(String id, int capacity, int duration, EventBus eventBus) {
        return new ConveyorBelt(id, capacity, duration, eventBus);
    }
}
