package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import algorithm.Action;
import algorithm.AndPredicate;
import algorithm.Plannable;
import algorithm.Predicate;
import algorithm.SimplePredicate;
import gameObjects.GameObject;
import gameObjects.GoalPoint;
import gameObjects.Position2D;
import model.Level2D;
import model.SokobanPolicy;

public class PlannableLevelAdapter implements Plannable<Position2D> {//Generates a new searchable, everytime goalState= where we want to go for "Move player" action

	private SokobanPolicy policy;
	private Level2D level;
	AndPredicate<Position2D> kb;
	public PlannableLevelAdapter() {
		kb=new AndPredicate<Position2D>();
	}
	
	/**
	 * return true only if p1 contradicts p2
	 *  
	 */
	@Override
	public boolean contradicts(Predicate<Position2D> p1, Predicate<Position2D> p2) {
		
		return false;
	}
	@Override
	public Predicate<Position2D> getGoal() {
		AndPredicate<Position2D> pToReturn = new AndPredicate<Position2D>();
		for (GameObject g : level.getObjReferences()) {
			if(g instanceof GoalPoint)
			{
				for (Position2D pos : level.getPositionsOfObject(g)) {
					pToReturn.add(new SimplePredicate<Position2D>("Crate #?",pos));
				}
			}
		}
		return pToReturn;
	}
	@Override
	public AndPredicate<Position2D> getKnowledgebase() {
		
		HashMap<Position2D, ArrayList<GameObject>> layout=level.getPositionObjectLayout();
		for (Position2D pos : layout.keySet()) {
			for (GameObject gameObj : level.getGameObjectArrayOf(pos)) {
				kb.add(generatePredicate(gameObj));
			}
		}
		return kb;
	}
	
	private Predicate<Position2D> generatePredicate(GameObject gameObj)//TODO: generate predicate for each game object
	{
		//Rules rules rules....
		//if instanceof crate, then add crate etc. etc.
		return null;
	}
	@Override
	public Action<Position2D> getSatisfyingAction(Predicate<Position2D> arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Action<Position2D>> getSatisfyingActions(Predicate<Position2D> arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean satisfies(Predicate<Position2D> arg0, Predicate<Position2D> arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
