# Mini Factory Automation Simulation — Current Architecture

## Snapshot (what is implemented now)
- Project follows Maven layout under `src/main/java/org/automation`.
- Core packages present:
  - org.automation.controllers (WorkflowController)
  - org.automation.services (IProductionLineService, ISensorService, IItemTrackingService, ActuatorService, ProductionLineService, SensorService, ItemTrackingService)
  - org.automation.entities (Machine, Station, Actuator, PackagingMachine, ProductItem, Sensor, Worker, ConveyorBelt)
  - org.automation.repositories (Repository implementations)
  - org.automation.engine (SimulationClock, SimulationEngine)
  - org.automation.core (DatabaseManager, EventBus, Logger)
  - org.automation.ui (ConsoleApp / ConsoleUI)
- GitHub Actions CI workflow added at `.github/workflows/maven.yml`.
- docs/architecture.md and ARCHITECTURE overview present.

## Key design decisions already applied
- Controllers are orchestrators; WorkflowController receives simulation ticks and delegates work.
- Actuators were decoupled from SimulationClock: Actuator classes no longer implement clock observers. Time/ scheduling moved to WorkflowController / services.
- Service interfaces updated to include lifecycle and tick hooks:
  - IProductionLineService: onTick(start/stop/processPending/getStatus)
  - ISensorService: onTick/start/stop/getStatus
  - IItemTrackingService: onTick/start/stop/getStatus
- ActuatorService scaffolded: central registry and command API for actuators (activate/deactivate/list & onTick hook).
- Duplicate top-level folders (Actuator/, ActuatorManager/, PackagingMachine/) were removed and code consolidated under `src/main/java/org/automation`.
- EventBus exists as an infra concept; SensorService is intended to publish sensor events.

## Current responsibilities (who does what)
- SimulationClock / SimulationEngine: emit ticks (time source).
- WorkflowController: registers with SimulationClock and coordinates per-tick sequence:
  1. sensorService.onTick(time)
  2. productionLine.onTick(time)
  3. actuatorService.onTick(time)
  4. itemTracking snapshot/logging as needed
- Services: implement domain logic and access repositories. Controllers call service interfaces only.
- Repositories: encapsulate persistence; only services use them.
- Entities: plain domain objects (no scheduling/time responsibility).

## Remaining actions (short-term)
- Consolidate duplicate Actuator file (decide canonical package: `entities` is recommended) and remove the duplicate `core/Actuator.java`.
- Implement service method bodies (ProductionLineService, SensorService, ItemTrackingService) to satisfy interface contracts used by WorkflowController.
- Add minimal unit-test skeletons under `src/test/java` for services and controllers.
- Finalize EventBus wiring: SensorService publishes events; WorkflowController / ActuatorService / ProductionLineService subscribe.
- Run CI (GitHub Actions) and fix compile/runtime issues reported.
- Add CONTRIBUTING.md with code style, package rules, and CI expectations.

## Recommended canonical package policy
- Domain entities: `org.automation.entities`
- Services/interfaces: `org.automation.services` (interfaces named `I*`, impls without `I`)
- Controllers: `org.automation.controllers`
- Infrastructure (DB, EventBus, Logger): `org.automation.core`
- UI/engine: `org.automation.ui`, `org.automation.engine`
- Repositories: `org.automation.repositories`

## Checklist (current to-do)
- [ ] Consolidate duplicate Actuator implementation (keep `entities/Actuator`)
- [ ] Implement missing service methods (onTick/start/stop/processPending/getStatus)
- [ ] Wire EventBus and sensor events
- [ ] Add unit tests under `src/test/java`
- [ ] Run CI and resolve errors
- [ ] Add CONTRIBUTING.md and short onboarding notes
