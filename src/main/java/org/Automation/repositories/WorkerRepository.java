package org.automation.repositories;
import org.automation.core.DatabaseManager;
import org.automation.entities.Worker;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkerRepository extends Repository<Worker> {
    public WorkerRepository(DatabaseManager db) {
        super("Worker", db);
    }

    @Override
    public Worker mapRow(ResultSet rs) throws SQLException {
        return new Worker(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("status"),
                rs.getString("role"),
                rs.getInt("assignedMachineId"),
                rs.getString("shiftStatus"));
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS Worker (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    role TEXT,
                    assignedMachineId INTEGER,
                    shiftStatus TEXT DEFAULT 'inactive',
                    FOREIGN KEY(assignedMachineId) REFERENCES Machine(id)
                );
            """;
    }
}
