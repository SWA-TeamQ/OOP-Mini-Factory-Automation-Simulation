# 🛠️ Extension Guide: Adding New Features

This guide explains how to extend the simulation with new hardware and logic.

## 1. Adding a New Machine Type
To add a new machine (e.g., a "3D Printer"):
1. **Define Type**: Add `PRINTER` to the `MachineType` enum.
2. **Subclass (Optional)**: If the printer has unique logic, create `PrinterMachine extends Machine`. Otherwise, just use the base `Machine` class.
3. **Register in Factory**: Update `EntityFactory.createMachine()` to handle the new type.
4. **Seed Data**: In `WorkFlowController.seedDataIfEmpty()`, create an instance of your printer and add it to a station.

## 2. Adding a New Sensor Type
To add a new sensor (e.g., a "Vibration Sensor"):
1. **Instance Creation**: Use `EntityFactory.createSensor("S-NEW", "Vibration", 50.0, 5, eventBus)`.
2. **Logic Integration**: 
   - Sensors are generic by default.
   - If you want the sensor to trigger a specific machine shutdown, create a new `Service` that subscribes to `threshold_exceeded` and calls `machineService.stopMachine()`.

## 3. Discovery & Registration
The system uses the **Repository Pattern** for discovery.
- All components must be saved to their respective `Repository`.
- `Station.getMachines()` and `Station.getSensors()` are the primary ways components are "discovered" during a simulation run.
- **Rule**: Never instantiate a machine and forget to `save()` it to the repository, or the DB and UI won't see it.

## 4. Registering in the Event Bus
If your new component needs to communicate:
- **Publishing**: Use `eventBus.publish("your_topic", payload)`.
- **Subscribing**: Add a subscription in your component's constructor or in `WorkFlowController.subscribeEvents()`.

## 5. Vital Rules to Avoid "Breaking" the Simulation
1. **Don't Block the Tick**: The `tick()` method in `Machine` or `Conveyor` must be fast. Never put `Thread.sleep()` or heavy DB calls inside a `tick()` method.
2. **Payload Consistency**: If you publish to an existing topic (e.g., `product_ready_for_transfer`), ensure your payload matches what the subscribers expect (e.g., a `ProductItem` object).
3. **Clock Registration**: Always call `SimulationClock.getInstance().registerTickable(this)` in the constructor of any time-dependent component.
4. **Stateless Events**: Events should contain *information*, not *behavior*. Pass IDs or Data Objects, not living service instances.
