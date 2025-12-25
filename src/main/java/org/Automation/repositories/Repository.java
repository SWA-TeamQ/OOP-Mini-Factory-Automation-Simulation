package org.Automation.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.Automation.core.DatabaseManager;

public abstract class Repository<T> {

  protected final String tableName;
  protected final DatabaseManager db;

  public Repository(String tableName, DatabaseManager db) {
    this.tableName = tableName;
    this.db = db;
  }

  // ---------- CRUD Operations ----------
  public boolean insert(String[] columns, Object[] values) {
    return db.insert(tableName, columns, values);
  }

  public boolean update(String setClause, String where, Object[] params) {
    return db.update(tableName, setClause, where, params);
  }

  public boolean delete(String where, Object[] params) {
    return db.delete(tableName, where, params);
  }

  public T findOne(String where, Object[] params) {
    try (ResultSet rs = db.find(tableName, where, params)) {
      if (rs.next()) {
        return mapRow(rs);
      }
    } catch (SQLException e) {
      throw new RuntimeException("findOne failed: " + e.getMessage(), e);
    }
    return null;
  }

  public List<T> findAllWhere(String where, Object[] params) {
    List<T> result = new ArrayList<>();
    try (ResultSet rs = db.find(tableName, where, params)) {
      while (rs.next()) {
        result.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("findAllWhere failed: " + e.getMessage(), e);
    }
    return result;
  }

  public List<T> findAll() {
    List<T> result = new ArrayList<>();
    try (ResultSet rs = db.find(tableName, null, null)) {
      while (rs.next()) {
        result.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("findAll failed: " + e.getMessage(), e);
    }
    return result;
  }

  // ---------- Maps a row from ResultSet into an entity ----------
  protected abstract T mapRow(ResultSet rs) throws SQLException;

  public abstract String createTableQuery();
}
