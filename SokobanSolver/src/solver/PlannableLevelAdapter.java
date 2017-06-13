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

public class PlannableLevelAdapter implements Plannable<Position2D> {

	private SokobanPolicy policy;
	private Level2D level;
	
	@Override
	public boolean contradicts(Predicate<Position2D> arg0, Predicate<Position2D> arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Predicate<Position2D> getGoal() {
		AndPredicate<Position2D> pToReturn = new AndPredicate<Position2D>();
		HashMap<Position2D, ArrayList<GameObject>> layout=level.getPositionObjectLayout();
		for (GameObject g : level.getObjReferences()) {
			if(g instanceof GoalPoint)
			{
				for (Position2D pos : level.getPositionsOfObject(g)) {
					
				}
			}
		}
	}
	@Override
	public AndPredicate<Position2D> getKnowledgebase() {
		// TODO Auto-generated method stub
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
