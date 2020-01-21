package ricardomanuelruizalu.mytools;

import java.util.Random;

import ontology.Types.ACTIONS;
import ricardomanuelruizalu.matrix.QTable;

/**
 *  Defines the Q-learning algorithm.
 *  
 * @author Ricardo Manuel Ruiz Diaz.
 */
public class QLearning {

	/**
	 * Privates attributes.
	 */
	private QTable qTable;
	private float gamma;
	private float alpha;
	public static double time = 0;
	private float epsilon;
	
	private boolean winCounter;
	private boolean deadCounter;
	
	private final float CONSTANT = 18000;
	
	private final float TIPREWARD = 800;
	private final float BESTTIPREWARD = 50;
	
	private final float SPEEDREWARD = 700;
	private final float BESTSPEEDREWARD = 0;
	
	private final float DEADREWARD = 1500;
	private final float WINREWARD = 1000;
	
	private final float DISTANCEFACTOR = 700;
	
	/**
	 * Constructor. Initializes the Qtable.
	 * @param qTable initial Qtable.
	 */
	public QLearning(QTable qTable) {
		this.qTable = qTable;
		gamma = 0.5f;
		alpha = 0.8f;
		epsilon = 0.8f;
		this.deadCounter = false;
		this.winCounter = false;
	}
	
	/**
	 * Save the Qtable information into CSV format.
	 * 
	 * @param path path to save the Qtable information.
	 */
	public void saveQTable(String path) {
		qTable.toCSV(path);
	}
	
	/**
	 * Execute the Q-learning formula.
	 * 
	 * @param previousState previous state.
	 * @param lastAction last action.
	 * @param currentState current state.
	 * @return next action.
	 */
	public ACTIONS learn(AgentState previousState, ACTIONS lastAction, AgentState currentState) {

		float sample = reward(previousState, lastAction, currentState) + gamma * qTable.getMaxQValue(currentState);
		float newQValue = (1-alpha)*qTable.get(previousState, lastAction) + alpha*sample;
		qTable.set(previousState, lastAction, newQValue);
		
		updateConstants();
					
		return nextAction(currentState);
	}
	
	/**
	 * Reward function.
	 * 
	 * @param previousState previous state.
	 * @param lastAction last action.
	 * @param currentState current state.
	 * @return reward.
	 */
	private float reward(AgentState previousState, ACTIONS lastAction, AgentState currentState) {
		
		float finalReward = 0;
		
		//Distance reward
		float currentDistance = currentState.getDistance2Portal();
		float previousDistance = previousState.getDistance2Portal();
		
		float distanceReward = 0;		
		float difDistance = previousDistance - currentDistance;
		
		distanceReward += difDistance * DISTANCEFACTOR;
		
		finalReward += distanceReward;			
		
		//Dead reward
		if (deadCounter) {
			finalReward -= DEADREWARD;
		}
		
		//Win reward
		if (winCounter) {
			finalReward +=  WINREWARD;
		}
		
		//Speed reward
		if(currentState.isHighSpeed()) {
			finalReward -= SPEEDREWARD;
		}
			
		//Tip reward
		if(!currentState.isPlaneTip()) {
			finalReward -= TIPREWARD;
			
			if(currentState.getTip() < AgentState.NORTHINITPOINT && 
				previousState.getTip() < AgentState.NORTHINITPOINT && 
				currentState.getTip() > previousState.getTip()) {
				finalReward += BESTTIPREWARD;
			}
			else if(currentState.getTip() >= 0 && previousState.getTip() <= 6.28) {
				finalReward += BESTTIPREWARD;
			}
			else if((currentState.getTip() > AgentState.NORTHFINISHPOINT && currentState.getTip() <= 4.71f) && 
					(previousState.getTip() > AgentState.NORTHFINISHPOINT && previousState.getTip() <= 4.71f) &&
					currentState.getTip() < previousState.getTip()) {
				finalReward += BESTTIPREWARD;
			}
			else if((currentState.getTip() > AgentState.NORTHFINISHPOINT && currentState.getTip() > 4.71f) && 
					(previousState.getTip() > AgentState.NORTHFINISHPOINT && previousState.getTip() > 4.71f) &&
					currentState.getTip() > previousState.getTip()) {
				finalReward += BESTTIPREWARD;
			}
			else {
				finalReward -= BESTTIPREWARD;
			}
		}
		
		return finalReward;
	}
	
	/**
	 * Agent has won.
	 */
	public void agentWin() {
		winCounter = true;
	}
	
	/**
	 * Agent has died.
	 */
	public void agentDead() {	
		deadCounter = true;
	}
	
	/**
	 * Return the next action taking into acount an 
	 * exploration policity.
	 * 
	 * @param currentState current state.
	 * @return next action.
	 */
	private ACTIONS nextAction(AgentState currentState) {
		Random rd = new Random();
		float randomNumber = Math.abs(rd.nextFloat());

//		System.out.println(currentState);
		
//		return ACTIONS.ACTION_NIL;	
				
		if (randomNumber < epsilon) {
			return qTable.getRandomAction();
		} else {
			return qTable.getBestAction(currentState);
		}
	}
	
	/**
	 * Update Q-learning constants.
	 */
	private void updateConstants() {
		alpha = (float) (0.9*CONSTANT/(CONSTANT + time));
		epsilon = (float) (0.9*CONSTANT/(CONSTANT + time));
		
		time++;
	}
	
	/**
	 * @return learning factor.
	 */
	public float getAlpha() {
		return alpha;
	}
	
}
