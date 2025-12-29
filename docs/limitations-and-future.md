# 🚀 Limitations & Future Improvements

## 1. Known Limitations
- **Single Threaded Clock**: While deterministic, a single-threaded clock cannot take advantage of multi-core processors. If the number of machines grows very large, the `tick()` loop might take longer than 50ms, causing "simulation lag."
- **Simplistic Routing**: The current `ProductionLineService` uses a linear sequence (indices in a list). It does not support complex branching (e.g., "If Quality Check fails, go back to Station 2").
- **Manual Repair**: Currently, machines stay in `ERROR` until a human (via UI) or a scripted command calls `repair()`. There is no autonomous "Maintenance Drone" logic.
- **Conveyor Simplification**: The simulation assumes the first conveyor in the repository is used for all transfers. It doesn't yet map specific conveyors to specific station pairs (A -> B vs B -> C).
- **Sensor Limitations**: Sensors simulate random values; they don't actually measure real `ProductItem` properties like temperature or weight.

## 2. Intentionally Unsupported Features
- **Power Consumption**: The simulation does not currently track the cost of electricity or fuel for machines.
- **Worker Simulation**: Only automated hardware is modeled; human operator shifts and fatigue are out of scope for this version.
- **Product Properties**: `ProductItem` currently only tracks `id`, `completed`, and `history`. It doesn't have mutable properties like `temperature` or `weight` that sensors could measure.

## 3. Future Improvement Ideas
- **Dynamic Routing Engine**: Replace the linear flow with a graph-based routing system where items can have different "recipes" (paths).
- **Physical Visualization**: A 2D or 3D graphical dashboard (JavaFX or Web-based) to see items physically moving on conveyors.
- **Predictive Maintenance**: Use the `Sensor` data to predict when a `Machine` is about to hit an `ERROR` state and trigger a repair before it fails.
- **Networked Simulation**: Allow multiple simulation instances to connect over a network (e.g., simulating two different factory buildings collaborating on a product).
- **Product Properties**: Extend `ProductItem` with `temperature` and `weight` fields that machines can modify and sensors can read.
- **Additional Sensor Types**: Add `PressureSensor`, `VibrationSensor`, `HumiditySensor` using the existing abstract `Sensor` framework.

## 4. How the Architecture Enables These
The **Event-Driven** and **Object-Oriented** nature makes most of these easy:
- For **Power Consumption**, we just add a new subscriber to `MachineProcessingStartedEvent` that increments a "wattage" counter.
- For **Graphics**, we add a `UIObserver` that listens to `ConveyorItemAddedEvent` and `ProductDeliveredEvent` to update a sprite's position on a screen.
- For **Recipes**, we can modify the `ProductItem` to carry its own "Target Station List," and `ProductionLineService` will just read that list instead of using a global sequence.
- For **New Sensors**, we simply extend the abstract `Sensor` class and create corresponding event types—no changes to existing code required (Open-Closed Principle).
