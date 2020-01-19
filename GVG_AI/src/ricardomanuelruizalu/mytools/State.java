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
	private boolean highSpeed;
	
	private boolean frontDanger;
	private boolean backDanger;
	private boolean leftDanger;
	private boolean rightDanger;
	
	private int compassTip;
	private int compassOrientation;
	
	public static final int POSHIGHSPEED = 0;
	public static final int POSFRONTDANGER = 1;
	public static final int POSBACKDANGER = 2;
	public static final int POSLEFTDANGER = 3;
	public static final int POSRIGHTDANGER = 4;
	public static final int POSCOMPASSTIP = 5;
	public static final int POSCOMPASSORIENTATION = 6;
	
	public static final int NORTH = 0;
	public static final int SOUTH = 1; 
	public static final int EAST = 2;
	public static final int WEST = 3;
		
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
		
		this.frontDanger = obj.frontDanger;
		this.backDanger = obj.backDanger;
		this.leftDanger = obj.leftDanger;
		this.rightDanger = obj.rightDanger;
		
		this.compassTip = obj.compassTip;
		this.compassOrientation = obj.compassOrientation;
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
		
		frontDanger = (array.get(POSFRONTDANGER) == 0 ? false : true);
		backDanger = (array.get(POSBACKDANGER) == 0 ? false : true);	
		leftDanger = (array.get(POSLEFTDANGER) == 0 ? false : true);
		rightDanger = (array.get(POSRIGHTDANGER) == 0 ? false : true);	
		
		compassTip = array.get(POSCOMPASSTIP);
		compassOrientation = array.get(POSCOMPASSORIENTATION);

	}
	
	/**
	 * Returns true if the attributes are exactly the same.
	 */
	@Override
	public boolean equals(Object obj) {
		State aux = (State) obj;
		return ( this.highSpeed == aux.highSpeed && 
				 this.frontDanger == aux.frontDanger &&
				 this.backDanger == aux.backDanger &&
				 this.leftDanger == aux.leftDanger &&
				 this.rightDanger == aux.rightDanger &&
				 this.compassTip == aux.compassTip &&
				 this.compassOrientation == aux.compassOrientation);
		
	}

	/**
	 * Returns a String with the information of the object.
	 */
	@Override
	public String toString() {
		String str = "";
		
		str =  	"highSpeed = " + Boolean.toString(highSpeed) + "\n" + 
				"frontDanger = " + Boolean.toString(frontDanger) + "\n" + 
				"backDanger = " + Boolean.toString(backDanger) + "\n" + 
				"leftDanger = " + Boolean.toString(leftDanger) + "\n" + 
				"rigthDanger = " + Boolean.toString(rightDanger) + "\n" + 
				"compassTip = ";
		
		switch (compassTip) {
			case NORTH: str += "North"; break;
			case SOUTH: str += "South"; break;
			case EAST: str += "East"; break;
			case WEST: str += "West"; break;
		}
		
		str += "\ncompassOrientation = ";
				
		switch (compassOrientation) {
			case NORTH: str += "North"; break;
			case SOUTH: str += "South"; break;
			case EAST: str += "East"; break;
			case WEST: str += "West"; break;
		}
		
		return str;
	}
}
