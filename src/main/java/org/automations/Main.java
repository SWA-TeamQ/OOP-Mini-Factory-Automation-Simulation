package org.automations;

import org.automations.core.DatabaseManager;
import org.automations.ui.ConsoleApp;

public class Main {
  public static void main(String[] args) {
    DatabaseManager db = new DatabaseManager();
    try {
      // Attempt to connect to the SQLite database
      db.connect();
      System.out.println("[BOOT]: Database connection established.");

      // 2. Launch the Console Application
      // The ConsoleApp constructor will build the repositories, services, and engine.
      // We pass the active 'db' connection into the app for Dependency Injection.
      ConsoleApp app = new ConsoleApp(db);

      // This transfers control to the UI loop (the menus you created)
      app.start();

    } catch (Exception e) {
      // High-level error handling: If anything fails during startup, we catch it
      // here.
      System.err.println("[CRITICAL ERROR]: Application failed to start!");
      e.printStackTrace();
    } finally {
      // 3. Cleanup Logic
      // This block runs no matter what (even if the app crashes).
      // It ensures we don't leave the database file locked.
      System.out.println("[SHUTDOWN]: Disconnecting Database...");
      db.closeConnection();
      System.out.println("[SHUTDOWN]: System offline.");
    }
  }
}
