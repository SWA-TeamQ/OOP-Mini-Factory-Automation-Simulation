package org.automation.core.interfaces;

import org.automation.events.Event;

public interface EventSubscriber {
    void onEvent(Event event);
}
