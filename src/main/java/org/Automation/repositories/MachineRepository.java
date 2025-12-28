package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.core.EntityFactory;
import org.Automation.core.EventBus;
import org.Automation.entities.Machine;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MachineRepository extends Repository<Machine> {
    private final EventBus eventBus;

    public MachineRepository(DatabaseManager db, EventBus eventBus) {
        super("Machine", db);
        this.eventBus = eventBus;
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
            rs.getString("status"),
            eventBus
        );
    }

    @Override
    public void save(Machine machine) {
        String[] columns = {"id", "type", "status"};
        Object[] values = {
            machine.getId(),
            machine.getType().toString(),
            machine.getStatus().toString()
        };
        db.insert(tableName, columns, values);
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[]{id});
    }
}
