# 🎓 QA & Project Defense Preparation

This document prepares you for common questions your teacher or an evaluator might ask during the project presentation.

## 1. "How do you handle two machines finishing at the exact same time?"
- **Answer**: "The `EventBus` handles events sequentially. Even if two machines publish at the same millisecond, the bus processes their callbacks one by one. Furthermore, our `SimulationClock` runs on a single thread (`Executors.newSingleThreadScheduledExecutor()`), which guarantees that `tick()` signals are processed in a deterministic, serial order."

## 2. "What happens if a machine breaks down while a product is inside?"
- **Answer**: "The `Machine` enters the `ERROR` status and stops calling `processingTicksRemaining--` in its `tick()` method. The `ProductItem` remains 'stuck' inside that machine entity until a `repair()` command is issued (via the UI or a Service), which transitions the machine back to `IDLE` or `RUNNING`, allowing processing to resume."

## 3. "Why use an EventBus instead of just calling `station.nextStation()`?"
- **Answer**: "Decoupling. If we used direct calls, adding a new type of station would require modifying the code of all existing stations. With the `EventBus`, a station just says 'I have a product ready,' and anyone—a logger, a tracking service, or a conveyor—can react to that without the station needing to know who they are."

## 4. "Is this simulation real-time or faster than real-time?"
- **Answer**: "It's hybrid. The `SimulationClock` has a `speedFactor`. By default, it's real-time (1 tick = 50ms), but we can increase the factor so that the `currentSecond` in the simulation advances faster than the wall-clock time on the computer, allowing us to simulate a 24-hour factory run in just a few minutes."

## 5. "How would you scale this to 10,000 machines?"
- **Answer**: "Currently, the bottleneck would be the single-threaded `EventBus`. To scale, we would:
  1. Distribute the `EventBus` (using something like RabbitMQ or Kafka).
  2. Use a clustered database instead of a local SQLite file.
  3. Divide the simulation into 'Zones', where each zone has its own local Clock, synchronized by a master clock signal."

## 6. "What is the role of the `WorkFlowController`?"
- **Answer**: "It's the 'System Brain'. It doesn't perform the physical work, but it holds all the pieces together. It initializes the repositories, wires the services, seeds the initial data, and provides the API that the UI uses to control the simulation."
