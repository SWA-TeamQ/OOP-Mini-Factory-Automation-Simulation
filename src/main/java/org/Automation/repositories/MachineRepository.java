package org.automation.repositories;
import org.automation.core.DatabaseManager;
import org.automation.entities.Machine;
import org.automation.entities.PackagingMachine;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MachineRepository extends Repository<Machine>{
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
                    status ENUM(IDLE, STOPPED, RUNNING, ) DEFAULT 'IDLE',
            );
            """;
    }

    @Override
    protected Machine mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String type = rs.getString("type");
        String status = rs.getString("status");

        PackagingMachine machine = new PackagingMachine();
        return new T(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("status"),
        );
    }
}
