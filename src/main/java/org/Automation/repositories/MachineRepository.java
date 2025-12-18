package org.automation.repositories;
import org.automation.core.DatabaseManager;
import org.automation.entities.Machine;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MachineRepository<T> extends Repository<Machine>{
    public MachineRepository(DatabaseManager db) {
        super("Machine", db);
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS Machine (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    status TEXT DEFAULT 'idle',
                    lastMaintenanceDate TEXT
            );
            """;
    }

    @Override
    protected T mapRow(ResultSet rs) throws SQLException {
        return new T(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("status"),
                rs.getString("lastMaintenanceDate")
        );
    }
}
