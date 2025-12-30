package org.automation.core;

import java.sql.*;

public final class DatabaseManager {

  private Connection connection;
  private final String url = "jdbc:sqlite:data/automation.sqlite";

  public boolean connect() {
    if (connection != null)
      return true;

    try {
      connection = DriverManager.getConnection(url);
      Logger.system("Connection to SQLite established.");
      return true;
    } catch (SQLException e) {
      Logger.error("Failed to connect to the database: " + e.getMessage());
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
        Logger.system("Database connection closed");
      }
      return true;
    } catch (SQLException e) {
      Logger.error("Failed to disconnect the database: " + e.getMessage());
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
      Logger.error("Query execution failed: " + e.getMessage());
      throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
    }
  }

  // ---------------- Helper method for INSERT, UPDATE, DELETE ----------------
  
  private boolean executeMutator(String sql, Object[] params) {
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      bindParams(pstmt, params);
      return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
      Logger.error("Mutator execution failed: " + e.getMessage());
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

    if (columns.length != values.length){
      Logger.error("Columns and values length mismatch");
      throw new IllegalArgumentException("Columns and values length mismatch");
    }

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

    String sql = "INSERT OR REPLACE INTO " + tableName + " (" + cols + ") VALUES (" + placeholders + ")";

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


  // ------------ Clear Database -------------

  public void clearDatabase() {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DROP TABLE IF EXISTS Station");
      stmt.execute("DROP TABLE IF EXISTS Machine");
      stmt.execute("DROP TABLE IF EXISTS ProductItem");
      stmt.execute("DROP TABLE IF EXISTS Sensor");
      stmt.execute("DROP TABLE IF EXISTS ConveyorBelt");
      Logger.system("Database cleared successfully.");
    } catch (SQLException e) {
      Logger.error("Failed to clear database: " + e.getMessage());
      throw new RuntimeException("Failed to clear database", e);
    }
  }

}
