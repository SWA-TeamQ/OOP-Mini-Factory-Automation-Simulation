package org.Automation.engine;

import java.time.LocalDateTime;


public interface ClockObserver {
  void onTick(LocalDateTime currentSecond);
}