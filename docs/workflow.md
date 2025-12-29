# 🔄 Workflow & Runtime Behavior

## 1. Simulation Startup Sequence
The system initialization follows a strict "Wiring" phase:
1. **Core Setup**: `EventBus` and `DatabaseManager` are initialized.
2. **Data Layer**: Repositories are created and connected to the DB.
3. **Logic Layer**: Services (`ItemTracking`, `ProductionLine`, `MachineService`, etc.) are instantiated and cross-injected.
4. **Seeding**: If the database is empty, `WorkFlowController` uses the `EntityFactory` to create a default layout (Stations, Machines, Sensors).
5. **UI Launch**: The `ConsoleApp` starts the user interaction loop.
6. **Clock Signal**: Once "Start Production" is selected, the `SimulationClock` begins ticking.

## 2. The Heartbeat: Simulation Clock & Ticks
The `SimulationClock` is a singleton running on a `ScheduledExecutorService`.
- **Interval**: Fixed at 50ms.
- **Logical Tick**: An incrementing counter used by components to measure duration.
- **Tick Propagation**: 
  - `Machine` uses ticks to count down processing time.
  - `ConveyorBelt` uses ticks to calculate when an item reaches the end.
  - `Sensor` uses ticks to determine its sampling frequency.

## 3. Product Lifecycle: Step-by-Step
A `ProductItem` follows this standard pipeline:

1. **Registration**: An item is created and saved to the `ProductItemRepository`.
2. **Input Entry**: The item enters the `InputStation`. 
3. **Routing**: `ProductionLineService` determines the next station in the sequence.
4. **Transition**: The item is moved to a `ConveyorBelt`. 
5. **Processing**:
   - The item arrives at a `ProductionStation`.
   - The Station assigns it to an available `Machine` (if `RUNNING`).
   - The Machine transitions to `BUSY`, processing for a set number of ticks.
6. **Completion**:
   - Once the Machine finishes, it signals the Station.
   - The item is marked as "Processed" in its history.
7. **Packaging & Exit**: The item eventually reaches the `PackagingStation`, is marked as `completed`, and is "removed" from the active line for tracking.

## 4. Station, Machine, and Conveyor Interactions

| Stage | Action | Trigger |
| :--- | :--- | :--- |
| **Arrival** | Station receives item | `product_delivered` event |
| **Assignment** | Machine picks up item | `processItems()` logic in Station |
| **Duration** | Machine/Conveyor ticks | `SimulationClock` |
| **Departure** | Item enters conveyor | `product_ready_for_transfer` event |

## 5. Queue & State Management
- **Stations**: Act as buffers. If all machines are busy, items wait in the `itemsInStation` list.
- **Machines**: Strictly manage state: `IDLE` ↔ `RUNNING` ↔ `BUSY`. If a machine hits an `ERROR` state, the station stops assigning items to it until `repaired`.
- **Transfers**: Items are never "teleported". They always spend time on a `ConveyorBelt`, which enforces physical capacity limits.

## 6. End-to-End Walkthrough (ASCII Flow)
```text
[ SOURCE ] -> (Item Created)
    ↓
[ INPUT STATION ] -> [ CONVEYOR 1 ]
    ↓                       ↓
[ PRODUCTION STATION ] <----'
    │  ↳ (Machine M-01: Processing...)
    ↓
[ CONVEYOR 2 ] -> [ PACKAGING STATION ]
                          │  ↳ (Machine M-02: Packing...)
                          ↓
                   [ STORAGE / EXIT ] -> (Item Completed)
```
