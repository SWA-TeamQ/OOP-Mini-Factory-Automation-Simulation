package org.automation.engine;

/**
 * Single shared clock observer interface used by SimulationClock and services/controllers.
 */
public interface ClockObserver {
    void onTick(double currentSecond);
}
