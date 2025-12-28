package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.core.EntityFactory;
import org.Automation.entities.Station;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StationRepository extends Repository<Station> {

    public StationRepository(DatabaseManager db) {
        super("Station", db);
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
            rs.getString("status")
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
