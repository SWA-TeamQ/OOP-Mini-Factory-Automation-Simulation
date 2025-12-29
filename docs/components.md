# 🧩 Component Deep-Dive

## 1. ProductionLineService
- **Responsibility**: High-level routing and physical flow management.
- **Owned Data**: `flowMap` (topology), `itemLocationMap` (runtime tracking).
- **Exposed Behavior**: `process(item)`, `handleTransfer(item)`, `handleArrival(item)`.
- **Boundaries**: Must NOT handle machine-specific logic or database direct-writes (use Repositories).

## 2. Station (Abstract & Implementations)
- **Implementations**: `InputStation`, `ProductionStation`, `PackagingStation`.
- **Responsibility**: Orchestrates machines and sensors within a physical zone.
- **Owned Data**: List of `Machine`, List of `Sensor`, `itemsInStation` buffer.
- **Exposed Behavior**: `onProductArrived()`, `processItems()`.
- **Boundaries**: Must NOT handle product movement between stations (delegated to `ProductionLineService`).

## 3. Machine
- **Responsibility**: Performs work on a `ProductItem` over time.
- **Owned Data**: `MachineType`, `MachineStatus`, `processingTicksRemaining`.
- **Exposed Behavior**: `assignItem()`, `tick()`, `start()`, `stop()`.
- **Boundaries**: Purely local. A machine should not know about other machines or the line topology.

## 4. Sensor
- **Responsibility**: Measures environment or product properties (e.g., Temperature).
- **Owned Data**: `threshold`, `samplingRateTicks`.
- **Exposed Behavior**: `tick()`, `sample()`.
- **Boundaries**: Read-only observation. A sensor should never modify the system state, only report it via events.

## 5. ConveyorBelt
- **Responsibility**: Transports items between stations with a fixed time delay.
- **Owned Data**: `capacity`, `transferDurationTicks`.
- **Exposed Behavior**: `addItem()`, `tick()`.
- **Boundaries**: Must only hold items in transit. It does not decide routing; it only delivers what it is given.

## 6. Event Bus
- **Responsibility**: Decoupled communication channel.
- **Exposed Behavior**: `publish(topic, payload)`, `subscribe(topic, callback)`.
- **Boundaries**: Must be stateless. It is a pipe, not a storage.

## 7. Simulation Clock
- **Responsibility**: Central time provider and hardware heartbeat.
- **Owned Data**: `logicalTick`, `speedFactor`.
- **Exposed Behavior**: `registerTickable()`, `start()`, `stop()`.
- **Boundaries**: Must NOT contain domain logic. It only signals that time has passed.

## 8. Repositories
- **Entities**: `StationRepository`, `MachineRepository`, `ProductItemRepository`, etc.
- **Responsibility**: Bridge between the application and the SQLite database.
- **Boundaries**: Should only contain CRUD operations and basic search logic. No business workflow logic.

## 9. ProductItem (Domain Model)
- **Responsibility**: Container for product data and history.
- **Owned Data**: `id`, `completed`, `history`.
- **Boundaries**: Pure data object (POJO). It should not have any logic that effects the state of the factory.
