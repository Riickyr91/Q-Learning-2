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
	
	private final float CONSTANT = 25000; //N = 2 -> 45000
	
	private final float TIPREWARD = 400; // 400
	private final float BESTTIPREWARD = 100	; // 100
	
	private final float SPEEDREWARD = 200; //1500;
	
	private final float DEADREWARD = 2000; //2000;
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

		double currentX = currentState.getAgentPos().x;
		double previousX = previousState.getAgentPos().x;
			
//		if(currentState.isPortalWest()) {
//			if(currentX < previousX) {
//				distanceReward += DISTANCEFACTOR;
//			}
//		}
//
//		else if(currentState.isPortalEast()) {
//			if(currentX > previousX) {
//				distanceReward += DISTANCEFACTOR;
//			}
//		}
		
		finalReward += distanceReward;
		
		//Dead reward
		if (deadCounter || (winCounter && !currentState.isPlaneTip())) {
			finalReward -= DEADREWARD;
		}
		
		//Win reward
		if (winCounter && currentState.isPlaneTip()) {
//			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			finalReward +=  WINREWARD;
		}
		
		// Speed reward
		if(currentState.getSpeed() > AgentState.SPEEDLIMIT) {
			finalReward -= SPEEDREWARD;
		} 		
		
//		// LateralSpeedReward
//		if(currentState.isHighLateralSpeed() && (currentState.isPortalEast() || currentState.isPortalWest())) {
//			double x = currentState.agentOrientation().x;
//			double y = currentState.agentOrientation().y;
//			
//			double angle = Math.atan(y/x);
//			
//			if((x > 0 && y < 0) || (x < 0 && y > 0)) {
//				angle = angle + Math.PI;
//			}
//			
//			float angleInit = (float) angle - 0.15f;
//			float angleFin = (float) angle + 0.15f;
//			
//			float currentTip = currentState.getTip();
//			float previousTip = previousState.getTip();
//			
//			if (currentTip < angleInit && currentTip > previousTip) {
//				finalReward += 2 * BESTTIPREWARD;
//			} else if (currentTip > angleFin && currentTip < previousTip) {
//				finalReward += 2 * BESTTIPREWARD;
//			}	
//			
//			if (currentTip > angleInit && currentTip < angleFin) {
//				finalReward += 2 * TIPREWARD;
//			}
//		
//		}
		
		//Correction de tip
		float currentTip = currentState.getTip();
		float previousTip = previousState.getTip();
	
		// Tip reward
		if (!currentState.isPlaneTip()) {// && !currentState.isHighLateralSpeed()) {
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
