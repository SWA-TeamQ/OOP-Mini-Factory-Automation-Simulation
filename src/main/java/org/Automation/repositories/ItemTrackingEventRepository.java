package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.entities.ItemTrackingEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemTrackingEventRepository extends Repository<ItemTrackingEvent> {

    public ItemTrackingEventRepository(DatabaseManager db) {
        super("ItemTrackingEvent", db);
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS ItemTrackingEvent (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp TEXT NOT NULL,
                    itemId TEXT NOT NULL,
                    eventType TEXT NOT NULL,
                    stationId TEXT,
                    machineId TEXT,
                    details TEXT
                );
                """;
    }

    @Override
    protected ItemTrackingEvent mapRow(ResultSet rs) throws SQLException {
        return new ItemTrackingEvent(
                rs.getLong("id"),
                rs.getString("timestamp"),
                rs.getString("itemId"),
                rs.getString("eventType"),
                rs.getString("stationId"),
                rs.getString("machineId"),
                rs.getString("details")
        );
    }

    @Override
    public void save(ItemTrackingEvent event) {
        String[] columns = {"timestamp", "itemId", "eventType", "stationId", "machineId", "details"};
        Object[] values = {
                event.getTimestamp(),
                event.getItemId(),
                event.getEventType(),
                event.getStationId(),
                event.getMachineId(),
                event.getDetails()
        };
        db.insert(tableName, columns, values);
    }
}


