package org.automation.core;

import java.util.*;

import org.automation.core.interfaces.EventSubscriber;
import org.automation.events.*;
import org.automation.events.abstracts.Event;

public class EventBus {
    private final HashMap<String, List<EventSubscriber>> listeners = new HashMap<>();

    public void subscribe(String eventType, EventSubscriber listener) {
        listeners.computeIfAbsent(eventType, _ -> new ArrayList<>()).add(listener);
    }

    public void publish(Event event) {
        String eventType = event.getType();
        if (listeners.containsKey(eventType)) {
            Logger.info(event.toString());
            for (EventSubscriber listener : listeners.get(eventType)) {
                listener.onEvent(event);
            }
        }
    }
}
