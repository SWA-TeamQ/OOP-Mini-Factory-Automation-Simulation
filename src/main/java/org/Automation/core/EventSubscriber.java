package org.automation.core;

import org.automation.events.Event;

public interface EventSubscriber {
    void onEvent(Event event);
}
