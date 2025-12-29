# 🧩 Component Deep-Dive

## 1. ProductionLineService
- **Responsibility**: High-level routing and physical flow management.
- **Owned Data**: `flowMap` (topology), `itemLocationMap` (runtime tracking).
- **Exposed Behavior**: `process(item)`, `handleTransfer(item)`, `handleArrival(item)`.
- **Event Subscriptions**: `ProductReadyForTransferEvent`, `ProductDeliveredEvent`.
- **Boundaries**: Must NOT handle machine-specific logic or database direct-writes (use Repositories).

## 2. Station (Abstract & Implementations)
- **Implementations**: `InputStation`, `ProductionStation`, `PackagingStation`.
- **Responsibility**: Orchestrates machines and sensors within a physical zone.
- **Owned Data**: 
  - `waitingQueue` (FIFO queue for items awaiting processing)
  - `machines` (list of attached machines)
- **Exposed Behavior**: `onProductArrived()`, `processQueue()`, `processItems()`.
- **Event Subscriptions**: `ProductReadyForTransferEvent` (triggers queue processing when machines become available).
- **Boundaries**: Must NOT handle product movement between stations (delegated to `ProductionLineService`).

## 3. Machine
- **Responsibility**: Performs work on a `ProductItem` over time.
- **Owned Data**: 
  - `MachineType`, `MachineStatus` (IDLE, BUSY, STOPPED, ERROR)
  - `processingTicksRemaining`, `totalProcessingTicks`
  - `currentItem` (the item being processed)
- **Exposed Behavior**: `assignItem()`, `tick()`, `start()`, `stop()`, `repair()`.
- **Events Published**: 
  - `MachineStartedEvent`, `MachineStoppedEvent`
  - `MachineProcessingStartedEvent`, `MachineProcessingFinishedEvent`
  - `MachineErrorEvent`, `MachineRepairedEvent`
  - `ProductReadyForTransferEvent`
- **Boundaries**: Purely local. A machine should not know about other machines or the line topology.

## 4. Sensor (Abstract Base & Implementations)
- **Responsibility**: Measures environment or product properties.
- **Implementations**: 
  - `TemperatureSensor` – Monitors temperature (ambient 20°C ± variation).
  - `WeightSensor` – Monitors weight (nominal 10kg ± variation).
- **Owned Data**: `threshold`, `samplingRateTicks`, `lastValue`, `locationId`.
- **Abstract Methods**: `readValue()`, `updateValue()`, `validateReading()`, `getSensorInfo()`.
- **Exposed Behavior**: `tick()` (triggers sampling at configured rate).
- **Events Published**:
  - `TemperatureReadingEvent`, `TemperatureThresholdExceededEvent`
  - `WeightReadingEvent`, `WeightThresholdExceededEvent`
- **Event Subscriptions**: 
  - `SET_TEMPERATURE` (for TemperatureSensor)
  - `SET_WEIGHT` (for WeightSensor)
- **Boundaries**: Read-only observation. A sensor should never modify the system state, only report it via events.

## 5. ConveyorBelt
- **Responsibility**: Transports items between stations with a fixed time delay.
- **Owned Data**: `capacity`, `transferDurationTicks`, `itemsInTransit`.
- **Exposed Behavior**: `addItem()`, `tick()`, `start()`, `stop()`.
- **Events Published**: `ConveyorItemAddedEvent`, `ProductDeliveredEvent`, `ConveyorStartedEvent`, `ConveyorStoppedEvent`.
- **Boundaries**: Must only hold items in transit. It does not decide routing; it only delivers what it is given.

## 6. Event Bus
- **Responsibility**: Decoupled communication channel using typed events.
- **Exposed Behavior**: `publish(Event)`, `subscribe(eventType, EventSubscriber)`.
- **Key Design**: 
  - Accepts `Event` objects (not raw strings).
  - Routes by `event.getType()` string.
  - Subscribers receive the full `Event` object and use `instanceof` for type safety.
- **Boundaries**: Must be stateless. It is a pipe, not a storage.

## 7. Simulation Clock
- **Responsibility**: Central time provider and hardware heartbeat.
- **Owned Data**: `logicalTick`, `speedFactor`.
- **Exposed Behavior**: `registerTickable()`, `start()`, `stop()`, `getLogicalTick()`.
- **Tickable Components**: `Machine`, `ConveyorBelt`, `Sensor` (and subclasses).
- **Boundaries**: Must NOT contain domain logic. It only signals that time has passed.

## 8. Repositories
- **Entities**: `StationRepository`, `MachineRepository`, `ProductItemRepository`, `ConveyorRepository`, `SensorRepository`.
- **Responsibility**: Bridge between the application and the SQLite database.
- **Boundaries**: Should only contain CRUD operations and basic search logic. No business workflow logic.

## 9. ProductItem (Domain Model)
- **Responsibility**: Container for product data and history.
- **Owned Data**: `id`, `completed`, `history` (list of processing events).
- **Exposed Behavior**: `addHistory(entry)`, `getHistory()`, `isCompleted()`, `setCompleted()`.
- **Boundaries**: Pure data object (POJO). It should not have any logic that affects the state of the factory.

## 10. Services Layer
- **ItemTrackingService**: Registers items, tracks movements, marks completion.
- **MachineService**: Starts/stops/repairs machines by ID.
- **SensorService**: Manages sensor lifecycle and queries.
- **ConveyorService**: Controls conveyor start/stop.
- **Interfaces**: `IMachineService`, `IConveyorService`, `ISensorService`, `IProductionLineService`, `IItemTrackingService`.
