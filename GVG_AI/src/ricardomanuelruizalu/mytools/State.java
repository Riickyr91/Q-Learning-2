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
	private boolean dangerOrientation;
	
	public static final int POSFRONTBLOCK = 0;
	public static final int POSBACKBLOCK = 1;
	public static final int POSLEFTBLOCK = 2;
	public static final int POSRIGHTBLOCK = 3;
	public static final int POSHIGHSPEED = 4;
	public static final int POSPLANETIP = 5;
	public static final int POSDANGERORIENTATION = 6;

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
		
		this.dangerOrientation = obj.dangerOrientation;
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
		
		dangerOrientation = (array.get(POSDANGERORIENTATION) == 0 ? false : true);

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
	 * Returns if the orientation points to a block
	 * @return true if the orientation points to a block.
	 */
	public boolean isDangerOrientation() {
		return dangerOrientation;
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
				 this.dangerOrientation == aux.dangerOrientation);
		
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
				"dangerOrientation = " + Boolean.toString(dangerOrientation) + "\n"; 
		
		return str;
	}

}
