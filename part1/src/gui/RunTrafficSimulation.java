package gui;

import simtraffic_conc_examples.TrafficSimulationSingleRoadMassiveNumberOfCars;
import simtraffic_conc_examples.TrafficSimulationSingleRoadSeveralCars;
import simtraffic_conc_examples.TrafficSimulationSingleRoadTwoCars;
import simtraffic_conc_examples.TrafficSimulationWithCrossRoads;

/**
 * 
 * Main class to create and run a simulation - with GUI
 * 
 */
public class RunTrafficSimulation {

	private static final int DEFAULT_STEPS = 10000;
	
	public static void main(String[] args) {		

		int nWorkers = Runtime.getRuntime().availableProcessors() + 1;

		//var simulation = new TrafficSimulationSingleRoadTwoCars();
		var simulation = new TrafficSimulationSingleRoadSeveralCars();
		// var simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars();

		
		simulation.configureNumWorkers(nWorkers);
        SimulationGUI gui = new SimulationGUI(DEFAULT_STEPS);

		SimulationController controller = new SimulationController(simulation);
		controller.attach(gui);
        gui.display();
				
	}
}
