package org.automation.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple console and file logger.
 */
public class Logger {

    private static final String LOG_FILE = "simulation.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void info(String message) {
        log("INFO", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void debug(String message) {
        log("DEBUG", message);
    }

    public static void clearLog() {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, false))) {
            out.print("");
        } catch (IOException e) {
            System.err.println("Could not clear log file: " + e.getMessage());
        }
    }

    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        // Print to console
        if (level.equals("ERROR")) {
            System.err.println(formattedMessage);
        } else {
            System.out.println(formattedMessage);
        }

        // Write to file
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            out.println(formattedMessage);
        } catch (IOException e) {
            System.err.println("Could not write to log file: " + e.getMessage());
        }
    }
}
