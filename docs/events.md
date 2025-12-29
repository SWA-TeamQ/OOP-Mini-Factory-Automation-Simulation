# 📡 Event System Documentation

The Event System is the nervous system of the simulation. It allows components to interact without tight coupling, making the system extensible and resilient.

## 1. Event Catalog

| Event Name | Producer | Payload | Description |
| :--- | :--- | :--- | :--- |
| `product_arrived` | `Station` | `String` (Msg) | Broadcast when an item enters a station's zone. |
| `processing_started` | `Machine` | `String` (Msg) | Fired when a machine begins work on an item. |
| `processing_completed` | `Machine` | `ProductItem` | Fired when a machine finishes its tick countdown. |
| `product_ready_for_transfer`| `Station` / `Machine` | `ProductItem` / `String` | Signals that an item can be moved to the next station. |
| `item_on_conveyor` | `ConveyorBelt` | `String` (Msg) | Fired when an item enters transit. |
| `product_delivered` | `ConveyorBelt` | `ProductItem` | Fired when an item reaches the end of a conveyor. |
| `item_completed` | `PackagingStation` | `ProductItem` | Fired when an item is fully packaged and exits. |
| `machine_error` | `Machine` | `String` (Msg) | Fired during random mechanical failures. |
| `measurement_taken` | `Sensor` | `String` (Msg) | Periodic data sampling broadcast. |
| `threshold_exceeded` | `Sensor` | `String` (Msg) | Alarm event if a sensor value passes its limit. |

## 2. Why Events? (Decoupling)
Instead of a `Machine` calling `Station.next()`, it simply shouts "I am done!".
- **Advantage**: The `Machine` doesn't need to know if it's in a `ProductionStation` or a `PackagingStation`.
- **Advantage**: Multiple observers can listen to the same event (e.g., the `Logger`, the `UI`, and the `ProductionLineService`) without the producer knowing they exist.

## 3. Propagation Model
1. **Machine** finishes -> Publishes `processing_completed`.
2. **Station** (Subscriber) -> Reacts to `processing_completed`, updates its internal count.
3. **Line Service** (Subscriber) -> Reacts to `product_ready_for_transfer`, moves item to Conveyor.
4. **Conveyor** (Clock-driven) -> Eventually publishes `product_delivered`.

## 4. Preventing Issues
- **Circular Events**: The system avoids circularity by ensuring that "Logic" events (Events) never directly trigger "Physical" ticks. Ticks come only from the `Clock`.
- **Invalid Events**: Heavy use of `instanceof` checks in subscribers (e.g., in `ProductionLineService`) ensures that if an event name is reused with a wrong payload, the system ignores it rather than crashing.
- **Duplicates**: Since the `SimulationClock` is the only source of truth for time, events are naturally ordered by the tick they occurred in.
