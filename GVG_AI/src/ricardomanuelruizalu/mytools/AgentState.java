package ricardomanuelruizalu.mytools;

import java.awt.Dimension;
import java.util.ArrayList;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.Vector2d;

/**
 * Defines an agent state.
 * 
 * @author Ricardo Manuel Ruiz Diaz.
 *
 */
public class AgentState extends State {

	/**
	 * Private attributes.
	 */
	private Vector2d agentPos;

	private Vector2d orientation; //Orientacion hacia donde se dirige
	private float tip; //Hacia donde apunta
	
	private float speedPlane; //Velocidad del avión
	
	private ArrayList<Vector2d> portalPos = new ArrayList<>(); //Posiciones del portal
	
	public static int BLOCKSIZE;
	
	public static final int TYPEPORTAL = 2;
	public static final int IMMOVABLE = 4;
	
	public static final float EASTPOINT =  0.78f;
	public static final float NORTHPOINT = 2.35f;
	public static final float WESTPOINT = 3.92f;
	public static final float SOUTHPOINT = 5.49f;
	
	/**
	 * Constructor.
	 * @param stateObs game observations.
	 */
 	public AgentState(StateObservation stateObs, float planeTip) {
		BLOCKSIZE = stateObs.getBlockSize();
				
		//Actualización de la punta del avion
		ACTIONS lastAction = stateObs.getAvatarLastAction();
		
		if(lastAction == ACTIONS.ACTION_LEFT) {
			this.tip = planeTip + 0.2f;
		}
		else if(lastAction == ACTIONS.ACTION_RIGHT) {
			this.tip = planeTip - 0.2f;
		}
		else {
			this.tip = planeTip;
		}
		
		//Posicion del portal
		for (ArrayList<Observation> v : stateObs.getImmovablePositions()) { // es un arraylist de array
			for (Observation a : v) { // es un array de observation
				if (a.itype == 2) {
					portalPos.add(calculateCell(a.position, BLOCKSIZE));
				}
			}	
		}
		
		perceive(stateObs);
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param obj object to be copied.
	 */
	public AgentState(AgentState obj) {
		super(obj);
		
		this.agentPos = new Vector2d(obj.agentPos);
		
		this.orientation = new Vector2d(obj.orientation);
		this.tip = obj.tip;
		
		this.speedPlane = obj.speedPlane;	
				
		this.portalPos = new ArrayList<Vector2d>(obj.portalPos);
	}
	
	/**
	 * Interprets game informations.
	 * 
	 * @param stateObs game observations.
	 */
	public void perceive(StateObservation stateObs) {
		
		//Posicion del agente
		agentPos = calculateCell(stateObs.getAvatarPosition(), BLOCKSIZE);	
		
		//Velocidad del agente
		speedPlane = (float) stateObs.getAvatarSpeed();

		//Orientación del agente
		orientation = stateObs.getAvatarOrientation();
		
		//Actualización del array
		int[] stateValues = new int[7];

		for(int i = 0;i < stateValues.length ; i++) {
			stateValues[i] = 0;
		}
		
		//Percieve speed
		if(speedPlane >= 10) {
			stateValues[POSHIGHSPEED] = 1;
		} else {
			stateValues[POSHIGHSPEED] = 0;
		}
		
		//Percieve compassTip		
		stateValues[POSCOMPASSTIP] = compassDirection(this.tip);	
		
		//Percieve orientation
		stateValues[POSCOMPASSORIENTATION] = compassOrientation(orientation);
		
		//Percieve danger
		ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
		Dimension world = stateObs.getWorldDimension();
		
		int[] dangerValues = new int[5];
		
		dangerValues = inDanger(grid, agentPos, world);

		stateValues[POSFRONTDANGER] = dangerValues[POSFRONTDANGER];
		stateValues[POSBACKDANGER] = dangerValues[POSBACKDANGER];
		stateValues[POSLEFTDANGER] = dangerValues[POSLEFTDANGER];
		stateValues[POSRIGHTDANGER] = dangerValues[POSRIGHTDANGER];
	
		ArrayList<Integer> arrayStateValues = new ArrayList<>();
		for(int i = 0; i < stateValues.length; i++) {
			arrayStateValues.add(stateValues[i]);
		}
				
		super.update(arrayStateValues);
	}
	
	/**
	 * Calculates the direction of the compass of the agent.
	 * 
	 * @param portalPos position of the goal.
	 * @param agentPos position of the agent expressed in cell coordinates.
	 * @return the direction of the compass.
	 */
	private int compassDirection(float tip) {
				
		if(tip > 6.38) {
			tip -= 6.38;
		}
		
		if(tip <=  EASTPOINT) return EAST;
		if(tip <= NORTHPOINT && tip > EASTPOINT) return NORTH;	
		if(tip <= WESTPOINT && tip > NORTHPOINT) return WEST;
		if(tip <= SOUTHPOINT && tip > WESTPOINT) return SOUTH;
		
		return EAST;

	}
	
	public int compassOrientation(Vector2d orientation) {
		
		double x = orientation.x;
		double y = orientation.y;
		
		if(x >= 0.5 ) return EAST;
		if(x <= -0.5) return WEST;
		if(y <= -0.5) return NORTH;
		if(y >= 0.5) return SOUTH;
		
		return NORTH;
		
	}
	
	/**
	 * Cast the position expressed in reals values to position expressed in cell coordinates.
	 * 
	 * @param pos position expressed in reals values.
	 * @param blockSize reference measure to cast positions.
	 * @return position expressed in cell coordinates.
	 */
	public static Vector2d calculateCell(Vector2d pos, int blockSize) {
		Vector2d cellCoords = new Vector2d();
		
		int x = (int) (pos.x/blockSize);
		int y = (int) (pos.y/blockSize);
		
		cellCoords.set(x, y);
		
		return cellCoords;
	}
			
	/**
	 * Checks if exist a danger in the position specified without taking into account the highway orientation.
	 * 
	 * @param grid game grid.
	 * @param pos position which must be checked expressed in cell coordinates.
	 * @param orientation indicates the side of the road which must be checked.
	 * @return true if exist a car which could run over the agent.
	 */
	private int[] inDanger(ArrayList<Observation>[][] grid, Vector2d agentPos, Dimension world) {
				
		int posX = (int) agentPos.x;
		int posY = (int) agentPos.y;
		
		int width = (int) world.width/BLOCKSIZE;
		int height = (int) world.height/BLOCKSIZE;
		
		int[] dangerValues = new int[5];
			
		boolean danger = false;
		
		//FRONTDANGER
		ArrayList<Observation> frontRow = new ArrayList<>();
		int observationY;
		
		if(posY - 2 < 0) {
			observationY = 0;
		} else {
			observationY = posY - 2;
		}
		
		for(int i = posX - 1; i <= posX + 1; i++) {
			if(i >= 0 && i <= width - 1) {
				for(int j = observationY; j < posY; j++) {
					if(!grid[i][j].isEmpty())
						if(!isThisCategory(grid[i][j].get(0), TYPEPORTAL))
							frontRow.add(grid[i][j].get(0));				
				} 
			}
		}
		
		if(frontRow.size() > 0) dangerValues[POSFRONTDANGER] = 1;
		
		//BACKDANGER
		ArrayList<Observation> backRow = new ArrayList<>();

		if (posY + 2 > height - 1) {
			observationY = height - 1;
		} else {
			observationY = posY + 2;
		}

		for (int i = posX - 1; i <= posX + 1; i++) {
			if (i >= 0 && i <= width - 1) {
				for (int j = observationY; j > posY; j--) {
					if (!grid[i][j].isEmpty())
						if (!isThisCategory(grid[i][j].get(0), TYPEPORTAL))
							backRow.add(grid[i][j].get(0));
				}
			}
		}

		if (backRow.size() > 0)	dangerValues[POSBACKDANGER] = 1;
		
		if(dangerValues[POSFRONTDANGER] == 1 || dangerValues[POSBACKDANGER] == 1) {
			danger = true;
		}
		
		//LEFTDANGER
		ArrayList<Observation> leftRow = new ArrayList<>();
		int observationX;
		
		if(posX - 2 < 0) {
			observationX = 0;
		} else {
			observationX = posX - 2;
		}
		
		if(danger) {
			
			for (int w = observationX; w < posX; w++) {
				if (!grid[w][posY].isEmpty())
					if (!isThisCategory(grid[w][posY].get(0), TYPEPORTAL))
						leftRow.add(grid[w][posY].get(0));
			}	
			
		} else {
			for(int i = posY - 1; i <= posY + 1; i++) {
				if(i >= 0 && i <= height - 1) {
					for(int j = observationX; j < posX; j++) {
						if(!grid[j][i].isEmpty())
							if(!isThisCategory(grid[j][i].get(0), TYPEPORTAL))
								leftRow.add(grid[j][i].get(0));	
					}
				} 
			}
		}
		if(leftRow.size() > 0) dangerValues[POSLEFTDANGER] = 1;
		
		//RIGHTDANGER
		ArrayList<Observation> rightDanger = new ArrayList<>();
		
		if(posX + 2 > width - 1) {
			observationX = width - 1;
		} else {
			observationX = posX + 2;
		}
		
		if(danger) {
			
			for (int w = observationX; w > posX; w--) {
				if (!grid[w][posY].isEmpty())
					if (!isThisCategory(grid[w][posY].get(0), TYPEPORTAL))
						rightDanger.add(grid[w][posY].get(0));
			}
			
		} else {
		
			for(int i = posY - 1; i <= posY + 1; i++) {
				if(i >= 0 && i <= height - 1) {
					for(int j = observationX; j > posX; j--) {
						if(!grid[j][i].isEmpty())
							if(!isThisCategory(grid[j][i].get(0), TYPEPORTAL))
								rightDanger.add(grid[j][i].get(0));
					}
				} 
			}
		}
		
		if(rightDanger.size() > 0) dangerValues[POSRIGHTDANGER] = 1;
			
		//Check if portal is down
		for(int z = posY + 1; z <= observationY; z++) {
			if(!grid[posX][z].isEmpty())
				if (isThisCategory(grid[posX][z].get(0), TYPEPORTAL))
					dangerValues[POSBACKDANGER] = 0;
		}
		
		return dangerValues;
	}
		
	private boolean isThisCategory(Observation obs, int category) {
		return obs.itype == category;
	}
	
	/**
	 * Returns a String with the information of the Object.
	 */
	@Override
	public String toString() {
		String str = super.toString();
		str +=  "\nAgent position = " + agentPos.toString() + 
				"\nPortal position = " + portalPos.toString() + 
				"\nSpeed = " + this.speedPlane + 
				"\nHacia donde apunta = " + this.tip + "\n\n";
		
		return str;
	}
	
	/**
	 * @return agent position in cell coordinates.
	 */
	public Vector2d getAgentPos() {
		return agentPos;
	}
	
	public Vector2d getOrientation() {
		return orientation;
	}
	
	public float getTip() {
		return tip;
	}
	
	public float getSpeedPlane() {
		return speedPlane;
	}

	public ArrayList<Vector2d> getPortalPos() {
		return portalPos;
	}
	
}
