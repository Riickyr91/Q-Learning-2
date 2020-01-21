package ricardomanuelruizalu.mytools;

import java.util.ArrayList;

import core.game.StateObservation;
import ontology.Types.ACTIONS;
import ricardomanuelruizalu.matrix.QTable;

/**
 * Defines the agent brain.
 * 
 * @author Ricardo Manuel Ruiz Diaz.
 */
public class Brain {
	
	/**
	 * Privates attributes.
	 */
	private QLearning qLearning;
	
	private AgentState currentState;
	private AgentState previousState;
	
	private ACTIONS lastAction;
	
	private String savePath;
		
	private QTable qTable;
	
	/**
	 * Constructor. Initializes the brain with the observations introduced by parameters.
	 * 
	 * @param stateObs game observations.
	 * @param savePath CSV file path to load the information of the Qtable.
	 */
	public Brain(StateObservation stateObs, String savePath) {
		this.savePath = savePath;
		
        currentState = new AgentState(stateObs);
        previousState = new AgentState(stateObs);
        
        lastAction = stateObs.getAvatarLastAction();
        
        ArrayList<State> states = StateGenerator.generate();
        
        ArrayList<ACTIONS> actions = stateObs.getAvailableActions(true);
        
		qTable = new QTable(states , actions, savePath);
		
		qLearning = new QLearning(qTable);

	}
	
	/**
	 * Percieve the information of the game and learn.
	 * 
	 * @param stateObs game observations.
	 * @return next game action.
	 */
	public ACTIONS learn(StateObservation stateObs) {

		previousState = new AgentState(currentState);
		currentState.perceive(stateObs);
		
		lastAction = stateObs.getAvatarLastAction();
	
        int ticks = stateObs.getGameTick(); 
        //IOModule.write("./History.txt", ticks + "\n" + currentState.toString(), true);
        
		return qLearning.learn(previousState, lastAction, currentState);

	}
	
	/**
	 * Percieve the information of the game and return the best action.
	 * 
	 * @param stateObs game observations.
	 * @return best action.
	 */
	public ACTIONS act(StateObservation stateObs) {

		currentState.perceive(stateObs);
        
		int ticks = stateObs.getGameTick();
        ACTIONS action = qTable.getBestAction(currentState);
        
        IOModule.write("./History.txt", ticks + "\n" + currentState.toString()  + "\n"  + action.toString() + "\n\n", true);

        return action;
	}
	
	/**
	 * Save the Qtable information.
	 */
	public void saveQTable() {
		qLearning.saveQTable(savePath);
	}

	/**
	 * Let qlearning know if he has won.
	 */
	public void agentWin() {
		qLearning.agentWin();
		qLearning.learn(previousState, lastAction, currentState);
	}
	
	/**
	 * Let qlearning know if he has died.
	 */
	public void agentDead() {
		qLearning.agentDead();
		qLearning.learn(previousState, lastAction, currentState);
	}
	
	/**
	 * @return alpha value of the Q-learning.
	 */
	public float getAlpha() {
		return this.qLearning.getAlpha();
	}
	
}
