package org.Automation.services;

import org.Automation.entities.Actuator;

import java.time.LocalDateTime;
import java.util.List;

public interface IActuatorService {
  void onTick(LocalDateTime currentSecond);

  void register(Actuator actuator);

  void activate(int id);

  void deactivate(int id);

  List<Actuator> getActuators();
}
