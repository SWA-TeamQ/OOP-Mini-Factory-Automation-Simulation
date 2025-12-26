package org.automations.services;

import java.time.LocalDateTime;

public interface IItemTrackingService {
  void onTick(LocalDateTime time);

  void start();

  void stop();

  String getStatus();
}
