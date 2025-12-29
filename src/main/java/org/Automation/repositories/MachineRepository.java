package org.automation.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.automation.core.DatabaseManager;
import org.automation.core.EntityFactory;
import org.automation.core.EventBus;
import org.automation.entities.Machine;

public class MachineRepository extends Repository<Machine> {

    public MachineRepository(DatabaseManager db, EventBus eventBus) {
        super("Machine", db);
        // eventBus unused
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS Machine (
                    id TEXT PRIMARY KEY,
                    type TEXT NOT NULL,
                    status TEXT DEFAULT 'IDLE'
                );
                """;
    }

    @Override
    protected Machine mapRow(ResultSet rs) throws SQLException {
        return EntityFactory.createMachine(
                rs.getString("type"),
                rs.getString("id"),
                null, // Name not in table yet
                rs.getString("status"));
    }

    @Override
    public void save(Machine machine) {
        String[] columns = { "id", "type", "status" };
        Object[] values = {
                machine.getId(),
                machine.getType().toString(),
                machine.getStatus().toString()
        };
        db.insert(tableName, columns, values);
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[] { id });
    }
}
