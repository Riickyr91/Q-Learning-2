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
	
//	private final float CONSTANT = 50000;
	private final float CONSTANT = 16000;

	private final float TIPREWARD = 400;
	private final float TIPPUNISH = 400;
			
	private final float SPEEDREWARD = 1500;
	
	private final float WINREWARD = 1000;	
	
	private final float DISTANCEFACTOR = 100;
		
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
		
		boolean east = currentState.isPortalEast();
		boolean west = currentState.isPortalWest();
		
		int orientation = currentState.getOrientation();
		int displacement = currentState.getDisplacement();
		
		if(displacement == State.NORTH) {
			finalReward -= 1000;
		}
		
		//Orientación Y Desplazamiento
		if(east) {
			//ORIENTACION
			if(orientation == State.RIGHT) {
				finalReward += TIPREWARD;
			}
			else if(orientation == State.CENTER || orientation == State.LEFT) {
				finalReward -= TIPPUNISH;
			}
			else if(orientation == State.DANGERLEFT || orientation == State.DANGERRIGHT) {
				finalReward -= TIPPUNISH * 1.5f ;
			}
			
			//DESPLAZAMIENTO
			if(displacement == State.EAST) {
				finalReward += TIPREWARD;
			}
			else if(displacement == State.EASTSOUTH) {
				
			}
			else if(displacement == State.SOUTHEAST || displacement == State.SOUTH) {
				finalReward -= TIPPUNISH;
			}
			else if(displacement == State.SOUTHWEST || displacement == State.WESTSOUTH || displacement == State.WEST) {
				finalReward -= TIPPUNISH * 1.5f;
			}
			
		}
		
		else if(west) {
			//ORIENTACION
			if(orientation == State.LEFT) {
				finalReward += TIPREWARD;
			}
			else if(orientation == State.CENTER || orientation == State.RIGHT) {
				finalReward -= TIPREWARD;
			}
			else if(orientation == State.DANGERLEFT || orientation == State.DANGERRIGHT) {
				finalReward -= TIPPUNISH * 1.5f;
			}
			
			//DESPLAZAMIENTO
			if(displacement == State.WEST) {
				finalReward += TIPREWARD;
			}
			else if(displacement == State.WESTSOUTH) {
				
			}
			else if(displacement == State.SOUTH || displacement == State.SOUTHWEST) {
				finalReward -= TIPPUNISH;
			}
			else if(displacement == State.SOUTHEAST || displacement == State.EASTSOUTH  || displacement == State.EAST) {
				finalReward -= TIPPUNISH * 1.5f;
			}
		}
		
		else if (!east && !west) {
			//ORIENTACION
			if(orientation == State.CENTER) {
				finalReward += TIPREWARD;
			}
			else if(orientation == State.RIGHT || orientation == State.LEFT) {
				finalReward -= TIPPUNISH;
			}
			else if(orientation == State.DANGERLEFT || orientation == State.DANGERRIGHT) {
				finalReward -= TIPPUNISH * 1.5f;
			}
			
			//DESPLAZAMIENTO
			if(displacement == State.SOUTH) {
				finalReward += TIPREWARD;
			}
			else if(displacement == State.SOUTHEAST) {
				if(orientation == State.LEFT) {
					
				} else {
					finalReward -= TIPPUNISH;
				}
			}
			else if(displacement == State.SOUTHEAST) {
				if(orientation == State.RIGHT) {
					
				} else {
					finalReward -= TIPPUNISH;
				}			
			}
			else if(displacement == State.EAST || displacement == State.EASTSOUTH) {
				finalReward -= TIPPUNISH * 1.5f;
			}
			else if(displacement == State.WEST || displacement == State.WESTSOUTH) {
				finalReward -= TIPPUNISH * 1.5f;
			}			
		}
				
		//Distance reward
		float distanceReward = 0;

		double currentX = currentState.getAgentPos().x;
		double previousX = previousState.getAgentPos().x;
			
		if(currentState.isPortalWest()) {
			if(currentX < previousX) {
				distanceReward += DISTANCEFACTOR;
			}
		}

		else if(currentState.isPortalEast()) {
			if(currentX > previousX) {
				distanceReward += DISTANCEFACTOR;
			}
		}
		
		finalReward += distanceReward;
				
		//Win reward
		if (winCounter && currentState.getOrientation() != State.DANGERLEFT && currentState.getOrientation() != State.DANGERRIGHT) {
			finalReward +=  WINREWARD;
		}
		
		// Speed reward
		if(currentState.getSpeed() > AgentState.SPEEDLIMIT) {
			finalReward -= SPEEDREWARD;
		} 	
		else if( currentState.getSpeed() < AgentState.SPEEDLIMIT) {
			finalReward += 200;
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
