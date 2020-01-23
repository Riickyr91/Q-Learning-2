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
	
	private final float CONSTANT = 10000;
	
	private final float TIPREWARD = 2000; // 2000;
	private final float BESTTIPREWARD = 300; //300;
	
	private final float SPEEDREWARD = 1000; //1500;
	
	private final float DEADREWARD = 1500; //2000;
	private final float WINREWARD = 1000; //2000;
	
	private final float DISTANCEFACTOR = 100; //100;
		
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
		float distanceReward = 0;

		float currentDistance = 0;
		float previousDistance = 0;
	
		if(currentState.isPortalDown()) {
			currentDistance = currentState.getDistance2Portal();
			previousDistance = previousState.getDistance2Portal();
			if(currentState.isPlaneTip() && currentDistance > previousDistance) {
				distanceReward -= DISTANCEFACTOR;
			}
//			else if(currentDistance < previousDistance) {
//				distanceReward += DISTANCEFACTOR;
//			}
			
		}
//		
//		else if(currentState.isPortalEast()) {
//			currentDistance = (float) (currentState.getPortalPos().get(0).x - currentState.getAgentPos().x);
//			previousDistance = (float) (previousState.getPortalPos().get(0).x - previousState.getAgentPos().x);
//			if(currentDistance > previousDistance) {
//				distanceReward -= DISTANCEFACTOR;
//			}	
//		}
//
//		else if(currentState.isPortalWest()) {
//			currentDistance = (float) (currentState.getAgentPos().x - currentState.getPortalPos().get(0).x);
//			previousDistance = (float) (previousState.getAgentPos().x - previousState.getPortalPos().get(0).x);
//			if(currentDistance > previousDistance) {
//				distanceReward -= DISTANCEFACTOR;
//			}			
//		}
		
		finalReward += distanceReward;
		
		//Dead reward
		if (deadCounter) {
			finalReward -= DEADREWARD;
		}
		
		//Win reward
		if (winCounter) {
			finalReward +=  WINREWARD;
		}
		
		// Speed reward
		if(currentState.getSpeed() > AgentState.SPEEDLIMIT) {
			finalReward -= SPEEDREWARD;
		}
		
		
		//Correction de tip
		float currentTip = currentState.getTip();
		float previousTip = previousState.getTip();
		/*
		if(currentState.isPortalDown() && previousState.isPortalWest() && lastAction == ACTIONS.ACTION_RIGHT) {
			finalReward += TIPREWARD;
		}
		
		if(currentState.isPortalDown() && previousState.isPortalEast() && lastAction == ACTIONS.ACTION_LEFT) {
			finalReward += TIPREWARD;
		}	
		*/	
		// Tip reward
		if (!currentState.isPlaneTip()) {
			finalReward -= TIPREWARD;

			
			if (currentTip > Math.PI) {
				finalReward -= TIPREWARD;
			}
			
			if(currentTip == previousTip) {
				finalReward -= BESTTIPREWARD;
			}

			// Portal is to the left of the avatar
			if (currentState.isPortalWest()) {
				if ((currentTip < AgentState.WESTPOINTINIT && currentTip > previousTip) ||
						(currentTip >= 0 && previousTip >= 6)) {
					finalReward += BESTTIPREWARD;
				} else if ((currentTip > AgentState.WESTPOINTFINISH && currentTip <= 5.5f && previousTip <= 5.5f) && (currentTip < previousTip)) {
					finalReward += BESTTIPREWARD;
				} else if ((currentTip > 5.5f && previousTip > 5.5f) && (currentTip > previousTip)) {
					finalReward += BESTTIPREWARD;
				}
			}

			// Portal is to the right of the avatar
			else if (currentState.isPortalEast()) {
				if ((currentTip < AgentState.EASTPOINTINIT && currentTip > previousTip) ||
						(currentTip >= 0 && previousTip >= 6)) {
					finalReward += BESTTIPREWARD;
				} else if ((currentTip > AgentState.EASTPOINTFINISH && currentTip <= 3.92f && previousTip <= 3.92f)	&& (currentTip < previousTip)) {
					finalReward += BESTTIPREWARD;
				} else if ((currentTip > 3.92f && previousTip > 3.92f) && (currentTip > previousTip)) {
					finalReward += BESTTIPREWARD;
				}
			}

			// Portal is to under of the avatar
			else if (currentState.isPortalDown()) {
				if ((currentTip < AgentState.NORTHPOINTINIT && currentTip > previousTip) ||
						(currentTip >= 0 && previousTip >= 6)) {
					finalReward += BESTTIPREWARD;
				} else if ((currentTip > AgentState.NORTHPOINTFINISH && currentTip <= 4.71f && previousTip <= 4.71f ) && (currentTip < previousTip)) {
					finalReward += BESTTIPREWARD;
				} else if ((currentTip > 4.71f && previousTip > 4.71f) && (currentTip > previousTip)) {
					finalReward += BESTTIPREWARD;
				}
			}

		} else {

			finalReward += TIPREWARD;
						
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
//		
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
		alpha = (float) (CONSTANT/(CONSTANT + time));
		epsilon = (float) (CONSTANT/(CONSTANT + time));
		
		time++;
	}
	
	/**
	 * @return learning factor.
	 */
	public float getAlpha() {
		return alpha;
	}
	

}
