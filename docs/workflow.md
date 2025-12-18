---
marp: true
---

# Mini Factory Automation Simulation — Workflow

## Purpose
This document describes the runtime workflow of the simulation and explains each major component: what it is, what it does, how it maps to a real factory, and how components interact. Use this as a developer reference for extending, testing, and integrating new features.

---

## High-level overview
- The simulation is a time-driven system. A single time source (SimulationClock / SimulationEngine) emits ticks.
- A central orchestrator (WorkflowController) receives ticks and coordinates services in a defined order so the system behaves deterministically.
- Services implement domain responsibilities (sensors, production line, actuators, item tracking). Repositories persist state.
- EventBus is an in-process message hub that decouples producers (sensors, services) from consumers (analytics, actuators, monitors).
- Entities model real-world equipment (Machine, ConveyorBelt, Station, Sensor, Actuator, ProductItem, Worker).

---

## Components and responsibilities

- SimulationClock / SimulationEngine
  - What: Single time source that advances simulated time and notifies subscribers.
  - How: Runs a scheduled executor and raises tick events (seconds-level notifications).
  - Real world mapping: Factory clock / control PLC scan cycle.
  - Notes: Keep the clock single-instance and deterministic for reproducible runs.

--- 

- WorkflowController
  - What: Orchestrator that registers with the clock and runs the per-tick sequence.
  - Responsibilities:
    1. Trigger SensorService.onTick(time)
    2. Trigger ProductionLineService.onTick(time)
    3. Trigger ActuatorService.onTick(time)
    4. Trigger ItemTrackingService.onTick(time) / logging
  - Real world mapping: MES / SCADA decision loop.

---

- SensorService (ISensorService)
  - What: Reads/simulates sensors each tick and publishes sensor events.
  - Responsibilities: manage sensor registry, sample values, emit events via EventBus.
  - Real world mapping: physical sensors on conveyors/machines.

---

- ProductionLineService (IProductionLineService)
  - What: Handles item movement, station processing, routing decisions.
  - Responsibilities: move ProductItems between Station objects, interact with Machine/Station logic, consult actuators when needed (through ActuatorService).
  - Real world mapping: conveyor logic, machine processing, work cell coordination.

---

- ActuatorService (IActuatorService)
  - What: Registry & runtime for actuators (valves, motors, pushers).
  - Responsibilities: apply commands (activate/deactivate), run per-tick checks, expose list/management API.
  - Real world mapping: output control modules, relays, motor drives.

---

- ItemTrackingService (IItemTrackingService)
  - What: Periodic snapshot and reporting of item locations, statuses and metrics.
  - Responsibilities: capture telemetry, persist metrics (DB), support playback/analysis.
  - Real world mapping: traceability / OPC UA historian.

---

- EventBus (org.automation.core.EventBus)
  - What: Lightweight publish/subscribe hub for in-process events.
  - Responsibilities: register/unregister listeners, publish events by type.
  - Real world mapping: message bus / internal eventing / alarm pipeline.

---

- Repositories (org.automation.repositories)
  - What: Abstractions for persistence (conveyor, machine, product item, sensor, worker).
  - Responsibilities: read/write entity state; service layer is the only consumer of repositories.
  - Real world mapping: factory database / historian.

---

- DatabaseManager, Logger, UI
  - DB: persistence layer for results; keep it isolated behind Repository interfaces.
  - Logger: consistent logging facility for debugging and audit.
  - ConsoleUI/ConsoleApp: simple front end to start/stop simulation and inspect system.

---

## Typical per-tick sequence (deterministic order)
1. SimulationClock advances time and notifies subscribers.
2. WorkflowController.onTick(time) executes.
3. SensorService.onTick(time)
   - Samples every Sensor; publishes SensorEvent on EventBus.
4. ProductionLineService.onTick(time)
   - Moves ProductItems along ConveyorBelts / between Stations.
   - Decides actions for Machines (request actuators via ActuatorService or publish commands).

---

5. ActuatorService.onTick(time)
   - Executes pending actuator commands, updates actuator state.
6. ItemTrackingService.onTick(time)
   - Persists snapshots/metrics; emits telemetry if required.
7. Background tasks (DB writes, UI refresh) process asynchronously where appropriate.

---

## Event flow examples
- Sensor detects item at station → SensorService publishes event → ProductionLineService subscribed handler updates item position/status → if action required, WorkflowController requests ActuatorService.activate(id).
- ProductionLineService detects jam → publishes alert event → UI/Logger subscribes and notifies operator.

---

## Extension points & best practices
- Services expose small, stable interfaces (IProductionLineService, ISensorService, IActuatorService, IItemTrackingService). Prefer interface-driven wiring in controllers.
- Use EventBus for cross-cutting notifications rather than direct method calls when coupling should be reduced.
- Keep Entities POJOs; domain logic belongs in Services.
- Add unit tests for each service (mock repositories and EventBus). Create integration tests using a deterministic SimulationClock instance.

---

## Concurrency model & determinism
- Ticks are single-threaded in the controller’s per-tick sequence to preserve deterministic behavior.
- Long-running tasks (DB writes, reports) should be queued and executed off the critical tick path.
- When adding multithreading, use immutable snapshots or synchronized access to avoid race conditions.

---

## Testing & CI
- Unit test services in isolation (mock repositories, replace EventBus with test stub).
- Integration test: run a short deterministic simulation with a fixed SimulationClock and assert item positions/state after N ticks.
- CI (GitHub Actions) should run `mvn -DskipTests package` and a unit test suite.

---

## Recommended next steps
- Implement business logic inside service scaffolds (movement rules, machine processing times).
- Add unit tests and integration scenarios (start/stop, failure modes).
- Add CONTRIBUTING.md with package and interface rules to help collaboration.
