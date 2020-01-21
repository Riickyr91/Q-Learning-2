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
	private ArrayList<Vector2d> portalPos; //Posiciones del portal
	
	private Dimension world;
	
	private ArrayList<Observation>[][] grid;
	
	public static final int TYPEPORTAL = 2;
	public static final int TYPEAVATAR = 1;

	public static final int DANGERDISTANCE = 2;
	
	public static final float SPEEDLIMIT = 9.35f;
	
	public static final float NORTHINITPOINT =  1.07f;
	public static final float NORTHFINISHPOINT = 2.07f;
	
	/**
	 * Constructor.
	 * @param stateObs game observations.
	 */
 	public AgentState(StateObservation stateObs) {
		//Posicion del agente
 		agentPos = calculateCell(stateObs.getAvatarPosition(), stateObs.getBlockSize());			
			 		
 		//Posicion de la punta
		this.tip = 0;	
		
		//Grid
		this.grid = stateObs.getObservationGrid();
		
		//Dimensiones del mapa
		this.world = new Dimension(stateObs.getWorldDimension());
		this.world.height = world.height / stateObs.getBlockSize();
		this.world.width = world.width / stateObs.getBlockSize();
		
		//Posicion del portal
		this.portalPos = getPortalPos(stateObs);
		
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
		this.grid = obj.grid;
		this.world = obj.world;
	}
	
	/**
	 * Interprets game informations.
	 * 
	 * @param stateObs game observations.
	 */
	public void perceive(StateObservation stateObs) {
		//Posicion del agente
 		agentPos = calculateCell(stateObs.getAvatarPosition(), stateObs.getBlockSize());			
			
		//Velocidad del avión
		this.speedPlane = (float) stateObs.getAvatarSpeed();
		
		//Orientacion
		this.orientation = new Vector2d(stateObs.getAvatarOrientation());
		
		//Actualización de la punta del avion
		ACTIONS lastAction = stateObs.getAvatarLastAction();
		
		if(lastAction == ACTIONS.ACTION_LEFT) {
			this.tip += 0.2f;
		}
		else if(lastAction == ACTIONS.ACTION_RIGHT) {
			this.tip -= 0.2f;
		}
		
		//Actualización del array
		int[] stateValues = new int[7];

		for(int i = 0;i < stateValues.length ; i++) {
			stateValues[i] = 0;
		}
				
		//Percieve danger
		ArrayList<Observation>[][] grid = stateObs.getObservationGrid();

		int[] blockValues = new int[4];
	
		blockValues = inDanger(grid, agentPos, world);
		
		stateValues[POSFRONTBLOCK] = blockValues[POSFRONTBLOCK];
		stateValues[POSBACKBLOCK] = blockValues[POSBACKBLOCK];
		stateValues[POSLEFTBLOCK] = blockValues[POSLEFTBLOCK];
		stateValues[POSRIGHTBLOCK] = blockValues[POSRIGHTBLOCK];
		
		//Percieve speed
		if(speedPlane > SPEEDLIMIT) {
			stateValues[POSHIGHSPEED] = 1;
		} else {
			stateValues[POSHIGHSPEED] = 0;
		}
		
		//Percieve compassTip		
		stateValues[POSPLANETIP] = planeTipDirection();	
		
		//Percieve orientation
		stateValues[POSDANGERORIENTATION] = isDangerOrientation(grid);
			
		ArrayList<Integer> arrayStateValues = new ArrayList<>();
		for(int i = 0; i < stateValues.length; i++) {
			arrayStateValues.add(stateValues[i]);
		}
			
		super.update(arrayStateValues);
	}
	
	/**
	 * Calculates the direction of the tip of the agent.
	 * 
	 * @param tip radians where it points.
	 * @return 0 if tip points to NORTH.
	 */
	private int planeTipDirection() {
		int direction;	
		if(this.tip >= 6.28) {
			this.tip -= 6.28;
		}
		
		if (this.tip <= 0) {
			this.tip += 6.28;
		}

		if(tip >= NORTHINITPOINT && tip <= NORTHFINISHPOINT ) {
			direction = 0;
		} else {
			direction = 1;
		}
				
		return direction;

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
		
		int x = (int) Math.round(pos.x/blockSize);
		int y = (int) Math.round(pos.y/blockSize);
		
		cellCoords.set(x, y);
		
		return cellCoords;
	}
			
	/**
	 * Checks if exist any blocks in the positions specified without taking into account the highway orientation.
	 * 
	 * @param grid game grid.
	 * @param pos position which must be checked expressed in cell coordinates.
	 * @param orientation indicates the side of the road which must be checked.
	 * @return true if exist a car which could run over the agent.
	 */
	private int[] inDanger(ArrayList<Observation>[][] grid, Vector2d agentPos, Dimension world) {
				
		int posX = (int) agentPos.x;
		int posY = (int) agentPos.y;
		
		int width = (int) world.width;
		int height = (int) world.height;
		
		int[] dangerValues = new int[5];

		for(int i = 0;i < dangerValues.length ; i++) {
			dangerValues[i] = 0;
		}
		
		int blockPositions;
		
		int iteradorY;
		int iteradorX;
		
		int observationY;
		int observationX;
		
		//FRONTDANGER		
		if((posY - DANGERDISTANCE) < 0) {
			observationY = 0;
		} else {
			observationY = posY - DANGERDISTANCE;
		}
		
		iteradorY = posY - 1;
		iteradorX = posX - 1;
		blockPositions = 0;
		
		while(iteradorY >= observationY  && blockPositions == 0) {
			while(iteradorX <= posX + 1 && blockPositions == 0) {
				if(iteradorX >= 0 && iteradorX <= width - 1) { 
					if(!grid[iteradorX][iteradorY].isEmpty()) {
						if((isThisCategory(grid[iteradorX][iteradorY].get(0), TYPEPORTAL) && speedPlane > SPEEDLIMIT) ||
								!isThisCategory(grid[iteradorX][iteradorY].get(0), TYPEAVATAR)) 
							blockPositions++;
					}
				}
				iteradorX++;
			}
			iteradorX = posX - 1;
			iteradorY--;
		}

		if(blockPositions > 0 || posY == 0) dangerValues[POSFRONTBLOCK] = 1;
		
		//BACKDANGER
		if ((posY + DANGERDISTANCE) > height - 1) {
			observationY = height - 1;
		} else {
			observationY = posY + DANGERDISTANCE;
		}
		
		iteradorY = posY + 1;
		iteradorX = posX - 1;
		blockPositions = 0;

		while(iteradorY <= observationY  && blockPositions == 0) {
			while(iteradorX <= posX + 1 && blockPositions == 0) {
				if(iteradorX >= 0 && iteradorX <= width - 1) {
					if(!grid[iteradorX][iteradorY].isEmpty()) {
						if((isThisCategory(grid[iteradorX][iteradorY].get(0), TYPEPORTAL) && speedPlane > SPEEDLIMIT) ||
								!isThisCategory(grid[iteradorX][iteradorY].get(0), TYPEAVATAR)) 
							blockPositions++;
					}
				}
				iteradorX++;
			}
			iteradorX = posX - 1;
			iteradorY++;
		}

		if(blockPositions > 0 || posY == height - 1) dangerValues[POSBACKBLOCK] = 1;
				
		//LEFTDANGER		
		if(posX - DANGERDISTANCE < 0) {
			observationX = 0;
		} else {
			observationX = posX - DANGERDISTANCE;
		}
		
		iteradorX = observationX;
		blockPositions = 0;
		
		while (iteradorX < posX && blockPositions == 0) {
			if (!grid[iteradorX][posY].isEmpty()) {
				if (!isThisCategory(grid[iteradorX][posY].get(0), TYPEPORTAL) && !isThisCategory(grid[iteradorX][posY].get(0), TYPEAVATAR)) {
					blockPositions++;
				}
			}
			iteradorX++;
		}
		
		if(blockPositions > 0 || posX == 0) dangerValues[POSLEFTBLOCK] = 1;
		
		//RIGHTDANGER					
		if(posX + DANGERDISTANCE > width - 1) {
			observationX = width - 1;
		} else {
			observationX = posX + DANGERDISTANCE;
		}
		
		iteradorX = observationX;
		blockPositions = 0;
		
		while (iteradorX > posX && blockPositions == 0) {
			if (!grid[iteradorX][posY].isEmpty()) {
				if (!isThisCategory(grid[iteradorX][posY].get(0), TYPEPORTAL) && !isThisCategory(grid[iteradorX][posY].get(0), TYPEAVATAR)) {
					blockPositions++;
				}
			}
			iteradorX--;
		}
		
		if(blockPositions > 0 || posX == width - 1) dangerValues[POSRIGHTBLOCK] = 1;
			
		//Check if portal
		if(posY - 1 >= 0 && !grid[posX][posY - 1].isEmpty()) {
			if (isThisCategory(grid[posX][posY-1].get(0), TYPEPORTAL) && speedPlane < SPEEDLIMIT) {
				dangerValues[POSFRONTBLOCK] = 0;
			}
		}
			
		if(posY + 1 <= height - 1 && !grid[posX][posY + 1].isEmpty()) {
			if (isThisCategory(grid[posX][posY + 1].get(0), TYPEPORTAL) && speedPlane < SPEEDLIMIT) {
				dangerValues[POSBACKBLOCK] = 0;
			}
		}
		
		if(posX - 1 >= 0 && !grid[posX - 1][posY].isEmpty()) {
			if (isThisCategory(grid[posX - 1][posY].get(0), TYPEPORTAL) && speedPlane < SPEEDLIMIT) {
				dangerValues[POSLEFTBLOCK] = 0;
			}
		}
		
		if(posX + 1 <= width - 1 && !grid[posX + 1][posY].isEmpty()) {
			if (isThisCategory(grid[posX + 1][posY].get(0), TYPEPORTAL) && speedPlane < SPEEDLIMIT) {
				dangerValues[POSRIGHTBLOCK] = 0;
			}
		}
		
		return dangerValues;
	}

	/**
	 * Return if the observation is of the category
	 * @param obs observation to check
	 * @param category category to check
	 * @return true if the observation is of the category
	 */
	private boolean isThisCategory(Observation obs, int category) {
		return obs.itype == category;
	}
	
	/**
	 * Return if the orientation points to a block.
	 * 
	 * @param grid maps of the game.
	 * @return 0 if the position where points, is not a block.
	 */
	private int isDangerOrientation(ArrayList<Observation>[][] grid) {
		int posX = (int) (agentPos.x + (2*orientation.x));
		int posY = (int) (agentPos.y + (2*orientation.y));
		
		if(posX < 0 || posX > world.width - 1 || posY < 0 || posY > world.height - 1) {
			System.out.println("Estoy fuera del mapa");
			return 1;
		}
				
		if(!grid[posX][posY].isEmpty()) {
			if(!isThisCategory(grid[posX][posY].get(0), TYPEAVATAR))
				if(!isThisCategory(grid[posX][posY].get(0), TYPEPORTAL) || speedPlane > SPEEDLIMIT) {
					return 1;
				}
		}
		
		return 0;
		
	}
	
	/**
	 * Return the nearest portal.
	 * 
	 * @return the nearest portal.
	 */
	private ArrayList<Vector2d> getPortalPos(StateObservation stateObs) {

		ArrayList<Vector2d> portals = new ArrayList<>();
		
		for (ArrayList<Observation> v : stateObs.getImmovablePositions()) { // es un arraylist de array
			for (Observation a : v) { // es un array de observation
				if (a.itype == 2 && (a.position.y > agentPos.y)) {
					portals.add(calculateCell(a.position, stateObs.getBlockSize()));
				}
			}
		}
		
		
		Vector2d portal = new Vector2d(portals.get(0));

		double distancia = agentPos.dist(portal);
		
		for(int i = 1; i < portals.size(); i++) {
			double newDist = agentPos.dist(portals.get(i));
			if(distancia > newDist) {
				distancia = newDist;
				portal = portals.get(i);
			}
		}
		
		ArrayList<Vector2d> samePortals = new ArrayList<>();
		samePortals.add(portal);
		
		int posX = (int) portal.x;
		int posY = (int) portal.y;
				
		for (int i = 1; i < portals.size(); i++) {
			if (posX + i <= world.width - 1) {
				if (!grid[posX + i][posY].isEmpty()) {
					if (isThisCategory(grid[posX + i][posY].get(0), TYPEPORTAL)) {
						samePortals.add(new Vector2d(posX + i, posY));
					} else {
						break;
					}
				} else {
					break;
				}
			} else {
				break;
			}
		}
		
		for (int i = 1; i < portals.size(); i++) {
			if (posX - i >= 0) {
				if (!grid[posX - i][posY].isEmpty()) {
					if (isThisCategory(grid[posX - i][posY].get(0), TYPEPORTAL)) {
						samePortals.add(new Vector2d(posX - i, posY));
					} else {
						break;
					}
				} else {
					break;
				}
			} else {
				break;
			}
		}
			
		return samePortals;

	}
	
	/**
	 * Return the distance to portalPos.
	 * 
	 * @return the distance to portalPos.
	 */
	float getDistance2Portal() {
		int x = 0;
		
		for(int i = 0; i < portalPos.size(); i++) {
			x += portalPos.get(i).x;
		}
		
		x = (int) (x / portalPos.size());
		
		return (float) (agentPos.dist(x,portalPos.get(0).y));
		
	}
	
	/**
	 * Return the speed of the plane.
	 * @return the speed of the plane.
	 */
	public float getSpeed(){
		return this.speedPlane;
	}
	
	/**
	 * Return the position of the tip.
	 * @return the position of the tip.
	 */
	public float getTip(){
		return this.tip;
	}
	
	/**
	 * @return agent position in cell coordinates.
	 */
	public Vector2d getAgentPos() {
		return agentPos;
	}
	
	/**
	 * Returns a String with the information of the Object.
	 */
	@Override
	public String toString() {
		String str = super.toString();
		str +=  "\nAgent position = " + agentPos.toString() + 
				"\nOrientacion = " + orientation.toString() + 
				"\nHacia donde apunta = " + this.tip +
				"\nSpeed = " + this.speedPlane + 
				"\nPosicion del portal = " + portalPos + 
				"\nDimensiones del mundo = " + world.toString() + "\n";
		
		return str;
	}
				
}
