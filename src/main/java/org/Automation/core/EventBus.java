package org.automation.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Very small in-process event bus used for decoupling services.
 * - Singleton (use EventBus.getInstance())
 * - Consumers can register for event types
 * - publish(event) will deliver to matching listeners
 *
 * Keep intentionally minimal; replace with a proper messaging implementation
 * (e.g. RxJava / Reactor / Guava EventBus) if requirements grow.
 */
public class EventBus {
    private static final EventBus INSTANCE = new EventBus();

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();

    private EventBus() {}

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public <T> void register(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                 .add((Consumer<Object>) listener);
    }

    public <T> void unregister(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<Object>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
        }
    }

    public void publish(Object event) {
        if (event == null) return;
        Class<?> emitted = event.getClass();

        // Deliver to listeners registered for super types as well
        listeners.forEach((type, consumers) -> {
            if (type.isAssignableFrom(emitted)) {
                for (Consumer<Object> c : consumers) {
                    try {
                        c.accept(event);
                    } catch (Exception e) {
                        System.err.println("[EventBus] listener error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
