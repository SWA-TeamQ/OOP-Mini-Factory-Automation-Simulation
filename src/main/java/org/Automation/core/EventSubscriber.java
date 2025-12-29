package org.Automation.core;

import org.Automation.events.Event;

public interface EventSubscriber {
    void onEvent(Event event);
}
