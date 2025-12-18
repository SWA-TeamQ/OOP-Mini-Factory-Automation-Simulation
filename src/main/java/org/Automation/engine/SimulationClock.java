package org.automation.engine;
import java.time.LocalDateTime;
//import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.temporal.ChronoUnit;

public class SimulationClock {
	//Instance Variables
	private static SimulationClock instance;
	
	private LocalDateTime simTime;
	private LocalDateTime lastNotificationDate;
	private int speedFactor=1;
	private boolean ispaused=false;
	
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();//for the entire system to work on the same time
	private final int Tick_Per_MS=50;// well we set it into a task per 50 milliseconds, meaning 20 times in a second
	
private ArrayList<ClockObserver> observers = new ArrayList<ClockObserver>();
	
	private SimulationClock(){
		this.simTime= LocalDateTime.now();
		this.lastNotificationDate=this.simTime;
		
		startIntegralTimer();
	}
	
	//Instance Methods
public static synchronized SimulationClock getInstance() { // used to create a single instance for all tasks
if (instance == null) {
    instance = new SimulationClock();
}
return instance;
}
	
	private void startIntegralTimer() {
		scheduler.scheduleAtFixedRate(()->{
		if(!ispaused) {
			tick();
		}} , 0, Tick_Per_MS, TimeUnit.MILLISECONDS);
	}
	
private void tick() {

    long millsToAdd = Tick_Per_MS * (long) speedFactor;

    simTime = simTime.plusNanos(millsToAdd * 1_000_000L);

    long secondsAfterPreviousNotification = ChronoUnit.SECONDS.between(lastNotificationDate, simTime);

    if (secondsAfterPreviousNotification >= 1) {
        notifyOthers();
        lastNotificationDate = lastNotificationDate.plusSeconds(secondsAfterPreviousNotification);
    }
}
	
public synchronized void register(ClockObserver observer) {
    observers.add(observer);
}
	
private synchronized void notifyOthers() {
    for (ClockObserver observer : observers) {
        observer.onTick(simTime);
    }
}

	public void start() {
		ispaused=false;
		System.out.println("The Simulation has been started at time: \n"+ simTime);
	}
	
	public void stop() {
		ispaused=true;
		System.out.println("The Simulation has been stopped at time: \n"+ simTime);
	}
	
	public void setSpeedFactor(int speed) {
		speedFactor=speed;
		System.out.println("The Simulation speed has been intiated into: \n"+ speedFactor);
	}

	public LocalDateTime getCurrentTime() {
		return simTime;
	}
}
