package org.automation;

import org.automation.controllers.SensorManager;
import org.automation.entities.Sensor;
import org.automation.entities.TemperatureSensor;
import org.automation.entities.WeightSensor;

import java.util.List;
import java.util.Scanner;

public class TestSensorManager {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Interactive SensorManager Test ===\n");
        SensorManager manager = new SensorManager();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Select option: ");
            try {
                switch (choice) {
                    case 1 -> listSensors(manager);
                    case 2 -> addTemperatureSensor(manager);
                    case 3 -> addWeightSensor(manager);
                    case 4 -> startSensor(manager);
                    case 5 -> stopSensor(manager);
                    case 6 -> readSensorValue(manager);
                    case 7 -> toggleAutomatic(manager);
                    case 8 -> setControl(manager);
                    case 9 -> removeSensor(manager);
                    case 10 -> findById(manager);
                    case 0 -> {
                        System.out.println("Shutting down...");
                        manager.stopAll();
                        manager.shutdown(5);
                        running = false;
                    }
                    default -> System.out.println("Unknown option");
                }
            } catch (Exception e) {
                System.err.println("Operation failed: " + e.getMessage());
            }
            System.out.println();
        }

        System.out.println("Goodbye.");
    }

    private static void printMenu() {
        System.out.println("Menu:");
        System.out.println(" 1) List sensors");
        System.out.println(" 2) Add TemperatureSensor");
        System.out.println(" 3) Add WeightSensor");
        System.out.println(" 4) Start sensor");
        System.out.println(" 5) Stop sensor");
        System.out.println(" 6) Read sensor value");
        System.out.println(" 7) Toggle automatic mode");
        System.out.println(" 8) Set control (enable/disable + params)");
        System.out.println(" 9) Remove sensor");
        System.out.println("10) Find sensor by ID");
        System.out.println(" 0) Exit (stop & shutdown)");
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private static void listSensors(SensorManager manager) {
        List<String> infos = manager.listSensorInfo();
        if (infos.isEmpty()) {
            System.out.println("No sensors registered.");
            return;
        }
        for (String info : infos) System.out.println(info);
    }

    private static void addTemperatureSensor(SensorManager manager) {
        System.out.println("Add TemperatureSensor:");
        String location = prompt("Location: ");
        double start = readDouble("Start threshold: ");
        double tol = readDouble("Tolerance: ");
        double target = readDouble("Target temperature: ");
        TemperatureSensor t = new TemperatureSensor("Temperature", location, "Init", start, tol, target, "°C");
        manager.addSensor(t);
    }

    private static void addWeightSensor(SensorManager manager) {
        System.out.println("Add WeightSensor:");
        String location = prompt("Location: ");
        double initial = readDouble("Initial weight: ");
        double capacity = readDouble("Capacity: ");
        WeightSensor w = new WeightSensor("Weight", location, "Init", initial, capacity, "kg");
        manager.addSensor(w);
    }

    private static void startSensor(SensorManager manager) {
        int id = readInt("Sensor ID to start: ");
        boolean ok = manager.startSensor(id);
        System.out.println(ok ? "Started" : "Start failed");
    }

    private static void stopSensor(SensorManager manager) {
        int id = readInt("Sensor ID to stop: ");
        boolean ok = manager.stopSensor(id);
        System.out.println(ok ? "Stopped" : "Stop failed");
    }

    private static void readSensorValue(SensorManager manager) {
        int id = readInt("Sensor ID to read: ");
        Sensor s = manager.findSensorById(id);
        if (s == null) { System.out.println("Not found"); return; }
        s.readValue();
    }

    private static void toggleAutomatic(SensorManager manager) {
        int id = readInt("Sensor ID: ");
        int v = readInt("0 = disable, 1 = enable automatic: ");
        boolean enabled = v == 1;
        boolean ok = manager.setSensorAutomaticMode(id, enabled);
        System.out.println(ok ? "Updated" : "Failed");
    }

    private static void setControl(SensorManager manager) {
        int id = readInt("Sensor ID: ");
        int v = readInt("0 = disable control, 1 = enable: ");
        if (v == 0) {
            boolean ok = manager.setSensorControl(id, false, 0, 0);
            System.out.println(ok ? "Control disabled" : "Failed");
            return;
        }
        double target = readDouble("Target value: ");
        double tol = readDouble("Tolerance: ");
        boolean ok = manager.setSensorControl(id, true, target, tol);
        System.out.println(ok ? "Control set" : "Failed");
    }

    private static void removeSensor(SensorManager manager) {
        int id = readInt("Sensor ID to remove: ");
        boolean ok = manager.removeSensor(id);
        System.out.println(ok ? "Removed" : "Remove failed");
    }

    private static void findById(SensorManager manager) {
        int id = readInt("Sensor ID: ");
        Sensor s = manager.findSensorById(id);
        if (s == null) System.out.println("Not found"); else System.out.println(s.getSensorInfo());
    }

    private static String prompt(String p) {
        System.out.print(p);
        return scanner.nextLine().trim();
    }
}
