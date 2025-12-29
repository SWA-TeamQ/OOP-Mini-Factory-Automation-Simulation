# 📡 Event System Documentation

The Event System is the nervous system of the simulation. It allows components to interact without tight coupling, making the system extensible and resilient.

## 1. Structured Event Architecture

All events now extend a base `Event` class that provides:
- **`type`**: A string identifier for the event (e.g., `"MachineErrorEvent"`).
- **`tickTimestamp`**: The logical simulation tick when the event occurred.
- **`source`**: The originating component or data payload.

### Event Class Hierarchy

```text
Event (abstract base)
├── MachineEvent (marker interface)
│   ├── MachineStartedEvent
│   ├── MachineStoppedEvent
│   ├── MachineProcessingStartedEvent
│   ├── MachineProcessingFinishedEvent
│   ├── MachineErrorEvent
│   └── MachineRepairedEvent
├── ProductEvent (marker interface)
│   ├── ProductArrivedAtStationEvent
│   ├── ProductReadyForTransferEvent
│   ├── ProductDeliveredEvent
│   ├── ItemCompletedEvent
│   ├── ItemMovedEvent
│   └── ItemRegisteredEvent
├── SensorEvent (marker interface)
│   ├── TemperatureReadingEvent
│   ├── TemperatureThresholdExceededEvent
│   ├── WeightReadingEvent
│   └── WeightThresholdExceededEvent
└── ConveyorEvent (marker interface)
    ├── ConveyorStartedEvent
    ├── ConveyorStoppedEvent
    └── ConveyorItemAddedEvent
```

## 2. Event Catalog

### Machine Events

| Event Class | Producer | Payload | Description |
| :--- | :--- | :--- | :--- |
| `MachineStartedEvent` | `Machine` | `machineId` | Fired when a machine transitions from STOPPED to IDLE. |
| `MachineStoppedEvent` | `Machine` | `machineId` | Fired when a machine is manually stopped. |
| `MachineProcessingStartedEvent` | `Machine` | `machineId`, `itemId` | Fired when a machine begins work on an item. |
| `MachineProcessingFinishedEvent` | `Machine` | `machineId`, `ProductItem` | Fired when a machine completes its processing cycle. |
| `MachineErrorEvent` | `Machine` | `machineId`, `message` | Fired during random mechanical failures. |
| `MachineRepairedEvent` | `Machine` | `machineId` | Fired when an ERROR machine is repaired to IDLE. |

### Product Events

| Event Class | Producer | Payload | Description |
| :--- | :--- | :--- | :--- |
| `ProductArrivedAtStationEvent` | `Station` | `stationId`, `itemId` | Fired when an item enters a station's zone. |
| `ProductReadyForTransferEvent` | `Station` | `Machine` | `ProductItem`, `sourceId` | Signals that an item can be moved to the next station. |
| `ProductDeliveredEvent` | `ConveyorBelt` | `ProductItem` | Fired when an item reaches the end of a conveyor. |
| `ItemCompletedEvent` | `PackagingStation` | `ProductItem`, `stationId` | Fired when an item is fully packaged and exits. |
| `ItemMovedEvent` | `ProductionLineService` | `itemId`, `from`, `to` | Tracks item location changes for audit. |
| `ItemRegisteredEvent` | `ItemTrackingService` | `ProductItem`, `stationId` | Fired when a new item enters the production line. |

### Sensor Events

| Event Class | Producer | Payload | Description |
| :--- | :--- | :--- | :--- |
| `TemperatureReadingEvent` | `TemperatureSensor` | `sensorId`, `value`, `message` | Periodic temperature sampling. |
| `TemperatureThresholdExceededEvent` | `TemperatureSensor` | `sensorId`, `value`, `threshold` | Alarm when temperature exceeds threshold. |
| `WeightReadingEvent` | `WeightSensor` | `sensorId`, `value`, `message` | Periodic weight sampling. |
| `WeightThresholdExceededEvent` | `WeightSensor` | `sensorId`, `value`, `threshold` | Alarm when weight exceeds threshold. |

### Conveyor Events

| Event Class | Producer | Payload | Description |
| :--- | :--- | :--- | :--- |
| `ConveyorStartedEvent` | `ConveyorBelt` | `conveyorId` | Fired when conveyor begins operation. |
| `ConveyorStoppedEvent` | `ConveyorBelt` | `conveyorId` | Fired when conveyor stops operation. |
| `ConveyorItemAddedEvent` | `ConveyorBelt` | `conveyorId`, `ProductItem` | Fired when an item is placed on the conveyor. |

## 3. Why Typed Events? (Decoupling + Type Safety)

The system uses **specific event classes** instead of generic string topics:

- **Type Safety**: The compiler catches errors if a subscriber expects a `MachineProcessingFinishedEvent` but receives something else.
- **Self-Documenting**: Each event class clearly defines its payload via getters.
- **IDE Support**: Auto-complete and refactoring tools work correctly.
- **Flexible Routing**: The `EventBus` routes by event type string, but subscribers can use `instanceof` for precise handling.

## 4. Propagation Model

1. **Machine** finishes → Publishes `MachineProcessingFinishedEvent` + `ProductReadyForTransferEvent`.
2. **Station** (Subscriber) → Reacts to `ProductReadyForTransferEvent`, triggers queue processing.
3. **ProductionLineService** (Subscriber) → Moves item to Conveyor via `moveItemViaConveyor()`.
4. **Conveyor** (Clock-driven) → Eventually publishes `ProductDeliveredEvent`.
5. **ProductionLineService** → Calls `handleArrival()`, delivering item to next Station.

## 5. Publishing and Subscribing

### Publishing Events

```java
// Create and publish a typed event
eventBus.publish(new MachineErrorEvent(machineId, "Hydraulic failure detected"));
```

### Subscribing to Events

```java
eventBus.subscribe("MachineErrorEvent", new EventSubscriber() {
    @Override
    public void onEvent(Event event) {
        if (event instanceof MachineErrorEvent errorEvent) {
            Logger.error("Machine " + errorEvent.getMachineId() + ": " + errorEvent.getMessage());
        }
    }
});
```

## 6. Preventing Issues

- **Circular Events**: The system avoids circularity by ensuring that "Logic" events never directly trigger "Physical" ticks. Ticks come only from the `Clock`.
- **Type Mismatches**: Using `instanceof` pattern matching ensures that only correctly-typed events are processed.
- **Duplicates**: Since the `SimulationClock` is the only source of truth for time, events are naturally ordered by their `tickTimestamp`.
