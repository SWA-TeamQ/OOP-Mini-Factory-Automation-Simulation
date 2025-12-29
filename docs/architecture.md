# 🏭 Architecture Overview: Production Line Simulation

## 1. System Goals & Problem Domain
The **Production Line Simulation** system is designed to model and automate a manufacturing floor. The primary goal is to simulate the flow of products through various **Stations**, processed by **Machines**, monitored by **Sensors**, and transported by **Conveyors**.

The system addresses the complexity of industrial automation by providing a digital twin that can:
- Predict throughput and identify bottlenecks.
- Validate workflow logic before physical deployment.
- Monitor real-time status and handle asynchronous events (errors, repairs).

## 2. Architectural Style: Hybrid Simulation
The system employs a **Hybrid Architectural Style**, combining two distinct paradigms to accurately model an industrial environment:

### A. Time-Driven Simulation (Physical Layer)
The "physics" of the factory—how long a machine takes to process an item or how fast a conveyor moves—is driven by a central **Simulation Clock**. 
- **Mechanism**: Every 50ms (or as configured), the clock sends a `tick` signal to all registered `Tickable` components.
- **Tickable Components**: `Machine`, `ConveyorBelt`, `TemperatureSensor`, `WeightSensor`.

### B. Event-Driven Communication (Logic Layer)
Orchestration and high-level decisions are handled through a **typed Event Bus**. This decouples components, allowing them to react to changes without knowing who triggered them.
- **Mechanism**: Components publish structured `Event` objects (e.g., `ProductReadyForTransferEvent`) and subscribers react accordingly.
- **Event Hierarchy**: All events extend a base `Event` class with `type`, `tickTimestamp`, and `source` fields.
- **Key Components**: `EventBus`, `ProductionLineService`, `WorkFlowController`, `ItemTrackingService`.

## 3. Core Design Principles
- **Separation of Concerns**: Logic is divided into Entities (data), Repositories (persistence), Services (process), and Controllers (orchestration).
- **Dependency Inversion**: High-level services depend on abstractions (Interfaces like `IMachineService`, `IConveyorService`) rather than concrete implementations.
- **Type-Safe Events**: The event system uses strongly-typed event classes for compile-time safety and self-documentation.
- **Observability**: A centralized `Logger` tracks every state change in the system.
- **State Persistence**: The system maintains its state in an SQLite database, allowing for recovery and historical analysis.

## 4. High-Level Component Interaction
1. **The Clock** drives the `Machines`, `Conveyors`, and `Sensors` via `tick()`.
2. **The Machines** publish `MachineProcessingFinishedEvent` when work is done.
3. **The Station** reacts to machine completion, triggering its queue processor.
4. **The ProductionLineService** listens for `ProductReadyForTransferEvent` and commands the `Conveyor` to move the item.
5. **The Conveyor** publishes `ProductDeliveredEvent` when an item reaches the end.
6. **The ItemTrackingService** updates the database and audit logs for every movement.

## 5. Package Structure
```
org.Automation
├── core/           # EventBus, Logger, EntityFactory, DatabaseManager
├── engine/         # SimulationClock, Tickable interface
├── entities/       # Station, Machine, Sensor, ConveyorBelt, ProductItem
│   └── enums/      # MachineType, MachineStatus, StationType, StationStatus
├── events/         # All 24 typed Event classes
├── repositories/   # Data access layer (Station, Machine, ProductItem, etc.)
├── services/       # Business logic (ProductionLine, ItemTracking, Machine, etc.)
├── Controllers/    # WorkFlowController (orchestration)
└── ui/             # ConsoleApp (user interface)
```

## 6. Scalability & Maintainability
- **Scalability**: New stations, machines, or sensor types can be added by extending the base classes and registering them in the repositories. The event-based nature means they don't need to be hard-coded into the main loop.
- **Maintainability**: The strict separation between the "Simulation Engine" (Clock/Ticks) and the "Business Logic" (Services/Events) allows developers to modify the physics (e.g., speed up the clock) without changing how routing works.
- **Extensibility**: The abstract `Sensor` class allows new sensor types (e.g., `VibrationSensor`, `PressureSensor`) to be added without modifying existing code.
