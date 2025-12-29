package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.core.EntityFactory;
import org.Automation.core.EventBus;
import org.Automation.entities.Sensor;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SensorRepository extends Repository<Sensor> {
    private final EventBus eventBus;

    public SensorRepository(DatabaseManager db, EventBus eventBus) {
        super("Sensor", db);
        this.eventBus = eventBus;
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS Sensor (
                    id TEXT PRIMARY KEY,
                    locationId TEXT,
                    type TEXT,
                    threshold REAL,
                    samplingRate INTEGER
                );
                """;
    }

    @Override
    protected Sensor mapRow(ResultSet rs) throws SQLException {
        return EntityFactory.createSensor(
                rs.getString("id"),
                rs.getString("locationId"),
                rs.getString("type"),
                rs.getDouble("threshold"),
                eventBus);
    }

    @Override
    public void save(Sensor sensor) {
        String[] columns = { "id", "locationId", "type", "threshold", "samplingRate" };
        Object[] values = {
                sensor.getId(),
                sensor.getLocationId(),
                sensor.getType(),
                sensor.getThreshold(),
                sensor.getSamplingRateTicks()
        };
        db.insert(tableName, columns, values);
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[] { id });
    }
}
