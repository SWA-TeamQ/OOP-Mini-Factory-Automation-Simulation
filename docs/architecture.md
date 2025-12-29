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
The "physics" of the factory—how long a machine takes to cut metal or how fast a conveyor moves—is driven by a central **Simulation Clock**. 
- **Mechanism**: Every 50ms (or as configured), the clock sends a `tick` signal to all registered `Tickable` components.
- **Components**: `Machine`, `ConveyorBelt`, `Sensor`.

### B. Event-Driven Communication (Logic Layer)
Orchestration and high-level decisions are handled through an **asynchronous Event Bus**. This decouples components, allowing them to react to changes without knowing who triggered them.
- **Mechanism**: Components publish events (e.g., `product_ready_for_transfer`) and subscribers (e.g., `ProductionLineService`) react accordingly.
- **Components**: `EventBus`, `ProductionLineService`, `WorkFlowController`, `ItemTrackingService`.

## 3. Core Design Principles
- **Separation of Concerns**: Logic is divided into Entities (data), Repositories (persistence), Services (process), and Controllers (orchestration).
- **Dependency Inversion**: High-level services depend on abstractions (Interfaces) rather than concrete implementations.
- **Observability**: A centralized `Logger` and UI `Observers` track every state change in the system.
- **State Persistence**: The system maintains its state in an SQLite database, allowing for recovery and historical analysis.

## 4. High-Level Component Interaction
1. **The Clock** drives the `Machines` and `Conveyors`.
2. **The Machines** notify the `Station` when work is done via the `EventBus`.
3. **The ProductionLineService** listens for these events and commands the `Conveyor` to move the item to the next `Station`.
4. **The ItemTrackingService** updates the database and audit logs for every movement.

## 5. Scalability & Maintainability
- **Scalability**: New stations or machines can be added by simply registering them in the `EntityFactory` and repositories. The event-based nature means they don't need to be hard-coded into the main loop.
- **Maintainability**: The strict separation between the "Simulation Engine" (Clock/Ticks) and the "Business Logic" (Services/Events) allows developers to modify the physics (e.g., speed up the clock) without changing how routing works.
