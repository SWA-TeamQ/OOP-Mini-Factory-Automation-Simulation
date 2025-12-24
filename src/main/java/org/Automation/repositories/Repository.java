package org.automation.repositories;
import org.automation.database.DatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        try {
            return db.queryOne(tableName, where, params, rs -> mapRow(rs));
        } catch (RuntimeException e) {
            throw new RuntimeException("findOne failed: " + e.getMessage(), e);
        }
    }

    public List<T> findAllWhere(String where, Object[] params) {
        try {
            return db.query(tableName, where, params, rs -> mapRow(rs));
        } catch (RuntimeException e) {
            throw new RuntimeException("findAllWhere failed: " + e.getMessage(), e);
        }
    }

    public List<T> findAll() {
        try {
            return db.query(tableName, null, null, rs -> mapRow(rs));
        } catch (RuntimeException e) {
            throw new RuntimeException("findAll failed: " + e.getMessage(), e);
        }
    }

    // ---------- Maps a row from ResultSet into an entity ----------
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    public abstract String createTableQuery();
}
