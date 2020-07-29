package ricardomanuelruizalu.mytools;

import java.util.ArrayList;

/**
 * Defines a Q-Table state.
 * 
 * @author Ricardo Manuel Ruiz Diaz
 */
public class State {
		
	public static final int POSHIGHSPEED = 0;
	public static final int POSPORTALEAST = 1;
	public static final int POSPORTALWEST = 2;
	public static final int POSORIENTATION = 3;
	public static final int POSDISPLACEMENT = 4;

	public static final int DANGERLEFT = 0;
	public static final int LEFT = 1;
	public static final int CENTER = 3;
	public static final int RIGHT = 2;
	public static final int	DANGERRIGHT = 4;
		
	/**
	 * Private attributes
	 */
	private boolean highSpeed;
	private boolean portalWest;
	private boolean portalEast;
	private int orientation;
	private int displacement;
	
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
		this.portalWest = obj.portalWest;
		this.portalEast = obj.portalEast;
		this.orientation = obj.orientation;
		this.displacement = obj.displacement;
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
	 * @param array Updates private attributes values.
	 */
	protected void update(ArrayList<Integer> array) {	
		highSpeed = (array.get(POSHIGHSPEED) == 0 ? false : true);
		portalWest = (array.get(POSPORTALWEST) == 0 ? false : true);
		portalEast = (array.get(POSPORTALEAST) == 0 ? false : true);
		orientation = array.get(POSORIENTATION);
		displacement = array.get(POSDISPLACEMENT);
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
	 * Return displacement of the avatar
	 * @return displacement of the avatar
	 */
	public int getDisplacement() {
		return this.displacement;
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
				 this.orientation == aux.orientation &&
				 this.portalWest == aux.portalWest &&
				 this.portalEast == aux.portalEast &&
				 this.displacement == aux.displacement);		
	}

	/**
	 * Returns a String with the information of the object.
	 */
	@Override
	public String toString() {
		String str = "";
		
		str =  	"highSpeed = " + Boolean.toString(highSpeed) + "\n" + 
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
			case DANGERLEFT:
				str += "DANGER LEFT";
				break;
			case LEFT:
				str += "LEFT";
				break;
			case CENTER:
				str += "CENTER";
				break;
			case RIGHT:
				str += "RIGHT";
				break;
			case DANGERRIGHT:
				str += "DANGER RIGHT";
				break;
		}
		
		str += "\ndisplacement = ";
		
		switch (displacement) {
			case DANGERLEFT:
				str += "DANGER LEFT";
				break;
			case LEFT:
				str += "LEFT";
				break;
			case CENTER:
				str += "CENTER";
				break;
			case RIGHT:
				str += "RIGHT";
				break;
			case DANGERRIGHT:
				str += "DANGER RIGHT";
				break;
		}
		
		return str;
	}

}
