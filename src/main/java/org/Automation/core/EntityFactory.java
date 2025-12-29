package org.automation.core;

import org.automation.entities.*;
import org.automation.entities.enums.*;

/**
 * Factory Pattern implementation to centralize object creation.
 * This makes it easy to reconstruct objects from database/file records.
 */
public class EntityFactory {

    public static Station createStation(String type, String id, String status, EventBus eventBus) {
        final String INPUT = "INPUT";
        final String PROCESSING = "PROCESSING";
        final String PACKAGING = "PACKAGING";

        Station station = switch (type.toUpperCase()) {
            case INPUT -> new InputStation(id, eventBus);
            case PROCESSING -> new ProductionStation(id, eventBus);
            case PACKAGING -> new PackagingStation(id, eventBus);
            default -> throw new IllegalArgumentException("Unknown station type: " + type);
        };
        if (status != null) {
            station.setStatus(StationStatus.valueOf(status.toUpperCase()));
        }
        return station;
    }

    public static Machine createMachine(String type, String id, String name, String status) {
        type = type.toUpperCase();

        MachineType machineType = MachineType.valueOf(type);
        Machine machine;

        switch (machineType){
            case INPUT:
                machine = new InputMachine();
                break;

            case PROCESSING:
                break;

            case PACKAGING:
                break;
        }

        Machine machine = (machineType == MachineType.PACKAGING) ? new PackagingMachine(id)
                : new Machine(id, machineType);

        if (status != null) {
            machine.setStatus(MachineStatus.valueOf(status.toUpperCase()));
        }
        return machine;
    }

    public static ProductItem createProductItem(String id) {
        return new ProductItem(id);
    }

    public static Sensor createSensor(String id, String locationId, String type, double threshold,
            EventBus eventBus) {
        type = type.toUpperCase();

        final String TEMPERATURE = "TEMPERATURE";
        final String WEIGHT = "WEIGHT";

        if (TEMPERATURE.equalsIgnoreCase(type)) {
            return new TemperatureSensor(id, locationId, threshold, eventBus);

        } else if (WEIGHT.equalsIgnoreCase(type)) {
            return new WeightSensor(id, locationId, threshold, eventBus);

        } else {
            throw new IllegalArgumentException("Unknown sensor type: " + type);
        }
    }

    public static ConveyorBelt createConveyor(String id, int capacity, int duration) {
        return new ConveyorBelt(id, capacity, duration);
    }
}
