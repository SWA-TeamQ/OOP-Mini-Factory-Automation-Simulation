package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.entities.ConveyorBelt;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConveyorRepository extends Repository<ConveyorBelt> {

    public ConveyorRepository(DatabaseManager db) {
        super("ConveyorBelt", db);
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS ConveyorBelt (
                    id TEXT PRIMARY KEY,
                    speed REAL
                );
                """;
    }

    @Override
    protected ConveyorBelt mapRow(ResultSet rs) throws SQLException {
        return new ConveyorBelt(rs.getString("id"), rs.getDouble("speed"));
    }

    @Override
    public void save(ConveyorBelt conveyor) {
        String[] columns = {"id", "speed"};
        Object[] values = {conveyor.getId(), conveyor.getSpeed()};
        db.insert(tableName, columns, values);
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[]{id});
    }
}
