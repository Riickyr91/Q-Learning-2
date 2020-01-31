package ricardomanuelruizalu.mytools;

import java.util.ArrayList;

/**
 * Defines a Q-Table state.
 * 
 * @author Ricardo Manuel Ruiz Diaz
 */
public class State {
		
	public static final int POSHIGHSPEED = 0;
	public static final int POSHIGHLATERALSPEED = 1;
	public static final int POSDANGER = 2;
	public static final int POSPLANETIP = 3;
	public static final int POSPORTALWEST = 4;
	public static final int POSPORTALEAST = 5;
	public static final int POSORIENTATION = 6;

	public static final int NORTH = 0;
	public static final int SOUTH = 1; 
	public static final int EAST = 2;
	public static final int WEST = 3;
	
	/**
	 * Private attributes
	 */
	private boolean highSpeed;
	private boolean highLateralSpeed;
	protected boolean danger;
	private boolean planeTip;
	private boolean portalWest;
	private boolean portalEast;
	private int orientation;
	
	/**
	 * Default constructor.
	 */
	public State() {}
	
	/**
	 * Copy constructor.
	 * 
	 * @param obj object to be copied.
	 */
	public State(State obj) {	
		this.highSpeed = obj.highSpeed;
		this.highLateralSpeed = obj.highLateralSpeed;
		this.danger = obj.danger;
		this.planeTip = obj.planeTip;
				
		this.portalWest = obj.portalWest;
		this.portalEast = obj.portalEast;
		
		this.orientation = obj.orientation;		
	}
	
	/**
	 * Constructor. 
	 * 
	 * @param array Private attributes values expressed with integers.
	 */
	public State(ArrayList<Integer> array) {
		update(array);		
	}
	
	/**
	 * Updates private attributes values.
	 * 
	 * @param array Updates private attributes values.
	 */
	protected void update(ArrayList<Integer> array) {	
		highSpeed = (array.get(POSHIGHSPEED) == 0 ? false : true);
		highLateralSpeed = (array.get(POSHIGHLATERALSPEED) == 0 ? false : true);
		danger = (array.get(POSDANGER) == 0 ? false : true);
		planeTip = (array.get(POSPLANETIP) == 0 ? true : false); 

		portalWest = (array.get(POSPORTALWEST) == 0 ? false : true);
		portalEast = (array.get(POSPORTALEAST) == 0 ? false : true);
		
		orientation = array.get(POSORIENTATION);
	}
	
	/**
	 * Return if the avatar speed is high.
	 * @return true if the avatar speed is high.
	 */
	public boolean isHighSpeed() {
		return highSpeed;
	}

	/**
	 * Return orientation of the avatar.
	 * @return orientation of the avatar.
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Return if the lateral speed high.
	 * @return true if is higher speed.
	 */
	public boolean getHighLateralSpeed() {
		return highLateralSpeed;
	}
	
	/**
	 * Return if the avatar tip is correct.
	 * @return true if the avatar tip is correct ( NORTH ).
	 */
	public boolean isPlaneTip() {
		return planeTip;
	}
	
	/**
	 * Return if portal is to West.
	 * @return true if portal is to West.
	 */
	public boolean isPortalWest() {
		return portalWest;
	}

	/**
	 * Return if portal is to East.
	 * @return true if portal is to East.
	 */
	public boolean isPortalEast() {
		return portalEast;
	}
	
	/**
	 * Return if portal is under the avatar.
	 * @return true if portal is under the avatar.
	 */
	protected boolean isPortalDown() {
		return !portalWest && !portalEast;
	}
	
	/**
	 * Returns true if the attributes are exactly the same.
	 */
	@Override
	public boolean equals(Object obj) {
		State aux = (State) obj;
		return ( this.highSpeed == aux.highSpeed && 
				 this.highLateralSpeed == aux.highLateralSpeed &&
				 this.planeTip == aux.planeTip &&
				 this.orientation == aux.orientation &&
				 this.portalWest == aux.portalWest &&
				 this.portalEast == aux.portalEast &&
				 this.orientation == aux.orientation);		
	}

	/**
	 * Returns a String with the information of the object.
	 */
	@Override
	public String toString() {
		String str = "";
		
		str =  	"highSpeed = " + Boolean.toString(highSpeed) + "\n" + 
				"highLateralSpeed = " + Boolean.toString(highLateralSpeed) + "\n" + 
				"danger = " + Boolean.toString(danger) + "\n" + 
				"planeTip = " + Boolean.toString(planeTip) + "\n" + 
				"portalWest = " + Boolean.toString(portalWest) + "\n" + 
				"portalEast = " + Boolean.toString(portalEast) + "\n" + 
				"portalDown = ";

		if (!portalWest && !portalEast) {
			str += "true" + "\n";
		} else {
			str += "false" + "\n";
		}

		str += "orientation = ";
		
		switch (orientation) {
		case NORTH:
			str += "North";
			break;
		case SOUTH:
			str += "South";
			break;
		case EAST:
			str += "East";
			break;
		case WEST:
			str += "West";
			break;
		}
		
		return str;
	}

}
