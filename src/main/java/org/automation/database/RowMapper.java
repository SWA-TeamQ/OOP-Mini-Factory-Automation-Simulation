package org.automation.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Functional interface for mapping a ResultSet row to a domain object.
 */
@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
