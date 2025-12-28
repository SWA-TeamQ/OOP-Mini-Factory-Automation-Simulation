package org.Automation.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    private final HashMap<String, List<Consumer<Object>>> listeners = new HashMap<>();

    public void subscribe(String eventType, Consumer<Object> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void publish(String eventType, Object payload) {
        if (listeners.containsKey(eventType)) {
            for (Consumer<Object> listener : listeners.get(eventType)) {
                listener.accept(payload);
            }
        }
    }
}
