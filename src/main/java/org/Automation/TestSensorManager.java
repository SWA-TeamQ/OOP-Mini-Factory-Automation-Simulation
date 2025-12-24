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
        System.out.println("Initializing Sensor Management System...");
        SensorManager manager = new SensorManager();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Select option: ");
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
                case 11 -> manager.startAll();
                case 12 -> manager.stopAll();
                case 0 -> { manager.shutdown(5); running = false; }
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n1-List 2-Add Temp 3-Add Weight 4-Start 5-Stop 6-Read 7-Auto 8-Control 9-Remove 10-Find 11-StartAll 12-StopAll 0-Exit");
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(scanner.nextLine().trim()); } catch (Exception e) { return -1; }
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        try { return Double.parseDouble(scanner.nextLine().trim()); } catch (Exception e) { return Double.NaN; }
    }

    private static String prompt(String msg) { System.out.print(msg); return scanner.nextLine().trim(); }

    private static void listSensors(SensorManager manager) {
        List<String> infos = manager.listSensorInfo();
        infos.forEach(System.out::println);
    }

    private static void addTemperatureSensor(SensorManager manager) {
        TemperatureSensor t = new TemperatureSensor("Temperature", prompt("Location: "), "Init", readDouble("Start: "), readDouble("Tol: "), readDouble("Target: "), "°C");
        manager.addSensor(t);
    }

    private static void addWeightSensor(SensorManager manager) {
        WeightSensor w = new WeightSensor("Weight", prompt("Location: "), "Init", readDouble("Initial: "), readDouble("Capacity: "), "kg");
        manager.addSensor(w);
    }

    private static void startSensor(SensorManager manager) { System.out.println(manager.startSensor(readInt("ID: ")) ? "Started" : "Failed"); }
    private static void stopSensor(SensorManager manager) { System.out.println(manager.stopSensor(readInt("ID: ")) ? "Stopped" : "Failed"); }
    private static void readSensorValue(SensorManager manager) {
        Sensor s = manager.findSensorById(readInt("ID: "));
        if (s != null) s.readValue();
    }
    private static void toggleAutomatic(SensorManager manager) { manager.setSensorAutomaticMode(readInt("ID: "), readInt("0/1: ") == 1); }
    private static void setControl(SensorManager manager) { manager.setSensorControl(readInt("ID: "), readInt("0/1: ") == 1, readDouble("Target: "), readDouble("Tol: ")); }
    private static void removeSensor(SensorManager manager) { manager.removeSensor(readInt("ID: ")); }
    private static void findById(SensorManager manager) { System.out.println(manager.findSensorById(readInt("ID: ")).getSensorInfo()); }
}
