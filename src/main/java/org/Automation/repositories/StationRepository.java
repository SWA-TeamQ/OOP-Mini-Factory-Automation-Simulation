package org.automation.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.automation.core.DatabaseManager;
import org.automation.core.EntityFactory;
import org.automation.core.EventBus;
import org.automation.entities.Station;

import java.util.ArrayList;

public class StationRepository extends Repository<Station> {
    private final EventBus eventBus;

    // In-memory cache for stations with machines wired
    private final Map<String, Station> stationCache = new HashMap<>();

    public StationRepository(DatabaseManager db, EventBus eventBus) {
        super("Station", db);
        this.eventBus = eventBus;
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS Station (
                    id TEXT PRIMARY KEY,
                    type TEXT NOT NULL,
                    status TEXT DEFAULT 'IDLE'
                );
                """;
    }

    @Override
    protected Station mapRow(ResultSet rs) throws SQLException {
        return EntityFactory.createStation(
                rs.getString("type"),
                rs.getString("id"),
                rs.getString("status"),
                eventBus);
    }

    /**
     * Adds a station to the cache (with machines already wired).
     */
    public void cacheStation(Station station) {
        stationCache.put(station.getId(), station);
    }

    @Override
    public Station findById(String id) {
        // Return from cache if available (has machines wired)
        if (stationCache.containsKey(id)) {
            return stationCache.get(id);
        }
        return super.findById(id);
    }

    @Override
    public List<Station> findAll() {
        // If cache is populated, return from cache (with machines)
        if (!stationCache.isEmpty()) {
            return new ArrayList<>(stationCache.values());
        }
        return super.findAll();
    }

    @Override
    public void save(Station station) {
        String[] columns = { "id", "type", "status" };
        Object[] values = {
                station.getId(),
                station.getClass().getSimpleName().replace("Station", ""),
                station.getStatus().toString()
        };
        db.insert(tableName, columns, values);

        // Also cache for future use
        cacheStation(station);
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[] { id });
        stationCache.remove(id);
    }
}
