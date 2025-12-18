package org.automation.services;

import java.time.LocalDateTime;

public interface ISensorService {
    void onTick(LocalDateTime time);
    void start();
    void stop();
    String getStatus();
}
