package org.automation.engine;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationClock {
	// Singleton instance
	private static SimulationClock instance;

	// Instance Variables
	private double currentSecond = 0;
	private boolean paused = true;
	private int speedFactor = 1;

	// well we set it into a task per 50 milliseconds, meaning 20 times in a second
	private final int Tick_Per_MS = 50;

	private ArrayList<ClockObserver> observers = new ArrayList<ClockObserver>();
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	// for the entire system to work on the same time 
	private SimulationClock() {
		scheduler.scheduleAtFixedRate(() -> tick(), 1000, Tick_Per_MS, TimeUnit.MILLISECONDS);
	}

	public static synchronized SimulationClock getInstance() {
		// used to create a single instance for all tasks
		if (instance == null) {
			instance = new SimulationClock();
		}
		return instance;
	}

	// Instance Methods
	private void tick() {
		if (paused)
			return;

		currentSecond += (Tick_Per_MS * speedFactor) / 1000;

		for (ClockObserver observer : observers)
			observer.onTick(currentSecond);
	}

	public synchronized void register(ClockObserver observer) {
		observers.add(observer);
	}

	public void start() {
		paused = false;
		System.out.println("The Simulation has been started at time: \n" + currentSecond);
	}

	public void stop() {
		paused = true;
		System.out.println("The Simulation has been stopped at time: \n" + currentSecond);
	}

	public void setSpeedFactor(int speed) {
		speedFactor = speed;
		System.out.println("The Simulation speed has been updated into: \n" + speedFactor);
	}

	public double getCurrentSecond() {
		return currentSecond;
	}
}
