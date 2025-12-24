package org.automation.database;

import java.sql.*;

public final class DatabaseManager implements AutoCloseable {

    private Connection connection;
    private static final String URL = "jdbc:sqlite:automation.sqlite";

    /**
     * Connect to SQLite database
     */
    public boolean connect() {
        if (connection != null) return true;

        try {
            // 🔴 REQUIRED: force SQLite JDBC driver to load
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite established.");
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect", e);
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database not connected");
        }
        return connection;
    }

    /**
     * Close DB connection
     */
    public boolean disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
            connection = null;
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to disconnect", e);
        }
    }

    /* ---------------- PARAM BINDING ---------------- */

    private void bindParams(PreparedStatement pstmt, Object[] params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    /* ---------------- SELECT HELPERS ---------------- */

    public <T> java.util.List<T> query(
            String tableName,
            String where,
            Object[] params,
            RowMapper<T> mapper
    ) {
        String sql = "SELECT * FROM " + tableName +
                (where != null && !where.trim().isEmpty() ? " WHERE " + where : "");

        java.util.List<T> result = new java.util.ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            bindParams(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query execution failed", e);
        }

        return result;
    }

    public <T> T queryOne(
            String tableName,
            String where,
            Object[] params,
            RowMapper<T> mapper
    ) {
        java.util.List<T> list = query(tableName, where, params, mapper);
        return list.isEmpty() ? null : list.get(0);
    }

    /* ---------------- DDL ---------------- */

    public boolean executeDDL(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("DDL execution failed", e);
        }
    }

    /* ---------------- MUTATORS ---------------- */

    private boolean executeMutator(String sql, Object[] params) {
      if (connection == null) connect();
      try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
        bindParams(pstmt, params);
        return pstmt.executeUpdate() > 0;
      } catch (SQLException e) {
        throw new RuntimeException("Mutator execution failed", e);
      }
  }


    public boolean insert(String tableName, String[] columns, Object[] values) {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns and values length mismatch");
        }

        String cols = String.join(", ", columns);
        String placeholders = String.join(", ", java.util.Collections.nCopies(values.length, "?"));

        String sql = "INSERT INTO " + tableName +
                " (" + cols + ") VALUES (" + placeholders + ")";

        return executeMutator(sql, values);
    }

    public boolean update(String tableName, String setClause, String where, Object[] params) {
        String sql = "UPDATE " + tableName + " SET " + setClause +
                (where != null ? " WHERE " + where : "");
        return executeMutator(sql, params);
    }

    public boolean delete(String tableName, String where, Object[] params) {
        String sql = "DELETE FROM " + tableName +
                (where != null ? " WHERE " + where : "");
        return executeMutator(sql, params);
    }

    /* ---------------- CLEANUP ---------------- */

    @Override
    public void close() {
        disconnect();
    }
}
