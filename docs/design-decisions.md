# 🧠 Design Decisions & Rationale

## 1. Tick-Based vs. Observer-Based Physics
- **Decision**: Tick-Based (Polling via Clock).
- **Rationale**: In an industrial simulation, components like conveyors have continuous progress. A tick-based approach allows us to "calculate" the position of an item at any millisecond, providing a much smoother and more realistic simulation than a pure event-based approach where things just "arrive" instantly.
- **Alternative**: Pure Observer/Callback. Rejected because it makes modeling duration and capacity very difficult and prone to race conditions.

## 2. Central Simulation Clock (Singleton)
- **Decision**: A single global clock.
- **Rationale**: Ensures all components are synchronized. If the conveyor moves at tick 100, the machine at the end must also be at tick 100 to receive it.
- **Alternative**: Distributed clocks per station. Rejected due to complexity in synchronization and "drift" issues.

## 3. Typed Event Classes vs. String Topics
- **Decision**: Use strongly-typed `Event` classes with a hierarchy.
- **Rationale**: 
  - **Type Safety**: The compiler catches errors if a subscriber expects a specific event type.
  - **Self-Documentation**: Each event class clearly defines its payload via getters.
  - **IDE Support**: Auto-complete, refactoring, and "Find Usages" work correctly.
  - **Metadata**: The base `Event` class includes `tickTimestamp` and `source` for debugging and auditing.
- **Alternative**: String topics with `Object` payloads. Rejected because it requires runtime `instanceof` checks everywhere and is error-prone.

## 4. Abstract Sensor with Specialized Subclasses
- **Decision**: `Sensor` is abstract; concrete types like `TemperatureSensor` and `WeightSensor` implement behavior.
- **Rationale**: 
  - Different sensors have different reading logic (temperature vs. weight simulation).
  - Different sensors publish different event types (`TemperatureReadingEvent` vs. `WeightReadingEvent`).
  - New sensor types can be added without modifying existing code.
- **Alternative**: Single `Sensor` class with a `sensorType` field and switch statements. Rejected due to OCP (Open-Closed Principle) violations.

## 5. FIFO Queue in Station
- **Decision**: Stations use a `waitingQueue` (FIFO) for items awaiting processing.
- **Rationale**: Provides predictable, fair ordering – first-come-first-served. When a machine becomes available, the oldest waiting item is processed first.
- **Alternative**: List-based storage with manual selection. Rejected because it doesn't enforce ordering semantics.

## 6. Station Orchestration vs. Machine Autonomy
- **Decision**: Stations orchestrate machines.
- **Rationale**: An industrial station usually has local logic (e.g., "If Machine A is busy, try Machine B"). Keeping this logic in the `Station` class prevents the `ProductionLineService` from becoming a "God Object."
- **Alternative**: Machines pick their own items. Rejected because machines shouldn't have visibility into the station's input queue.

## 7. Repository Pattern & SQLite
- **Decision**: Persistence layer using Repositories.
- **Rationale**: Allows the simulation to be "restarted" or audited. Even if the application crashes, the state of the product items and machine history is preserved.
- **Alternative**: In-memory Lists. Rejected because it lacks professional-grade auditability and durability.

## 8. Service Layer with Interfaces
- **Decision**: Services implement interfaces (`IMachineService`, `IConveyorService`, etc.).
- **Rationale**: 
  - Enables dependency injection and testability.
  - High-level components depend on abstractions, not concrete implementations.
  - Allows for mock implementations in testing.
- **Alternative**: Direct instantiation of services. Rejected due to tight coupling.
