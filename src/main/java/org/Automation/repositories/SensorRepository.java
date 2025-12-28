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
                    type TEXT,
                    machineId TEXT,
                    status TEXT DEFAULT 'inactive'
                );
                """;
    }

    @Override
    protected Sensor mapRow(ResultSet rs) throws SQLException {
        return EntityFactory.createSensor(
            rs.getString("id"),
            rs.getString("type"),
            rs.getString("machineId"),
            rs.getString("status"),
            eventBus
        );
    }

    @Override
    public void save(Sensor sensor) {
        String[] columns = {"id", "type", "machineId", "status"};
        Object[] values = {
            sensor.getId(),
            null, // type not in entity yet
            null, // machineId not in entity yet
            null  // status not in entity yet
        };
        
        if (!db.update(tableName, "type=?, machineId=?, status=?", "id=?", new Object[]{values[1], values[2], values[3], values[0]})) {
            db.insert(tableName, columns, values);
        }
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[]{id});
    }
}
