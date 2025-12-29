package org.Automation.core;

/**
 * Interface for components that need to react to the simulation clock.
 */
public interface Tickable {
    /**
     * Called on every tick of the simulation clock.
     * @param currentTick The current logical time step.
     */
    void tick(long currentTick);
}
