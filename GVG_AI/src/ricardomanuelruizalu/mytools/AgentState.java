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

	public static final int FRONTBACKDISTANCE = 3;
	public static final int LEFTRIGHTDISTANCE = 2;
	
	public static final float SPEEDLIMIT = 9.35f;
	public static final float LATERALSPEEDLIMIT = 4;
	
	public static final float WESTPOINTFINISH = 2.25f;
	public static final float WESTPOINTINIT = 1.95f;
	
	public static final float EASTPOINTFINISH = 1.05f;  	
	public static final float EASTPOINTINIT = 0.75f;

	public static final float NORTHPOINTFINISH = 1.65f;
	public static final float NORTHPOINTINIT =  1.35f;
	
	public static final float NORTHORIENTATION = 0.2f;
	public static final float SOUTHORIENTATION = 0.1f;
	
	public static final float XORIENTATION = 0.7f;
	
	/**
	 * Constructor.
	 * @param stateObs game observations.
	 */
 	public AgentState(StateObservation stateObs) {
 		agentPos = calculateCell(stateObs.getAvatarPosition(), stateObs.getBlockSize());		
		this.tip = 0;	
		this.grid = stateObs.getObservationGrid();
		
		this.world = new Dimension(stateObs.getWorldDimension());
		this.world.height = world.height / stateObs.getBlockSize();
		this.world.width = world.width / stateObs.getBlockSize();
		
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
			
 		//Posicion del portal
 		portalPos = getPortalPos(stateObs);
 		
		//Velocidad del avión
		this.speedPlane = (float) stateObs.getAvatarSpeed();
		
		//Orientacion
		this.orientation = new Vector2d(stateObs.getAvatarOrientation());
		
		//Mapa
		grid = stateObs.getObservationGrid();
		
		//Actualización de la punta del avion
		ACTIONS lastAction = stateObs.getAvatarLastAction();
		updateTip(lastAction);
				
		//Actualización del array
		int[] stateValues = new int[7];

		for(int i = 0;i < stateValues.length ; i++) {
			stateValues[i] = 0;
		}
				
		//Percieve speed
		if(speedPlane >= SPEEDLIMIT) {
			stateValues[POSHIGHSPEED] = 1;
		} else {
			stateValues[POSHIGHSPEED] = 0;
		}
		
		//Percieve lateral speed
		stateValues[POSHIGHLATERALSPEED] = getLateralSpeed(stateObs.getAvatarSpeed());	
		
		//Portal WEST, EAST or DOWN
		int[] portalDirection = new int[2];
		
		portalDirection = getPortalDirection();
		
		stateValues[POSPORTALWEST] = portalDirection[0];
		stateValues[POSPORTALEAST] = portalDirection[1];
		
		//Percieve compassTip		
		stateValues[POSPLANETIP] = planeTipDirection();	
		
		//Percieve orientation
		stateValues[POSORIENTATION] = getAvatarOrientation();

		//Percieve danger
		stateValues[POSDANGER] = inDanger(stateValues[POSORIENTATION]);
						
		ArrayList<Integer> arrayStateValues = new ArrayList<>();
		
		for(int i = 0; i < stateValues.length; i++) {
			arrayStateValues.add(stateValues[i]);
		}
			
		super.update(arrayStateValues);
	}
	
	/**
	 * Update the private attribute tip.
	 * @param lastAction
	 */
	private void updateTip(ACTIONS lastAction) {
		if(lastAction == ACTIONS.ACTION_LEFT) {
			this.tip += 0.2f;
		}
		
		else if(lastAction == ACTIONS.ACTION_RIGHT) {
			this.tip -= 0.2f;
		}
		
		if(this.tip >= 6.28) {
			this.tip -= 6.28;
		} 
		else if (this.tip <= 0) {
			this.tip += 6.28;
		}
	}
	
	/**
	 * Check if the lateral speed is high.
	 * @return 0 if it is less than lateralSpeedLimit.
	 */
	private int getLateralSpeed(double speed) {
		double x = Math.abs(orientation.x);
		double y = Math.abs(orientation.y);
		
		double angle = Math.atan(y/x);
				
		double lateralSpeed = speed * Math.cos(angle);
		
		if(lateralSpeed < 0.00000001) {
			lateralSpeed = 0;
		}
				
		if (lateralSpeed >= LATERALSPEEDLIMIT) {
			return 1;
		} else {
			return 0;
		}
		
	}
	
	/**
	 * Calculates the direction of the tip of the agent.
	 * 
	 * @param tip radians where it points.
	 * @return 0 if tip points to NORTH.
	 */
	private int planeTipDirection() {
		int direction = 1;
		
		if(isPortalWest()) {
			if(tip >= WESTPOINTINIT && tip <= WESTPOINTFINISH ) {
				direction = 0;
			} else {
				direction = 1;
			}	
		} 
		else if (isPortalEast()){
			if(tip >= EASTPOINTINIT && tip <= EASTPOINTFINISH ) {
				direction = 0;
			} else {
				direction = 1;
			}
		}
		else {
			if(tip >= NORTHPOINTINIT && tip <= NORTHPOINTFINISH ) {
				direction = 0;
			} else {
				direction = 1;
			}			
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
	private int inDanger(int orient) {
		int width = (int) world.width;
		int height = (int) world.height;
		
		double x = orientation.x;
		double y = orientation.y;

		int posX = (int) agentPos.x;
		int posY = (int) agentPos.y;
		
		int danger = 0;
		
		int blockPositions = 0;
		int observationY = 0;
		int observationX = 0;
		
		int iteradorY;
		int iteradorX;
		
		int xFinal;
		int yFinal;
		
		if(orient == State.NORTH) {
			blockPositions = 0;

			if((posY - FRONTBACKDISTANCE) <= 0) {
				blockPositions++;
			} else {
				observationY = posY - FRONTBACKDISTANCE;
			}
			
			iteradorY = posY - 1;
			iteradorX = posX - 1;
			
			xFinal = posX + 1;
			
			while(iteradorY >= observationY  && blockPositions == 0) {
				while(iteradorX <= xFinal && blockPositions == 0) {
					if(iteradorX >= 0 && iteradorX <= width - 1) { 
						if(!grid[iteradorX][iteradorY].isEmpty()) {
							if(!isThisCategory(grid[iteradorX][iteradorY].get(0), TYPEAVATAR)) 
								blockPositions++;
						}					
					} else {
						blockPositions++;
					}
					iteradorX++;
				}
				iteradorX = posX - 1;
				iteradorY--;
			}

			if(blockPositions > 0) danger = 1;
		}
		
		else if(orient == State.SOUTH) {
			blockPositions = 0;

			if((posY + FRONTBACKDISTANCE) >= height - 1) {
				blockPositions++;
			} else {
				observationY = posY + FRONTBACKDISTANCE;
			}
			
			iteradorY = posY + 1;
			iteradorX = posX;
			
			xFinal = posX;
			
			while(iteradorY <= observationY  && blockPositions == 0) {
				while(iteradorX <= xFinal && blockPositions == 0) {
					if(iteradorX >= 0 && iteradorX <= width - 1) { 
						if(!grid[iteradorX][iteradorY].isEmpty()) {
							if(!isThisCategory(grid[iteradorX][iteradorY].get(0), TYPEAVATAR)) { 
								blockPositions++;
							}
						}					
					} else {
						blockPositions++;
					}
					iteradorX++;
				}
				iteradorX = posX;
				iteradorY++;
			}

			if(blockPositions > 0) danger = 1;
		}
		
		else if(orient == State.WEST) {
			blockPositions = 0;

			if(y > 0 && x > -XORIENTATION) {
				if (posX - 1 <= 0 || (!grid[posX - 1][posY].isEmpty()
						&& !isThisCategory(grid[posX - 1][posY].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posX - 1 <= 0 || posY + 1 >= world.height - 1 || (!grid[posX - 1][posY + 1].isEmpty()
						&& !isThisCategory(grid[posX - 1][posY + 1].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posY + 1 >= world.height - 1 || (!grid[posX][posY + 1].isEmpty()
						&& !isThisCategory(grid[posX][posY + 1].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posX - 2 <= 0 || posY + 2 >= world.height - 1 || (!grid[posX - 2][posY + 2].isEmpty()
						&& !isThisCategory(grid[posX - 2][posY + 2].get(0), TYPEAVATAR))) {
					blockPositions++;
				}
				
			}
			
			else if (y < 0 && x > -XORIENTATION) {
				if (posX - 1 <= 0 || (!grid[posX - 1][posY].isEmpty()
						&& !isThisCategory(grid[posX - 1][posY].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posX - 1 <= 0 || posY - 1 <= 0 || (!grid[posX - 1][posY - 1].isEmpty()
						&& !isThisCategory(grid[posX - 1][posY - 1].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posY - 1 <= 0 || (!grid[posX][posY - 1].isEmpty()
						&& !isThisCategory(grid[posX][posY - 1].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posX - 2 <= 0 || posY - 2 <= 0 || (!grid[posX - 2][posY - 2].isEmpty()
						&& !isThisCategory(grid[posX - 2][posY - 2].get(0), TYPEAVATAR))) {
					blockPositions++;
				}	
				
			} else {
				if(posX - LEFTRIGHTDISTANCE <= 0) {
					blockPositions++;
				} else {
					observationX = posX - LEFTRIGHTDISTANCE;
				}
				
				iteradorX = posX - 1;

				iteradorY = posY - 1;
				yFinal = posY + 1;
				
				
				while(iteradorY <= yFinal  && blockPositions == 0) {
					while(iteradorX >= observationX && blockPositions == 0) {
						if(iteradorY >= 0 && iteradorY <= world.height - 1) { 
							if(!grid[iteradorX][iteradorY].isEmpty()) {
								if(!isThisCategory(grid[iteradorX][iteradorY].get(0), TYPEAVATAR)) 
									blockPositions++;
							}					
						} else {
							blockPositions++;
						}
						iteradorX--;
					}
					iteradorX = posX - 1;
					iteradorY++;
				}
			}
			
			if(blockPositions > 0) danger = 1;	
			
		}
		
		else if(orient == State.EAST) {
			blockPositions = 0;

			if(y > 0 && x < XORIENTATION) {
				if (posX + 1 >= world.width - 1 || (!grid[posX + 1][posY].isEmpty()
						&& !isThisCategory(grid[posX + 1][posY].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posX + 1 >= world.width - 1 || posY + 1 >= world.height - 1 || (!grid[posX + 1][posY + 1].isEmpty()
						&& !isThisCategory(grid[posX + 1][posY + 1].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posY + 1 >= world.height - 1 || (!grid[posX][posY + 1].isEmpty()
						&& !isThisCategory(grid[posX][posY + 1].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posX + 2 >= world.width - 1 || posY + 2 >= world.height - 1 || (!grid[posX + 2][posY + 2].isEmpty()
						&& !isThisCategory(grid[posX + 2][posY + 2].get(0), TYPEAVATAR))) {
					blockPositions++;
				}
				
			}
			
			else if (y < 0 && x < XORIENTATION) {
				if (posX + 1 >= world.width - 1 || (!grid[posX + 1][posY].isEmpty()
						&& !isThisCategory(grid[posX + 1][posY].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posX + 1 >= world.width - 1 || posY - 1 <= 0 || (!grid[posX + 1][posY - 1].isEmpty()
						&& !isThisCategory(grid[posX + 1][posY - 1].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posY - 1 <= 0 || (!grid[posX][posY - 1].isEmpty()
						&& !isThisCategory(grid[posX][posY - 1].get(0), TYPEAVATAR))) {
					blockPositions++;
				}

				if (posX + 2 >= world.width - 1 || posY - 2 <= 0 || (!grid[posX + 2][posY - 2].isEmpty()
						&& !isThisCategory(grid[posX + 2][posY - 2].get(0), TYPEAVATAR))) {
					blockPositions++;
				}
				
			} else {
				if(posX + LEFTRIGHTDISTANCE >= world.width - 1) {
					blockPositions++;
				} else {
					observationX = posX + LEFTRIGHTDISTANCE;
				}
				
				iteradorX = posX + 1;

				iteradorY = posY - 1;
				yFinal = posY + 1;
				
				
				while(iteradorY <= yFinal  && blockPositions == 0) {
					while(iteradorX <= observationX && blockPositions == 0) {
						if(iteradorY >= 0 && iteradorY <= world.height - 1) { 
							if(!grid[iteradorX][iteradorY].isEmpty()) {
								if(!isThisCategory(grid[iteradorX][iteradorY].get(0), TYPEAVATAR)) 
									blockPositions++;
							}					
						} else {
							blockPositions++;
						}
						iteradorX++;
					}
					iteradorX = posX + 1;
					iteradorY++;
				}
				
			}
							
			if(blockPositions > 0) danger = 1;

		}
		
		//Check if portal is ... or it is dangerous		
		if(isPortalDown() && orient == State.SOUTH && (agentPos.y - portalPos.get(0).y < FRONTBACKDISTANCE)  && !super.isHighSpeed()) {
			danger = 0;
		}
		
		return danger;
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
	 * Verify in which direction the portal is.
	 * 
	 * @return integer vector.
	 */
	private int[] getPortalDirection() {
		int[] portalDirection = new int[2];
		
		portalDirection[0] = 0;
		portalDirection[1] = 0;
		
		boolean underAvatar = false;
				
		for(int i = 0; i < portalPos.size(); i++) {
			if(portalPos.get(i).x == agentPos.x) {
				underAvatar = true;
			}
		}
		
		if(!underAvatar) {
			
			if(portalPos.get(0).x > agentPos.x) {
				portalDirection[0] = 0;
				portalDirection[1] = 1;
			}
			else if (portalPos.get(0).x < agentPos.x){
				portalDirection[0] = 1;
				portalDirection[1] = 0;
			}
		}
		
		return portalDirection;				
	}
	
	/**
	 * Return if the orientation points is a up or down.
	 * 
	 * @param orientation is avatar.
	 * @return 0 if orientation is down.
	 */
	private int getAvatarOrientation() {
		int aux = 0;
		double x = orientation.x;
		double y = orientation.y;		
		
		if( y >= 0 && (x >= -SOUTHORIENTATION && x <= SOUTHORIENTATION)) {
			aux = State.SOUTH;
		}
		else if(y < 0 && (x >= -NORTHORIENTATION && x <= NORTHORIENTATION)) {
			aux = State.NORTH;
		}
		else if((y > 0 && x < -SOUTHORIENTATION) || ((y < 0 && x < -NORTHORIENTATION))) {
			aux = State.WEST;
		}
		else if ((y > 0 && x > SOUTHORIENTATION) || ((y < 0 && x > NORTHORIENTATION)) ){
			aux = State.EAST;			
		}
		
		return aux;		
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
				if (a.itype == 2) {
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
		
		x = (int) Math.round(x / portalPos.size());
		
		return (float) (agentPos.dist(x,portalPos.get(0).y));
		
	}
	
	/**
	 * Return the speed of the plane.
	 * @return the speed of the plane.
	 */
	public float getSpeed(){
		return this.speedPlane;
	}
	
	public boolean isHighLateralSpeed() {
		return super.getHighLateralSpeed();
	}
	
	public Vector2d agentOrientation() {
		return orientation;
	}
	
	public int avatarOrientation() {
		return super.getOrientation();
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
	 * @return portal position in cell coordinates.
	 */
	public ArrayList<Vector2d> getPortalPos(){
		return portalPos;
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
				"\nDimensiones del mundo = [ " + world.width + " , " + world.height + " ]\n";
		
		return str;
	}
				
}
