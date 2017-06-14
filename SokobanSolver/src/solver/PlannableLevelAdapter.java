package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.LayoutFocusTraversalPolicy;

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
		String name1 = p1.getName();
		String name2 = p2.getName();
		if(p1.getData().equals(p2))
		{
			if(name1.startsWith("Wall_At"))
			{
				if(name2.startsWith("Crate_At")) return true;
				if(name2.startsWith("Player1_At")) return true;
				if(name2.startsWith("GoalPoint_At")) return true;
				if(name2.startsWith("Wall_At")) return true;
			}
			if(name1.startsWith("Player1_At"))
			{
				if(name2.startsWith("Wall_At")) return true;
				if(name2.startsWith("Crate_At")) return true;
			}
			if(name1.startsWith("Crate_At"))
			{
				if(name2.startsWith("Wall_At")) return true;
				if(name2.startsWith("Player1_At")) return true;
				if(name2.startsWith("Crate_At") &&  !(name1.equals(name2)))  return true;
			}
			if(name1.startsWith("GoalPoint_At"))
			{
				if(name2.startsWith("GoalPoint_At") &&  !(name1.equals(name2)))  return true;
				if(name2.startsWith("Wall_At")) return true;
			}
		}
		return false;
	}
	@Override
	public Predicate<Position2D> getGoal() {
		AndPredicate<Position2D> pToReturn = new AndPredicate<Position2D>();
		level.getObjReferences().forEach((g)-> { 
			if(g instanceof GoalPoint) 
				level.getPositionsOfObject(g).forEach((p)->pToReturn.add(new SimplePredicate<Position2D>("Crate #?",p)));});
		return pToReturn;
	}
	@Override
	public AndPredicate<Position2D> getKnowledgebase() {
		
		HashMap<Position2D, ArrayList<GameObject>> layout=level.getPositionObjectLayout();
		layout.keySet().forEach((p) -> level.getGameObjectArrayOf(p).forEach((gameObj)->kb.add(generatePredicate(gameObj))));
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
	public boolean satisfies(Predicate<Position2D> p1, Predicate<Position2D> p2) {
		//if p1 -> p2
		if(contradicts(p1, p2)) return false;
		
		if(p1 instanceof AndPredicate)
		{
			if(p2 instanceof SimplePredicate)
			{
				AndPredicate<Position2D> temp = (AndPredicate) p1;
				for (Predicate<Position2D> predicate : temp.getComponents()) 
				{
					if(!(temp.getComponents().contains(predicate))) return false;
				}
				return true;
			}
			if(p2 instanceof AndPredicate)
			{
				return p1.equals(p2);
			}
		}
	}
}
