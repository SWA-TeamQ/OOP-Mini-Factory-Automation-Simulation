package org.automation.core.interfaces;

import org.automation.events.abstracts.Event;

public interface EventSubscriber {
    void onEvent(Event event);
}
