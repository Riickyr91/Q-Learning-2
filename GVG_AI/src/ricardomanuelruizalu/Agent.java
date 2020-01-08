package ricardomanuelruizalu;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {
    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;
    /**
     * List of available actions for the agent
     */
    protected ArrayList<Types.ACTIONS> actions;


    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    //
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
        actions = so.getAvailableActions();
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    //Recibe la percepción, piensa y devuelve la acción.
    //ElapsedCpuTimer cuanto queda de tiempo
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		
		int index = randomGenerator.nextInt(actions.size());

		/*
		 * No importa hacia donde apunta, gana de cualquier forma
		 */
		// Posición
//    	Vector2d posicion = stateObs.getAvatarPosition();
//      System.out.print(stateObs.getAvatarPosition());

//    	// Convierto reales a celdas
//		Vector2d cellCoords = new Vector2d();
//		
//		int x = (int) (posicion.x/stateObs.getBlockSize());
//		int y = (int) (posicion.y/stateObs.getBlockSize());
//		
//		cellCoords.set(x, y);
//		System.out.print(cellCoords);

		/*
		 * Orientacion 0 - 1 refiere a que apunta justo hacia abajo
		 */
		// Orientación. Refiere la dirección donde se mueve no donde apunta
//        System.out.println(" - " + stateObs.getAvatarOrientation());

		// Tomar celda a la que orienta
//    	stateObs.getObservationGrid()

		/*
		 * El "portal" es objeto inmovil tipo 2
		 */
		// Objetos inmovibles
		// Los objetos inmoviles lo recogemos en el constructor.
//		for (ArrayList<Observation> v : stateObs.getImmovablePositions()) // es un arraylist de array
//			for (Observation a : v) { // es un array de observation
//				if (a.itype == 2) {
////					System.out.println(a);
////					System.out.println(a.itype);
//					
//					// Convierto reales a celdas
//					Vector2d cellCoords = new Vector2d();
//					
//					int x = (int) (a.position.x/stateObs.getBlockSize());
//					int y = (int) (a.position.y/stateObs.getBlockSize());
//					
//					cellCoords.set(x, y);
//					
//					System.out.println(cellCoords);
////					System.out.println("Objetos Inmoviles" + v.toString());
//				}
//			}		

//		// Dimensiones del mapa
//		System.out.println("Altura");
//		System.out.println(stateObs.getWorldDimension().height/stateObs.getBlockSize());
//		
//		System.out.println("Anchura");
//		System.out.println(stateObs.getWorldDimension().width/stateObs.getBlockSize());

		// Objetos movibles. NO HAY OBJETOS MOVILES

		/*
		 * Para poder ganar, el avatar debe tener una velocidad inferior a 10
		 */
		// Velocidad del avatar
//        System.out.println(stateObs.getAvatarSpeed());

		// Devuelve todas las acciones
//        System.out.println(stateObs.getAvailableActions());      

//		System.out.println("*******************************************");

//	    for (int i = 0; i < 1000; i++) {
//
//	    }

//		return actions.get(1);
		  return null;
        
    }
    
	public void close() {

//    	brain.saveQTable();

//    	System.out.println("QTable saved!");
//    	
//    	double time = QLearning.time;
//    	double alpha = brain.getAlpha();
//    	double score = stateObs.getGameScore();
//    	String row = Double.toString(time) + "," + Double.toString(alpha) + "," + Double.toString(score) + "\n";
//    	
//    	IOModule.write("./time_alpha_score.csv", row, true);
//    	
//		System.out.println("Time = " + time + " Alpha = " + alpha);

	}
}
