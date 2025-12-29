package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.core.EntityFactory;
import org.Automation.core.EventBus;
import org.Automation.entities.Station;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StationRepository extends Repository<Station> {
    private final EventBus eventBus;

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
            eventBus
        );
    }

    @Override
    public void save(Station station) {
        String[] columns = {"id", "type", "status"};
        Object[] values = {
            station.getId(),
            station.getClass().getSimpleName().replace("Station", ""),
            station.getStatus().toString()
        };
        db.insert(tableName, columns, values);
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[]{id});
    }
}
