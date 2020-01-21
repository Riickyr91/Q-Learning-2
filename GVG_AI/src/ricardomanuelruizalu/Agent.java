package ricardomanuelruizalu;

import java.util.ArrayList;
import java.util.Random;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import ricardomanuelruizalu.mytools.Brain;
import ricardomanuelruizalu.mytools.QLearning;
import tools.ElapsedCpuTimer;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {
    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;
    
    /**
     * List of available actions for the agent
     */
   
    private StateObservation stateObs;
    private Brain brain;
    
    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    	String savePath = "./QTable/Qtable.txt";
        randomGenerator = new Random();
        brain = new Brain(stateObs, savePath);
        this.stateObs = stateObs;
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    //Recibe la percepción, piensa y devuelve la acción.
    //ElapsedCpuTimer cuanto queda de tiempo
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		
    	this.stateObs = stateObs;
    	    	
//    	for(int i = 0; i < 10000; i++) {
//			System.out.println("");
//		}
		
    	ACTIONS act = brain.act(stateObs);
		return act;
    	
//		return brain.learn(stateObs);

//    	return ACTIONS.ACTION_NIL;
    }
    
	public void close(double score) {
   	
    	//Update score    	    	
    	if(score > 0) {
    		brain.agentWin();
    	} else {
    		brain.agentDead();
    	}
//    
		//Save QTable
    	brain.saveQTable();
    	System.out.println("QTable saved!");
    	
    	//Time and alpha parameters
        double time = QLearning.time;
    	double alpha = brain.getAlpha();
		System.out.println("Time = " + time + " Alpha = " + alpha);

    	//Data to excell
//    	String row = Double.toString(time) + "," + Double.toString(alpha) + "," + Double.toString(score) + "\n";
//    	IOModule.write("./time_alpha_score.csv", row, true);
    	

	}
}
