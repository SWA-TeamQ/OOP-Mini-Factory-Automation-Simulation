package org.automation.database;

import java.lang.ref.Cleaner;
import java.sql.*;

public final class DatabaseManager {

  private Connection connection;
  private final String url = "jdbc:sqlite:automation.sqlite";

  public boolean connect() {
    if (connection != null) return true;

    try {
      connection = DriverManager.getConnection(url);
      System.out.println("Connection to SQLite established.");
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to connect", e);
    }
  }

  public Connection getConnection() {
    return this.connection;
  }

  public boolean disconnect() {
    try {
      if (connection != null) {
        connection.close();
        System.out.println("Connection closed.");
      }
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to disconnect", e);
    }
  }

  // ------------ Helper method for parameter binding -------------
  private void bindParams(PreparedStatement pstmt, Object[] params) throws SQLException {
    if (params == null)
      return;

    for (int i = 0; i < params.length; i++) {
      pstmt.setObject(i + 1, params[i]);
    }
  }

  // ---------------- Helper method for SELECT queries ----------------
  private ResultSet executeQuery(String sql, Object[] params) {
    try {
      PreparedStatement pstmt = connection.prepareStatement(sql);
      bindParams(pstmt, params);
      return pstmt.executeQuery();
    } catch (SQLException e) {
      throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
    }
  }

  /**
   * Safe query helper that maps rows using the provided RowMapper and
   * ensures PreparedStatement and ResultSet are closed properly.
   */
  public <T> java.util.List<T> query(String tableName, String where, Object[] params, org.automation.database.RowMapper<T> mapper) {
    String sql = "SELECT * FROM " + tableName + (where != null && !where.trim().isEmpty() ? " WHERE " + where : "");
    java.util.List<T> result = new java.util.ArrayList<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      bindParams(pstmt, params);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          result.add(mapper.mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
    }
    return result;
  }

  public <T> T queryOne(String tableName, String where, Object[] params, org.automation.database.RowMapper<T> mapper) {
    java.util.List<T> list = query(tableName, where, params, mapper);
    return list.isEmpty() ? null : list.get(0);
  }

  /**
   * Execute DDL statements such as CREATE TABLE.
   */
  public boolean executeDDL(String sql) {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute(sql);
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("DDL execution failed: " + e.getMessage(), e);
    }
  }

  // ---------------- Helper method for INSERT, UPDATE, DELETE ----------------
  private boolean executeMutator(String sql, Object[] params) {
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      bindParams(pstmt, params);
      return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("Mutator execution failed: " + e.getMessage(), e);
    }
  }

  // ------------ Find (SELECT) -------------
  public ResultSet find(String tableName, String where, Object[] params) {
    String sql = "SELECT * FROM " + tableName;

    if (where != null && !where.trim().isEmpty()) {
      sql += " WHERE " + where;
    }

    return executeQuery(sql, params);
  }

  // ------------ INSERT -------------
  public boolean insert(String tableName, String[] columns, Object[] values) {

    if (columns.length != values.length)
      throw new IllegalArgumentException("Columns and values length mismatch");

    String cols = String.join(", ", columns);

    // creating placeholders (?) for the statement
    String placeholders = "";
    for (int i = 0; i < values.length; i++) {
      if (i < values.length - 1) {
        placeholders += "?, ";
      } else {
        placeholders += "?";
      }
    }

    String sql = "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + placeholders + ")";

    return executeMutator(sql, values);
  }

  // ------------ DELETE -------------
  public boolean delete(String tableName, String where, Object[] params) {
    String sql = "DELETE FROM " + tableName +
        (where != null ? " WHERE " + where : "");

    return executeMutator(sql, params);
  }

  // ------------ UPDATE -------------
  public boolean update(String tableName, String setClause, String where, Object[] params) {

    String sql = "UPDATE " + tableName + " SET " + setClause +
        (where != null ? " WHERE " + where : "");

    return executeMutator(sql, params);
  }

  void Cleaner(){
    disconnect();
  }
}
