package org.Automation.services;

import org.Automation.entities.Actuator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ActuatorService implements IActuatorService {

  private final CopyOnWriteArrayList<Actuator> actuators = new CopyOnWriteArrayList<>();

  @Override
  public void onTick(LocalDateTime currentSecond) {
    for (Actuator a : actuators) {
      if (a.canPerformAction(currentSecond)) {
        a.performAction(currentSecond);
        a.updateLastActionTime(currentSecond);
      }
    }
  }

  @Override
  public void register(Actuator actuator) {
    if (actuator == null)
      return;
    actuators.removeIf(a -> a.getId() == actuator.getId());
    actuators.add(actuator);
    System.out.println("[ActuatorService] registered actuator: " + actuator.getId());
  }

  @Override
  public void activate(int id) {
    actuators.forEach(a -> {
      if (a.getId() == id) {
        a.activate();
        System.out.println("[ActuatorService] activated " + id);
      }
    });
  }

  @Override
  public void deactivate(int id) {
    actuators.forEach(a -> {
      if (a.getId() == id) {
        a.deactivate();
        System.out.println("[ActuatorService] deactivated " + id);
      }
    });
  }

  @Override
  public List<Actuator> getActuators() {
    return Collections.unmodifiableList(actuators.stream().collect(Collectors.toList()));
  }

}
