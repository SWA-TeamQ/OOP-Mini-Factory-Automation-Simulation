package org.Automation;

import java.sql.*;

interface DatabaseManagerInterface {

  boolean connect();

  Connection getConnection();

  boolean disconnect();

  ResultSet find(String tableName, String where, Object[] params);

  boolean insert(String tableName, String[] columns, Object[] values);

  boolean delete(String tableName, String where, Object[] params);

  boolean update(String tableName, String setClause, String where, Object[] params);
}

public final class DatabaseManager implements DatabaseManagerInterface {

  private Connection connection;
  private final String url = "jdbc:sqlite:automation.sqlite";

  public DatabaseManager() {
  }

  @Override
  public boolean connect() {
    if (connection != null)
      return true;

    try {
      connection = DriverManager.getConnection(url);
      System.out.println("Connection to SQLite established.");
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to connect", e);
    }
  }

  @Override
  public Connection getConnection() {
    return this.connection;
  }

  @Override
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
  @Override
  public ResultSet find(String tableName, String where, Object[] params) {
    String sql = "SELECT * FROM " + tableName;

    if (where != null && !where.trim().isEmpty()) {
      sql += " WHERE " + where;
    }

    return executeQuery(sql, params);
  }

  // ------------ INSERT -------------
  @Override
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
  @Override
  public boolean delete(String tableName, String where, Object[] params) {
    String sql = "DELETE FROM " + tableName +
        (where != null ? " WHERE " + where : "");

    return executeMutator(sql, params);
  }

  // ------------ UPDATE -------------
  @Override
  public boolean update(String tableName, String setClause, String where, Object[] params) {

    String sql = "UPDATE " + tableName + " SET " + setClause +
        (where != null ? " WHERE " + where : "");

    return executeMutator(sql, params);
  }
}
