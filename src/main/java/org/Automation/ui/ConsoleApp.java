package org.automation.ui;
import org.automation.engine.SimulationEngine;
import org.automation.controllers.WorkflowController;
import org.automation.core.Logger;

public class ConsoleApp extends ConsoleUI {
	//Instance Variables
	private SimulationEngine simulationEngine;
	private WorkflowController controller;
	
	ConsoleApp(){
		simulationEngine= new SimulationEngine();
	}
	
	//Instance Methods
	public void start() {
		simulationEngine.startSimulation();
		
	}
	
	public void runMainMenu() {
		
	}
	
	public void handleUserInput(String input) {
		
	}
	
	public void printWelcomeMessage() {
		
	}
	
	public void exit() {
		
	}
}
