package org.automation.engine;
import java.time.LocalDateTime;
// import org.automation.controllers.WorkflowController;
// import org.automation.utils.Logger;

public class SimulationEngine {
	//Instance Variables
	
	// WorkflowController controller;
	// Logger logger;
	
	}
	public void startSimulation() {
		SimulationClock clock =  SimulationClock.getInstance();
		clock.setSpeedFactor(1);
		clock.start();
		System.out.println("The System has started its function at time: "+ clock.getCurrentTime());
	}
	
	public void stopSimulation() {
		SimulationClock clock =  SimulationClock.getInstance();
		clock.stop();
		System.out.println("The System has ended its function at time: "+ clock.getCurrentTime());
	}
	
	public void printSImulationStatus() {
		
	}
}
