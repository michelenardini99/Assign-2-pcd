package gui;

import simtraffic_conc_examples.TrafficSimulationSingleRoadMassiveNumberOfCars;

public class RunTrafficSimulationMassiveTest {
    public static void main(String[] args) throws InterruptedException {

        int nWorkers = Runtime.getRuntime().availableProcessors() + 1;

        int numCars = 5000;
        int nSteps = 100;

        var simulation = new TrafficSimulationSingleRoadMassiveNumberOfCars(numCars);

        log("Running the simulation: " + numCars + " cars, for " + nSteps + " steps ...");

        simulation.configureNumWorkers(nWorkers);
        SimulationGUI gui = new SimulationGUI(nSteps);

        SimulationController controller = new SimulationController(simulation);
        controller.attach(gui);
        gui.display();


        //Sequential
        //[ SIMULATION ] Running the simulation: 5000 cars, for 100 steps ...
        //[ SIMULATION ] Completed in 38187 ms - average time per step: 381 ms

        //Completed in 12577 ms
        //time per steps: 118 ms
        //cpu-usage: 70%
        //thread-pool: 1 per il controller della gui
        //thread-pool: 9 per i worker agent

        //Cuncurrent
        //Completed in 25155 ms - average time per step: 240 ms

    }

    private static void log(String msg) {
        System.out.println("[ SIMULATION ] " + msg);
    }
}
