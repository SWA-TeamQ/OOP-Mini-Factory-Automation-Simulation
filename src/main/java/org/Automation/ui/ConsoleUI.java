package org.Automation.ui;

import org.Automation.Controllers.WorkFlowController;
import org.Automation.ui.helpers.EventLog;
import org.Automation.ui.helpers.FactorySnapshotView;
import org.Automation.ui.helpers.StatusView;

import java.util.Scanner;

public class ConsoleUI {
    private final WorkFlowController controller;
    private final Scanner scanner;
    private final EventLog eventLog;
    private final FactorySnapshotView snapshotView;
    private final StatusView statusView;

    public ConsoleUI(WorkFlowController controller,
                     EventLog eventLog,
                     FactorySnapshotView snapshotView,
                     StatusView statusView) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
        this.eventLog = eventLog;
        this.snapshotView = snapshotView;
        this.statusView = statusView;
    }

    public void start() {
        while (true) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> controller.startProduction();
                case 2 -> controller.stopProduction();
                case 3 -> snapshotView.displaySnapshot();
                case 4 -> statusView.displayMachineStatus();
                case 5 -> displayEventLog();
                case 6 -> {
                    System.out.print("Are you sure you want to clear all data? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        controller.resetFactory();
                    }
                }
                case 0 -> {
                    controller.stopProduction();
                    System.out.println("Exiting simulation...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showMenu() {
        System.out.println("\n=== FACTORY SIMULATION MENU ===");
        System.out.println("1. Start Production");
        System.out.println("2. Stop Production");
        System.out.println("3. View Factory Snapshot");
        System.out.println("4. View Machine Status");
        System.out.println("5. View Event Log");
        System.out.println("6. Fresh Start (Clear Database)");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    private void displayEventLog() {
        System.out.println("=== EVENT LOG ===");
        eventLog.getLogs().forEach(System.out::println);
        System.out.println("=================\n");
    }
}
