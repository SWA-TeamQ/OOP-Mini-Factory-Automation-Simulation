package org.Automation.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.Automation.DatabaseManager;
import org.Automation.entities.EventLog;

public class EventLogRepository extends Repository<EventLog> {
    public EventLogRepository(DatabaseManager db) {
        super("EventLog", db);
    }
    

    @Override
    public EventLog mapRow(ResultSet rs) throws SQLException {
        return new EventLog(
                rs.getInt("id"),
                rs.getString("timestamp"),
                rs.getString("componentType"),
                rs.getInt("componentId"),
                rs.getString("eventType"),
                rs.getString("message"));
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS EventLog (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp TEXT NOT NULL,
                    componentType TEXT,
                    componentId INTEGER,
                    eventType TEXT,
                    message TEXT
                );
            """;
    }
}
