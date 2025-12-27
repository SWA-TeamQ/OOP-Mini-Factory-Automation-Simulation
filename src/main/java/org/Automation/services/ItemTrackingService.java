package org.Automation.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.Automation.core.DatabaseManager;
import org.Automation.entities.ProductItem;
import org.Automation.repositories.ProductItemRepository;

public class ItemTrackingService implements IItemTrackingService {
  private final DatabaseManager db;
  private final ProductItemRepository itemRepo;

  private volatile boolean running = false;
  private String status = "stopped";

  // In-memory “last known location” so we can detect movement between ticks.
  private final Map<Integer, Integer> lastStationByItemId = new HashMap<>();

  // Avoid spamming the console if DB schema isn't ready.
  private boolean warnedAboutMissingProductItemTable = false;

  public ItemTrackingService(DatabaseManager db, ProductItemRepository itemRepo) {
    this.db = db;
    this.itemRepo = itemRepo;
  }

  private void ensureTrackingTableExists() {
    try {
      Connection conn = db.getConnection();
      if (conn == null) {
        throw new IllegalStateException("Database connection is null. Did you call db.connect()?");
      }

      try (Statement stmt = conn.createStatement()) {
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS ItemTrackingEvent (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              timestamp TEXT NOT NULL,
              itemId INTEGER NOT NULL,
              fromStationId INTEGER,
              toStationId INTEGER,
              itemStatus TEXT
            );
            """);
      }
    } catch (Exception e) {
      status = "error";
      System.err.println("[ItemTrackingService] Failed to ensure tracking table: " + e.getMessage());
    }
  }

  private void persistMove(LocalDateTime time, ProductItem item, Integer fromStationId, Integer toStationId) {
    try {
      Connection conn = db.getConnection();
      if (conn == null)
        return;

      try (PreparedStatement ps = conn.prepareStatement("""
          INSERT INTO ItemTrackingEvent (timestamp, itemId, fromStationId, toStationId, itemStatus)
          VALUES (?, ?, ?, ?, ?);
          """)) {
        ps.setString(1, String.valueOf(time));
        ps.setInt(2, item.getId());
        if (fromStationId == null) {
          ps.setObject(3, null);
        } else {
          ps.setInt(3, fromStationId);
        }
        if (toStationId == null) {
          ps.setObject(4, null);
        } else {
          ps.setInt(4, toStationId);
        }
        ps.setString(5, item.getStatus());
        ps.executeUpdate();
      }
    } catch (Exception e) {
      // Tracking should never crash the simulation loop.
      System.err.println("[ItemTrackingService] Failed to persist tracking event: " + e.getMessage());
    }
  }

  @Override
  public void start() {
    running = true;
    status = "running";
    ensureTrackingTableExists();
  }

  @Override
  public void stop() {
    running = false;
    status = "stopped";
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public void onTick(LocalDateTime time) {
    if (!running)
      return;

    final List<ProductItem> items;
    try {
      items = itemRepo.findAll();
    } catch (RuntimeException e) {
      // Most common cause: "no such table: ProductItem"
      if (!warnedAboutMissingProductItemTable) {
        warnedAboutMissingProductItemTable = true;
        System.err.println(
            "[ItemTrackingService] Cannot read ProductItem table yet (DB schema/data not initialized). Tracking is running but has nothing to read.");
        System.err.println("[ItemTrackingService] Details: " + e.getMessage());
      }
      return;
    }

    for (ProductItem item : items) {
      int itemId = item.getId();
      int currentStationId = item.getCurrentStationId();

      if (!lastStationByItemId.containsKey(itemId)) {
        // First time seeing this item: establish baseline location.
        lastStationByItemId.put(itemId, currentStationId);
        continue;
      }

      int previousStationId = lastStationByItemId.get(itemId);
      if (previousStationId != currentStationId) {
        System.out.println("[ItemTracking] " + time + " item#" + itemId
            + " moved " + previousStationId + " -> " + currentStationId);
        persistMove(time, item, previousStationId, currentStationId);
        lastStationByItemId.put(itemId, currentStationId);
      }
    }
  }

}
