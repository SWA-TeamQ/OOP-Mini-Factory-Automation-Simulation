# 🧠 Design Decisions & Rationale

## 1. Tick-Based vs. Observer-Based Physics
- **Decision**: Tick-Based (Polling via Clock).
- **Rationale**: In an industrial simulation, components like conveyors have continuous progress. A tick-based approach allows us to "calculate" the position of an item at any millisecond, providing a much smoother and more realistic simulation than a pure event-based approach where things just "arrive" instantly.
- **Alternative**: Pure Observer/Callback. Rejected because it makes modeling duration and capacity very difficult and prone to race conditions.

## 2. Central Simulation Clock (Singleton)
- **Decision**: A single global clock.
- **Rationale**: Ensures all components are synchronized. If the conveyor moves at tick 100, the machine at the end must also be at tick 100 to receive it.
- **Alternative**: Distributed clocks per station. Rejected due to complexity in synchronization and "drift" issues.

## 3. Hybrid Event-Driven Communication
- **Decision**: Use an `EventBus` for control logic.
- **Rationale**: Decouples the "routing" logic from the hardware. The machine doesn't care where the item goes next; it only cares that it finished its own job. This makes the system extremely flexible (e.g., we can change the factory floor layout in the DB without touching the `Machine` class code).
- **Alternative**: Direct Method Calls. Rejected because it creates "Spaghetti Code" where every component needs a reference to every other component.

## 4. Station Orchestration vs. Machine Autonomy
- **Decision**: Stations orchestrate machines.
- **Rationale**: An industrial station usually has local logic (e.g., "If Machine A is busy, try Machine B"). Keeping this logic in the `Station` class prevents the `ProductionLineService` from becoming a "God Object."
- **Alternative**: Machines pick their own items. Rejected because machines shouldn't have visibility into the station's input queue.

## 5. Repository Pattern & SQLite
- **Decision**: Persistence layer using Repositories.
- **Rationale**: Allows the simulation to be "restarted" or audited. Even if the application crashes, the state of the product items and machine history is preserved.
- **Alternative**: In-memory Lists. Rejected because it lacks professional-grade auditability and durability.
