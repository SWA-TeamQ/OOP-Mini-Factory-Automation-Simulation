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

    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + "/simulation.log";
    private static final String ERROR_LOG = LOG_DIR + "/error.log";
    private static final String SYSTEM_LOG = LOG_DIR + "/system.log";

    private static final String ERROR = "ERROR";
    private static final String INFO = "INFO";
    private static final String WARN = "WARN";
    private static final String DEBUG = "DEBUG";
    private static final String SYSTEM = "SYSTEM";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void info(String message) {
        log(INFO, message);
    }

    public static void warn(String message) {
        log(WARN, message);
    }

    public static void error(String message) {
        log(ERROR, message);
    }

    public static void debug(String message) {
        log(DEBUG, message);
    }

    public static void system(String message) {
        log(SYSTEM, message);
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
        String logMessage = String.format("[%s] %s", level, message);
        String timeLoggedMessage = String.format("[%s] %s", timestamp, logMessage);

        // Print to console
        if (level.equals("ERROR")) {
            System.err.println(logMessage);
        } else {
            System.out.println(logMessage);
        }

        // Write the time logged message to file
        String targetFile = switch (level) {
            case INFO, DEBUG -> LOG_FILE;
            case ERROR, WARN -> ERROR_LOG;
            case SYSTEM -> SYSTEM_LOG;
            default -> null;
        };

        if (targetFile != null) {
            writeToFile(targetFile, timeLoggedMessage);
        }
    }

    private static void writeToFile(final String filename, final String timeLoggedMessage) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
            out.println(timeLoggedMessage);
        } catch (IOException e) {
            System.err.println("Could not write to log file to " + filename + ": " + e.getMessage());
        }
    }
}
