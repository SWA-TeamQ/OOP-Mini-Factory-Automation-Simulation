# 🛠️ Extension Guide: Adding New Features

This guide explains how to extend the simulation with new hardware and logic.

## 1. Adding a New Machine Type
To add a new machine (e.g., a "3D Printer"):
1. **Define Type**: Add `PRINTER` to the `MachineType` enum with its associated `StationType`.
2. **Subclass (Optional)**: If the printer has unique logic (e.g., different processing behavior), create `PrinterMachine extends Machine`. Otherwise, just use the base `Machine` class with the new type.
3. **Register in Factory**: Update `EntityFactory.createMachine()` to handle the new type.
4. **Seed Data**: In `WorkFlowController.seedDataIfEmpty()`, create an instance of your printer and add it to a station.

## 2. Adding a New Sensor Type
To add a new sensor (e.g., a "Vibration Sensor"):

1. **Create Subclass**: Create `VibrationSensor extends Sensor`.
   ```java
   public class VibrationSensor extends Sensor {
       public VibrationSensor(String id, String locationId, double threshold, 
                              int samplingRateTicks, EventBus eventBus) {
           super(id, locationId, "Vibration", threshold, samplingRateTicks, eventBus);
       }
       
       @Override
       public void readValue() {
           lastValue = random.nextDouble() * 10.0; // Simulated vibration
           eventBus.publish(new VibrationReadingEvent(id, lastValue, "message"));
       }
       
       @Override
       public void validateReading() {
           if (lastValue > threshold) {
               eventBus.publish(new VibrationThresholdExceededEvent(id, lastValue, threshold));
           }
       }
       // ... implement other abstract methods
   }
   ```

2. **Create Event Classes**: Create `VibrationReadingEvent` and `VibrationThresholdExceededEvent` extending `Event`.
   ```java
   public class VibrationReadingEvent extends Event {
       private final String sensorId;
       private final double value;
       
       public VibrationReadingEvent(String sensorId, double value, String message) {
           super("VibrationReadingEvent", message);
           this.sensorId = sensorId;
           this.value = value;
       }
       // ... getters
   }
   ```

3. **Update Factory**: Add to `EntityFactory.createSensor()`.

4. **Subscribe for Action**: If you want the sensor to trigger a specific machine shutdown:
   ```java
   eventBus.subscribe("VibrationThresholdExceededEvent", event -> {
       if (event instanceof VibrationThresholdExceededEvent e) {
           machineService.stopMachine(affectedMachineId);
       }
   });
   ```

## 3. Discovery & Registration
The system uses the **Repository Pattern** for discovery.
- All components must be saved to their respective `Repository`.
- `Station.getMachines()` is the primary way machines are "discovered" during a simulation run.
- **Rule**: Never instantiate a machine and forget to `save()` it to the repository, or the DB and UI won't see it.

## 4. Creating New Event Types
When adding new events:
1. **Extend Event**: Create a class that extends `Event`.
2. **Set Type**: Call `super("YourEventType", source)` in the constructor.
3. **Add Payload**: Include relevant data as private final fields with getters.
4. **Optional Marker Interface**: Create or use existing marker interfaces (`MachineEvent`, `SensorEvent`, etc.) for grouping.

```java
public class CustomEvent extends Event {
    private final String customData;
    
    public CustomEvent(String customData) {
        super("CustomEvent", customData);
        this.customData = customData;
    }
    
    public String getCustomData() { return customData; }
}
```

## 5. Registering in the Event Bus
If your new component needs to communicate:
- **Publishing**: Use `eventBus.publish(new YourEvent(...))`.
- **Subscribing**: Add a subscription in your component's constructor or in `WorkFlowController.subscribeEvents()`.

## 6. Vital Rules to Avoid "Breaking" the Simulation
1. **Don't Block the Tick**: The `tick()` method in `Machine`, `Conveyor`, or `Sensor` must be fast. Never put `Thread.sleep()` or heavy DB calls inside a `tick()` method.
2. **Consistent Event Types**: When subscribing, use the exact event type string (e.g., `"MachineErrorEvent"`).
3. **Clock Registration**: Always call `SimulationClock.getInstance().registerTickable(this)` in the constructor of any time-dependent component.
4. **Stateless Events**: Events should contain *information*, not *behavior*. Pass IDs or Data Objects, not living service instances.
5. **Use instanceof**: When handling events, always verify the event type with `instanceof` before casting.
