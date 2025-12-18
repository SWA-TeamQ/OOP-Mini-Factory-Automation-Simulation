package org.automation.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.automation.entities.Actuator;

/**
 * Simple in-process actuator registry and runner.
 * Keeps actuators passive; controller schedules actions via activate/deactivate.
 */
public class ActuatorService implements IActuatorService {

    private final CopyOnWriteArrayList<Actuator> actuators = new CopyOnWriteArrayList<>();

    @Override
    public void onTick(LocalDateTime time) {
        // Walk actuators and perform per-tick maintenance if needed.
        for (Actuator a : actuators) {
            // Actuator implementation is intentionally simple; place hooks here.
            if (a.getStatus()) {
                // Example heartbeat log
                System.out.println("[ActuatorService] actuator " + a.getId() + " active at " + time);
            }
        }
    }

    @Override
    public void listActuators() {
        System.out.println("Registered actuators:");
            for (Actuator a : actuators) {
            System.out.println(" - id=" + a.getId() + " active=" + a.getStatus());
        }
    }

    @Override
    public void registerActuator(Actuator actuator) {
        if (actuator == null) return;
        actuators.removeIf(a -> a.getId().equals(actuator.getId()));
        actuators.add(actuator);
        System.out.println("[ActuatorService] registered actuator: " + actuator.getId());
    }

    @Override
    public void activate(String id) {
        actuators.stream()
                 .filter(a -> a.getId().equals(id))
                 .findFirst()
                 .ifPresent(a -> {
                     a.activate();
                     System.out.println("[ActuatorService] activated " + id);
                 });
    }

    @Override
    public void deactivate(String id) {
        actuators.stream()
                 .filter(a -> a.getId().equals(id))
                 .findFirst()
                 .ifPresent(a -> {
                     a.deactivate();
                     System.out.println("[ActuatorService] deactivated " + id);
                 });
    }

    @Override
    public List<Actuator> getActuators() {
        return Collections.unmodifiableList(actuators.stream().collect(Collectors.toList()));
    }
}
