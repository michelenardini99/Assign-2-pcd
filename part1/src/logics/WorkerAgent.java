package logics;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;

public class WorkerAgent{
	
	private List<AbstractAgent> assignedSimAgents;
	private int dt;
	private Flag stopFlag;
	private String id;
	
	public WorkerAgent(String id, List<AbstractAgent> assignedSimAgents, int dt, Flag flag) {
		this.id = id;
		this.assignedSimAgents = assignedSimAgents;
		this.dt = dt;
		this.stopFlag = flag;
	}
	
	public void execute() {
		assignedSimAgents.forEach(w -> w.step(dt));
	}
	
	public void log(String msg) {
		System.out.println("[ Worker " + id +"] " + msg);
	}
	
	
}
