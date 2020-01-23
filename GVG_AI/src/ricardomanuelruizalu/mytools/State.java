package ricardomanuelruizalu.mytools;

import java.util.ArrayList;

/**
 * Defines a Q-Table state.
 * 
 * @author Ricardo Manuel Ruiz Diaz
 */
public class State {
	
	/**
	 * Private attributes
	 */
	private boolean frontDanger;
	private boolean backDanger;
	private boolean leftDanger;
	private boolean rightDanger;
	private boolean highSpeed;
	private boolean planeTip;
	private boolean orientation;
	private boolean portalWest;
	private boolean portalEast;
	
	public static final int POSFRONTBLOCK = 0;
	public static final int POSBACKBLOCK = 1;
	public static final int POSLEFTBLOCK = 2;
	public static final int POSRIGHTBLOCK = 3;
	public static final int POSHIGHSPEED = 4;
	public static final int POSPLANETIP = 5;
	public static final int POSORIENTATION = 6;
	public static final int POSPORTALWEST = 7;
	public static final int POSPORTALEAST = 8;

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
		this.frontDanger = obj.frontDanger;
		this.backDanger = obj.backDanger;
		this.leftDanger = obj.leftDanger;
		this.rightDanger = obj.rightDanger;
		
		this.highSpeed = obj.highSpeed;

		this.planeTip = obj.planeTip;
		
		this.orientation = obj.orientation;
		
		this.portalWest = obj.portalWest;
		this.portalEast = obj.portalEast;
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
		frontDanger = (array.get(POSFRONTBLOCK) == 0 ? false : true);
		backDanger = (array.get(POSBACKBLOCK) == 0 ? false : true);	
		leftDanger = (array.get(POSLEFTBLOCK) == 0 ? false : true);
		rightDanger = (array.get(POSRIGHTBLOCK) == 0 ? false : true);	
		highSpeed = (array.get(POSHIGHSPEED) == 0 ? false : true);
		planeTip = (array.get(POSPLANETIP) == 0 ? true : false); 
		orientation = (array.get(POSORIENTATION) == 0 ? false : true); 	//False Down
																		//True Up
		portalWest = (array.get(POSPORTALWEST) == 0 ? false : true);
		portalEast = (array.get(POSPORTALEAST) == 0 ? false : true);

	}
	
	/**
	 * Return if the avatar speed is high.
	 * @return true if the avatar speed is high.
	 */
	public boolean isHighSpeed() {
		return highSpeed;
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
		return ( this.frontDanger == aux.frontDanger &&
				 this.backDanger == aux.backDanger &&
				 this.leftDanger == aux.leftDanger &&
				 this.rightDanger == aux.rightDanger &&
				 this.highSpeed == aux.highSpeed && 
				 this.planeTip == aux.planeTip &&
				 this.orientation == aux.orientation &&
				 this.portalWest == aux.portalWest &&
				 this.portalEast == aux.portalEast);
		
	}

	/**
	 * Returns a String with the information of the object.
	 */
	@Override
	public String toString() {
		String str = "";
		
		str =  	"frontDanger = " + Boolean.toString(frontDanger) + "\n" + 
				"backDanger = " + Boolean.toString(backDanger) + "\n" + 
				"leftDanger = " + Boolean.toString(leftDanger) + "\n" + 
				"rigthDanger = " + Boolean.toString(rightDanger) + "\n" + 
				"highSpeed = " + Boolean.toString(highSpeed) + "\n" + 
				"planeTip = " + Boolean.toString(planeTip) + "\n" + 
				"orientation = " + (orientation == false ? "Down" : "Up") + "\n" + 
				"portalWest = " + Boolean.toString(portalWest) + "\n" + 
				"portalEast = " + Boolean.toString(portalEast) + "\n"; 
		
		return str;
	}
	
}
