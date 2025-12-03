package org.Automation.controllers;

import java.time.LocalDateTime;

/**
 * ClockObserver - Interface for receiving simulation clock notifications
 */
public interface ClockObserver {
    void onTick(LocalDateTime currentTime);
}


