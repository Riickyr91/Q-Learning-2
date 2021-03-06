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
	private ArrayList<Vector2d> portalPos; //Posiciones del portal
	private Dimension world;
	private ArrayList<Observation>[][] grid;
	
	private float speed; //Velocidad del avi�n
	private float orientation; //Hacia donde apunta
	private float displacement;
	
	private boolean portalWest;
	private boolean portalEast;
		
	public static final int TYPEPORTAL = 2;
	public static final int TYPEAVATAR = 1;
	
	public static final float SPEEDLIMIT = 3.00f; // Para ganar tiene que ir a menos de 9.35
	
	public static final float ORIENTATIONLEFTPOINT = 1.83f;
	public static final float ORIENTATIONLEFTCENTERPOINT = 1.65f;
	public static final float ORIENTATIONCENTERRIGHTPOINT = 1.48f;  	
	public static final float ORIENTATIONRIGHTPOINT = 1.30f;
	
	public static final float W = 3.0F;  	
	public static final float WS = 3.4f;
	public static final float SW = 3.95f;
	public static final float SOUTHW = 4.5f;
	public static final float SOUTHE = 4.9f;
	public static final float SE = 5.45f;
	public static final float ES = 6f;
	public static final float E = 0.2f;

		
	/**
	 * Constructor.
	 * @param stateObs game observations.
	 */
 	public AgentState(StateObservation stateObs) {
 		this.agentPos = calculateCell(stateObs.getAvatarPosition(), stateObs.getBlockSize());
		this.world = new Dimension(stateObs.getWorldDimension());	
		this.world.height = world.height / stateObs.getBlockSize();
		this.world.width = world.width / stateObs.getBlockSize();
		this.grid = stateObs.getObservationGrid();
		
 		this.portalPos = getPortalPos(stateObs);
		
		this.orientation = 0;	
		this.speed = (float) stateObs.getAvatarSpeed();
		
		this.displacement = vector2Float(stateObs.getAvatarOrientation());		
				
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
		this.portalPos = new ArrayList<Vector2d>(obj.portalPos);
		this.grid = obj.grid;
		this.world = obj.world;
		this.orientation = obj.orientation;
		this.speed = obj.speed;	
		this.displacement = obj.displacement;
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
 		
		//Velocidad del avi�n
		this.speed = (float) stateObs.getAvatarSpeed();
		
		//Desplazamiento
		this.displacement = vector2Float(stateObs.getAvatarOrientation());
		
		//Mapa
		grid = stateObs.getObservationGrid();
		
		//Actualizaci�n de la punta del avion
		ACTIONS lastAction = stateObs.getAvatarLastAction();
		updateOrientation(lastAction);
				
		//Actualizaci�n del array
		int[] stateValues = new int[5];

		for(int i = 0;i < stateValues.length ; i++) {
			stateValues[i] = 0;
		}
				
		//Percieve speed
		if(speed >= SPEEDLIMIT) {
			stateValues[POSHIGHSPEED] = 1;
		} else {
			stateValues[POSHIGHSPEED] = 0;
		}
			
		//Portal WEST, EAST or DOWN
		int[] portalDirection = new int[2];
		
		portalDirection = getPortalDirection();
		
		stateValues[POSPORTALEAST] = portalDirection[0];
		portalEast = ( portalDirection[0] == 0 ? false : true);
		stateValues[POSPORTALWEST] = portalDirection[1];
		portalWest = ( portalDirection[1] == 0 ? false : true);
		
		//Percieve compassTip		
		stateValues[POSORIENTATION] = planeOrientation();	
		
		//Percieve orientation
		stateValues[POSDISPLACEMENT] = getAvatarDisplacement();
						
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
	private void updateOrientation(ACTIONS lastAction) {
		if(lastAction == ACTIONS.ACTION_LEFT) {
			this.orientation += 0.2f;
		}
		
		else if(lastAction == ACTIONS.ACTION_RIGHT) {
			this.orientation -= 0.2f;
		}
		
		if(this.orientation >= 6.28) {
			this.orientation -= 6.28;
		} 
		else if (this.orientation <= 0) {
			this.orientation += 6.28;
		}
	}
	
	/**
	 * Calculates the direction of the tip of the agent.
	 * 
	 * @param tip radians where it points.
	 * @return 0 if tip points to NORTH.
	 */
	private int planeOrientation() {
		int direction = State.DANGERRIGHT;
		
		if(this.orientation > ORIENTATIONLEFTPOINT && this.orientation <= 4.71f )
			direction = State.DANGERLEFT;
		else if(this.orientation <= ORIENTATIONLEFTPOINT && this.orientation > ORIENTATIONLEFTCENTERPOINT)
			direction = State.LEFT;
		else if(this.orientation <= ORIENTATIONLEFTCENTERPOINT && this.orientation > ORIENTATIONCENTERRIGHTPOINT)
			direction = State.CENTER;
		else if(this.orientation <= ORIENTATIONCENTERRIGHTPOINT && this.orientation > ORIENTATIONRIGHTPOINT)
			direction = State.RIGHT;
		else if(this.orientation <= ORIENTATIONRIGHTPOINT || this.orientation > 4.71f)
			direction = State.DANGERRIGHT;
				
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
				portalDirection[0] = 1;
				portalDirection[1] = 0;
			}
			else if (portalPos.get(0).x < agentPos.x){
				portalDirection[0] = 0;
				portalDirection[1] = 1;
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
	public int getAvatarDisplacement() {	
		if(this.displacement < W && this.displacement > E )
			return State.NORTH;
		else if(this.displacement <= E)
			return State.EAST;
		else if(this.displacement <= WS)
			return State.WEST;
		else if(this.displacement < SW)
			return State.WESTSOUTH;
		else if(this.displacement < SOUTHW)
			return State.SOUTHWEST;
		else if(this.displacement <= SOUTHE)
			return State.SOUTH;
		else if(this.displacement < SE)
			return State.SOUTHEAST;
		else if(this.displacement <ES)
			return State.EASTSOUTH;
		else
			return State.EAST;	
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
	 * Return the angle formed by vector.
	 * @param vector
	 * @return
	 */
	float vector2Float(Vector2d vector) {
		float numero = 0;
		
		float ejex = (float) vector.x;
		float ejey = (float) vector.y;
		
		numero = (float) Math.atan2(-ejey, ejex);
		
		if(numero < 0) {
			numero = (float) (numero + 2*Math.PI);
		}
				
		return numero;
	}
			
	/**
	 * @return portal position in cell coordinates.
	 */
	public ArrayList<Vector2d> getPortalPos(){
		return portalPos;
	}
		
	/**
	 * @return if the portal is west.
	 */
	public boolean isPortalWest() {
		return portalWest;
	}

	/**
	 * @return if the portal is east.
	 */
	public boolean isPortalEast() {
		return portalEast;
	}

	/**
	 * @return the speed of the avatar.
	 */
	public float getSpeed() {
		return speed;
	}
	
	/**
	 * @return the position of avatar.
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
				"\nDisplacement = " + this.displacement + 
				"\nHacia donde apunta = " + this.orientation +
				"\nSpeed = " + this.speed + 
				"\nPosicion del portal = " + portalPos + 
				"\nDimensiones del mundo = [ " + world.width + " , " + world.height + " ]\n";
		
		return str;
	}
				
}
