package org.Automation.repositories;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Repository<T> {
    protected final Map<String, T> storage = new ConcurrentHashMap<>();

    public void add(String id, T item) { storage.put(id, item); }
    public void remove(String id) { storage.remove(id); }
    public T findById(String id) { return storage.get(id); }
    public Collection<T> findAll() { return storage.values(); }
    public boolean exists(String id) { return storage.containsKey(id); }

    public abstract void save(T entity);
    public abstract void delete(String id);
}
