package org.automation.engine;

import java.time.LocalDateTime;

public interface ClockObserver {
    void onTick(LocalDateTime currentTime);
}

