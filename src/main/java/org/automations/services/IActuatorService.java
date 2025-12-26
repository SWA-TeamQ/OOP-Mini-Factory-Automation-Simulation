package org.automations.services;

import java.time.LocalDateTime;
import java.util.List;

import org.automations.entities.Actuator;

public interface IActuatorService {
  void onTick(LocalDateTime currentSecond);

  void register(Actuator actuator);

  void activate(int id);

  void deactivate(int id);

  List<Actuator> getActuators();
}
