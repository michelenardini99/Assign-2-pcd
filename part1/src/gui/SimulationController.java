package gui;

import logics.*;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SimulationController {

	private Flag stopFlag;
	private AbstractSimulation simulation;
	private SimulationGUI gui;
	private RoadSimView view;
	private RoadSimStatistics stat;
	Executor executor;
	 
	public SimulationController(AbstractSimulation simulation) {
		this.simulation = simulation;
		this.stopFlag = new Flag();
		this.executor = Executors.newCachedThreadPool();
	}
	
	public void attach(SimulationGUI gui) {
		this.gui = gui;		
		view = new RoadSimView();
		stat = new RoadSimStatistics();
		simulation.addSimulationListener(stat);
		simulation.addSimulationListener(view);
		gui.setController(this);
	}

	public void notifyStarted(int nSteps) {
		executor.execute(() -> {
			simulation.setup();
			view.display();

			stopFlag.reset();
			simulation.run(nSteps, stopFlag, true);
			long d = simulation.getSimulationDuration();
			System.out.println("Time per steps " + simulation.getAverageTimePerStep() + " ms");
			System.out.println("Completed in " + d + " ms");
			gui.reset();
		});
	}
	
	public void notifyStopped() {
		stopFlag.set();
	}

}
