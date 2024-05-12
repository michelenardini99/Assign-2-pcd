package gui;

import logics.*;

public class SimulationController {

	private Flag stopFlag;
	private AbstractSimulation simulation;
	private SimulationGUI gui;
	private RoadSimView view;
	private RoadSimStatistics stat;
	 
	public SimulationController(AbstractSimulation simulation) {
		this.simulation = simulation;
		this.stopFlag = new Flag();
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
		new Thread(() -> {
			simulation.setup();			
			view.display();
		
			stopFlag.reset();
			simulation.run(nSteps, stopFlag, true);
			long d = simulation.getSimulationDuration();
			System.out.println("Completed in " + d + " ms");
			gui.reset();
			
		}).start();
	}
	
	public void notifyStopped() {
		stopFlag.set();
	}

}
