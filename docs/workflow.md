# 🔄 Workflow & Runtime Behavior

## 1. Simulation Startup Sequence
The system initialization follows a strict "Wiring" phase:
1. **Core Setup**: `EventBus` and `DatabaseManager` are initialized.
2. **Data Layer**: Repositories are created and connected to the DB.
3. **Logic Layer**: Services (`ItemTrackingService`, `ProductionLineService`, `MachineService`, `SensorService`, `ConveyorService`) are instantiated and cross-injected.
4. **Seeding**: If the database is empty, `WorkFlowController` uses the `EntityFactory` to create a default layout (Stations, Machines, Sensors).
5. **UI Launch**: The `ConsoleApp` starts the user interaction loop.
6. **Clock Signal**: Once "Start Production" is selected, the `SimulationClock` begins ticking.

## 2. The Heartbeat: Simulation Clock & Ticks
The `SimulationClock` is a singleton running on a `ScheduledExecutorService`.
- **Interval**: Fixed at 50ms.
- **Logical Tick**: An incrementing counter used by components to measure duration.
- **Tick Propagation**: 
  - `Machine` uses ticks to count down `processingTicksRemaining`.
  - `ConveyorBelt` uses ticks to calculate when an item reaches the end.
  - `TemperatureSensor` / `WeightSensor` use ticks to determine sampling frequency via `samplingRateTicks`.

## 3. Product Lifecycle: Step-by-Step
A `ProductItem` follows this standard pipeline:

1. **Registration**: An item is created and saved to the `ProductItemRepository`.
2. **Input Entry**: The item enters the `InputStation` via `onProductArrived()`.
3. **Queue Placement**: Item is added to the station's `waitingQueue` (FIFO).
4. **Machine Assignment**: `processQueue()` assigns the item to an `IDLE` machine.
5. **Processing**:
   - The Machine transitions to `BUSY`, processing for `totalProcessingTicks` + random variation.
   - Machine may fail with 0.2% chance per tick → publishes `MachineErrorEvent`.
6. **Completion**:
   - Machine publishes `MachineProcessingFinishedEvent` + `ProductReadyForTransferEvent`.
   - Station removes item from processing, triggers queue for next item.
7. **Transfer**:
   - `ProductionLineService` handles `ProductReadyForTransferEvent`.
   - Item placed on `ConveyorBelt` → publishes `ConveyorItemAddedEvent`.
8. **Arrival**:
   - Conveyor publishes `ProductDeliveredEvent` when transit time completes.
   - `ProductionLineService.handleArrival()` delivers item to next station.
9. **Packaging & Exit**: 
   - Item reaches `PackagingStation`, is processed, marked as `completed`.
   - `ItemCompletedEvent` is published.

## 4. Station, Machine, and Conveyor Interactions

| Stage | Action | Trigger |
| :--- | :--- | :--- |
| **Arrival** | Station receives item | `ProductDeliveredEvent` via `handleArrival()` |
| **Queueing** | Item added to `waitingQueue` | `onProductArrived()` method |
| **Assignment** | Machine picks up item | `processQueue()` finds IDLE machine |
| **Processing** | Machine ticks countdown | `SimulationClock.tick()` |
| **Completion** | Machine finishes | `MachineProcessingFinishedEvent` |
| **Departure** | Item enters conveyor | `ProductReadyForTransferEvent` triggers `handleTransfer()` |

## 5. Queue & State Management
- **Stations**: Use a FIFO `waitingQueue`. If all machines are busy, items wait in the queue. When any machine finishes (`ProductReadyForTransferEvent`), the queue is re-processed.
- **Machines**: Strictly manage state: `IDLE` ↔ `BUSY` ↔ `STOPPED` ↔ `ERROR`. If a machine hits `ERROR`, the station stops assigning items to it until `repair()` is called.
- **Transfers**: Items are never "teleported". They always spend time on a `ConveyorBelt`, which enforces physical capacity limits.

## 6. End-to-End Walkthrough (ASCII Flow)
```text
[ SOURCE ] -> (Item Created)
    ↓
[ INPUT STATION ] -> waitingQueue -> Machine Assignment
    │                                     ↓
    │                           (Machine Processing...)
    ↓
[ CONVEYOR 1 ] <- ProductReadyForTransferEvent
    ↓
[ PRODUCTION STATION ] <- ProductDeliveredEvent
    │  ↳ waitingQueue -> Machine Assignment
    │                         ↓
    │              (Machine Processing...)
    ↓
[ CONVEYOR 2 ] <- ProductReadyForTransferEvent
    ↓
[ PACKAGING STATION ] <- ProductDeliveredEvent
    │  ↳ waitingQueue -> Machine Assignment
    │                          ↓
    │               (PackagingMachine...)
    ↓
[ STORAGE / EXIT ] -> ItemCompletedEvent (Item Completed)
```
