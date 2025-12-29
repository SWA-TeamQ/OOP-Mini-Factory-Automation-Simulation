package org.automation.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.automation.core.DatabaseManager;
import org.automation.core.EntityFactory;
import org.automation.core.EventBus;
import org.automation.entities.ConveyorBelt;

public class ConveyorRepository extends Repository<ConveyorBelt> {

    public ConveyorRepository(DatabaseManager db, EventBus eventBus) {
        super("ConveyorBelt", db);
        // eventBus unused
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS ConveyorBelt (
                    id TEXT PRIMARY KEY,
                    capacity INTEGER,
                    duration INTEGER
                );
                """;
    }

    @Override
    protected ConveyorBelt mapRow(ResultSet rs) throws SQLException {
        return EntityFactory.createConveyor(
                rs.getString("id"),
                rs.getInt("capacity"),
                rs.getInt("duration"));
    }

    @Override
    public void save(ConveyorBelt conveyor) {
        String[] columns = { "id", "capacity", "duration" };
        Object[] values = {
                conveyor.getId(),
                conveyor.getCapacity(),
                conveyor.getTransferDurationTicks()
        };
        db.insert(tableName, columns, values);
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[] { id });
    }
}
