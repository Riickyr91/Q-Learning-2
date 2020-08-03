package ricardomanuelruizalu.mytools;

import java.util.ArrayList;

/**
 * Generates all possible and logical agent state.
 * 
 * @author Ricardo Manuel Ruiz Diaz.
 */
public class StateGenerator {
	
	/**
	 * Generates all possible and logical agent state.
	 * @return An array of states.
	 */
	public static ArrayList<State> generate() {
		int[] values = new int[2];
		values[0] = 0;
		values[1] = 1;
		
		// Generation and filtering states without compass
		ArrayList<ArrayList<Integer>> combStates = combnk(3, values);
		
		int[] orientationValues = {State.DANGERLEFT, State.LEFT, State.CENTER, State.RIGHT, State.DANGERRIGHT};
		combStates = addInteger2Combination(combStates, orientationValues);
		
		int[] displacementValues = {State.NORTH, State.SOUTH, State.EAST, State.SOUTHEAST, State.EASTSOUTH, State.WEST, State.SOUTHWEST, State.WESTSOUTH};
		combStates = addInteger2Combination(combStates, displacementValues);

		filterStates(combStates);
				
		ArrayList<State> output = integer2States(combStates);
					
		return output;			
	}
		
	/**
	 * Add a new value to all combination.
	 * 
	 * @param combStates combinations values.
	 * @param values to be added.
	 * @return combinations with the new values.
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<ArrayList<Integer>> addInteger2Combination(ArrayList<ArrayList<Integer>> combStates, int[] values) {
		ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> aux;
		
		for(int indexValues = 0; indexValues < values.length; indexValues++) {
			for(int indexArray = 0; indexArray < combStates.size(); indexArray++) {
				aux = (ArrayList<Integer>) combStates.get(indexArray).clone();
				aux.add(values[indexValues]);
				output.add(aux);
			}
		}
		
		return output;
	}
	
	/**
	 * Add a new value to all combination and create states for each combination.
	 * 
	 * @param combStates combinations values.
	 * @param values to be added.
	 * @return states.
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<State> integer2States(ArrayList<ArrayList<Integer>> combStates) {
		ArrayList<State> output = new ArrayList<State>();
		ArrayList<Integer> aux;
		
		for(int indexArray = 0; indexArray < combStates.size(); indexArray++) {
			aux = (ArrayList<Integer>) combStates.get(indexArray).clone();
			output.add(new State(aux));
		}
		
		return output;
	}
		
	/**
	 * Generates all possible combinations using the values and length specified.
	 * 
	 * @param length size of combination.
	 * @param values possible values for each positions.
	 * @return an array of arrays of integers.
	 */
	private static ArrayList<ArrayList<Integer>> combnk(int length, int[] values) {
		return combnkRec(length-1, values, new ArrayList<ArrayList<Integer>>());
	}
	
	/**
	 * Recursive call for combnk.
	 * 
	 * @param length size of combination.
	 * @param values possible values for each positions.
	 * @param array memory space to save middle values.
	 * @return an array of arrays of integers.
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<ArrayList<Integer>> combnkRec(int length, int[] values, ArrayList<ArrayList<Integer>> array){
		if(length == 0) {
			for(int indexValues = 0; indexValues < values.length; indexValues++) {
				array.add(new ArrayList<Integer>());
				array.get(indexValues).add(values[indexValues]);
			}
			return array;
		}
		ArrayList<ArrayList<Integer>> aux = new ArrayList<ArrayList<Integer>>();
		aux = combnkRec(length - 1, values, array);
				
		ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>();
		for(int indexValue = 0; indexValue < values.length; indexValue++) {
			for(int indexArray = 0; indexArray < aux.size(); indexArray++) {
				ArrayList<Integer> subArray = (ArrayList<Integer>) aux.get(indexArray).clone();	
				subArray.add(values[indexValue]);
				output.add(subArray);
			}
		}
		return output;				
	}
		
	/**
	 * Removes illogical states.
	 * 
	 * @param combStates an array of arrays of integers with all possible combinations.
	 */
	private static void filterStates(ArrayList<ArrayList<Integer>> combStates){
		for(int i = 0; i < combStates.size(); i++ ) {
			if(!isValid(combStates.get(i))) {
				combStates.remove(i);
				i--;
			}
		}
	}

	/**
	 * Applies restrictions to filter a state.
	 * 
	 * @param comb array of integers with one combination of values.
	 * @return true if overcomes all restrictions.
	 */
	private static boolean isValid(ArrayList<Integer> comb) {	
		boolean portalWest = (comb.get(State.POSPORTALWEST) == 0 ? false : true);
		boolean portalEast = (comb.get(State.POSPORTALEAST) == 0 ? false : true);
			
		if(portalWest && portalEast) return false;		
		
		return true;	
	}
		
	public static void main(String[] args) {
		System.out.println("Longitud = " + StateGenerator.generate().size());
	}
	
}
