package org.Automation.core;

import org.Automation.events.*;
import java.util.*;

public class EventBus {
    private final HashMap<String, List<EventSubscriber>> listeners = new HashMap<>();

    public void subscribe(String eventType, EventSubscriber listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void publish(Event event) {
        String eventType = event.getType();
        if (listeners.containsKey(eventType)) {
            for (EventSubscriber listener : listeners.get(eventType)) {
                listener.onEvent(event);
            }
        }
    }
}
