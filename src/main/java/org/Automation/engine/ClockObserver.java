package org.Automation.engine;

import java.time.LocalDateTime;

/**
 * Single shared clock observer interface used by SimulationClock and
 * services/controllers.
 */
public interface ClockObserver {
  void onTick(LocalDateTime currentSecond);
}
