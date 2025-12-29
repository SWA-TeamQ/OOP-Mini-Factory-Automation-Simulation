package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.core.EntityFactory;
import org.Automation.core.EventBus;
import org.Automation.entities.ConveyorBelt;
import java.sql.ResultSet;
import java.sql.SQLException;

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
