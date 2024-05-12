package logics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MasterAgent extends Thread {
	
	private boolean toBeInSyncWithWallTime;
	private int nStepsPerSec;
	private int numSteps;

	private long currentWallTime;
	
	private AbstractSimulation sim;
	private Flag stopFlag;
	private Semaphore done;
	private int nWorkers;
	
	public MasterAgent(AbstractSimulation sim, int nWorkers, int numSteps, Flag stopFlag, Semaphore done, boolean syncWithTime) {
		toBeInSyncWithWallTime = false;
		this.sim = sim;
		this.stopFlag = stopFlag;
		this.numSteps = numSteps;
		this.done = done;
		this.nWorkers = nWorkers;
		
		if (syncWithTime) {
			this.syncWithTime(25);
		}
	}

	public void run() {
		
		log("booted");
		Executor executor = Executors.newCachedThreadPool();
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		var simEnv = sim.getEnvironment();
		var simAgents = sim.getAgents();

		simEnv.init();
		for (var a: simAgents) {
			a.init(simEnv);
		}

		int t = sim.getInitialTime();
		int dt = sim.getTimeStep();
		
		sim.notifyReset(t, simAgents, simEnv);
		
		log("creating workers...");
		
		int nAssignedAgentsPerWorker = simAgents.size()/nWorkers;

		int index = 0;
		List<WorkerAgent> workers = new ArrayList<>();
		for (int i = 0; i < nWorkers - 1; i++) {
			List<AbstractAgent> assignedSimAgents = new ArrayList<>();
			for (int j = 0; j < nAssignedAgentsPerWorker; j++) {
				assignedSimAgents.add(simAgents.get(index));
				index++;
			}
			
			WorkerAgent worker = new WorkerAgent("worker-"+i, assignedSimAgents, dt, stopFlag);
			workers.add(worker);
		}
		
		List<AbstractAgent> assignedSimAgents = new ArrayList<>();
		while (index < simAgents.size()) {
			assignedSimAgents.add(simAgents.get(index));
			index++;
		}

		WorkerAgent worker = new WorkerAgent("worker-"+(nWorkers-1), assignedSimAgents, dt, stopFlag);
		workers.add(worker);

		log("starting the simulation loop.");

		int step = 0;
		currentWallTime = System.currentTimeMillis();

		try {
			while (!stopFlag.isSet() &&  step < numSteps) {
				futures.clear();
				System.out.println("Step " + step);
				simEnv.step(dt);
				simEnv.cleanActions();
				workers.forEach(w -> {
					CompletableFuture<Void> future = CompletableFuture.runAsync(w::execute, executor);
					futures.add(future);
				});

				// Wait for all agents to complete their steps
				CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
				try {
					allOf.get(); // Wait for all tasks to complete
				} catch (Exception e) {
					e.printStackTrace();
				}


				/* executed actions */
				simEnv.processActions();

				sim.notifyNewStep(t, simAgents, simEnv);
	
				if (toBeInSyncWithWallTime) {
					syncWithWallTime();
				}
				
				/* updating logic time */
				
				t += dt;
				step++;
			}	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		log("done");
		stopFlag.set();

		done.release();
	}

	private void syncWithTime(int nStepsPerSec) {
		this.toBeInSyncWithWallTime = true;
		this.nStepsPerSec = nStepsPerSec;
	}

	private void syncWithWallTime() {
		try {
			long newWallTime = System.currentTimeMillis();
			long delay = 1000 / this.nStepsPerSec;
			long wallTimeDT = newWallTime - currentWallTime;
			currentWallTime = System.currentTimeMillis();
			if (wallTimeDT < delay) {
				Thread.sleep(delay - wallTimeDT);
			}
		} catch (Exception ex) {}
		
	}
	
	private void log(String msg) {
		System.out.println("[MASTER] " + msg);
	}
	
	
}
