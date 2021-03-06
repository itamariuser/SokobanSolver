package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import algorithm.Action;
import algorithm.AndPredicate;
import algorithm.LinkAction;
import algorithm.Plannable;
import algorithm.Predicate;
import algorithm.SimplePredicate;
import common.Level2D;
import common.Position2D;
import data.BestFirstSearcher;
import data.GoalNotFoundException;
import data.Searchable;
import data.Solution;
import data.State;
import gameObjects.BlankSpace;
import gameObjects.Crate;
import gameObjects.GameObject;
import gameObjects.GoalPoint;
import gameObjects.MainCharacter;
import gameObjects.Wall;
/**
 * An object adapter from level to plannable, for use with a Planner.
 * @author itamar
 */
public class LevelPlannable implements Plannable<Position2D> {
	AndPredicate<Position2D> goal;
	AndPredicate<Position2D> kb;
	
	public LevelPlannable(Level2D lev) {
		goal=new AndPredicate<Position2D>();
		kb=new AndPredicate<Position2D>();
		//generate knowledgebase
		
		addPredicates(lev);
		//generate goal
		AndPredicate<Position2D> pToReturn = new AndPredicate<Position2D>();
		lev.getObjReferences().forEach((g)-> { 
			if(g instanceof GoalPoint) 
			{
				Position2D pos=getPosOf(g, lev);
				pToReturn.add(new SimplePredicate<Position2D>("Crate_At",pos));
			}
			goal = pToReturn;
		});
	}
	
	@Override
	public Predicate<Position2D> getGoal() {
		return this.goal;
	}
	@Override
	public AndPredicate<Position2D> getKnowledgebase() {
		return kb;
	}
	/**
	 * Iterate over the game objects of a level, and insert fitting predicates to the knowledge base.
	 * @param level
	 */
	private void addPredicates(Level2D level)
	{
		HashMap<Position2D, ArrayList<GameObject>> layout=level.getPositionObjectLayout();
		for (Position2D p : layout.keySet()) {
			for (GameObject gameObj : level.getGameObjectArrayOf(p)) {
				Position2D objPos=getPosOf(gameObj, level);
				ArrayList<GameObject> objsAtPos=level.getGameObjectArrayOf(objPos);
				boolean posBlocked=false;
				for (GameObject gameObject : objsAtPos) {
					if(gameObject instanceof Crate || gameObject instanceof MainCharacter || gameObject instanceof Wall)
					{
						posBlocked=true;
					}
				}
				if((gameObj instanceof BlankSpace || gameObj instanceof GoalPoint) && posBlocked==false)
				{
					kb.add(new SimplePredicate<Position2D>("Clear_At", objPos));
				}
			}
		}
		layout.keySet().forEach((p) -> level.getGameObjectArrayOf(p).forEach((gameObj)->kb.add(generatePredicate(gameObj,level))));
	}
	
	/**
	 * Given a gameObject and a level, generate a fitting predicate.
	 * @param gameObj - The game object.
	 * @param level - The level, from which the game object is taken.
	 * @return A predicate repesenting the game object.
	 */
	private Predicate<Position2D> generatePredicate(GameObject gameObj,Level2D level)
	{
		return new SimplePredicate<Position2D>(gameObj.getClass().getSimpleName()+"_At",getPosOf(gameObj,level));
	}
	
	/**
	 * Given a move action, update its effects and preconditions according to a source and target position.
	 * @param action - The action to update.
	 * @param srcPos - Position before the action.
	 * @param destPos - Position after the action.
	 */
	private void updateAction(Action<Position2D> action,Position2D srcPos,Position2D destPos)
	{
		if(action.getName().startsWith("Move"))
		{
			action.addPrecondition(new SimplePredicate<Position2D>("MainCharacter_At", srcPos));
			action.addEffect(new SimplePredicate<Position2D>("MainCharacter_At", destPos));
			action.addEffect(new SimplePredicate<Position2D>("Clear_At", srcPos));
		}
	}
	
	/**
	 * Determine how different an action is from the current knowledge base.
	 * 
	 * @param action - The evaluated action.
	 * @return The difference score. Higher is more different.
	 */
	private float difFromKB(Action<Position2D> action)
	{
		float cost=0f;
		for (Predicate<Position2D> predicate : action.getPreconditions().getComponents()) {
			String predName=predicate.getName();
			Position2D predPos=predicate.getData();
			float importanceMultiplier=0.0f;
			if(predName.startsWith("Clear_At")) importanceMultiplier=1.2f;
			else if(predName.startsWith("MainCharacter_At")) importanceMultiplier=1.0f;
			else if(predName.startsWith("Crate_At")) importanceMultiplier=10f;
			else if(predName.startsWith("Wall_At")) importanceMultiplier=9999.0f;
			
			float minDist=Float.MAX_VALUE;
			for (Predicate<Position2D> kbPredi : kb.getComponents()) {
				if(kbPredi.getName().equals(predName))
				{
					float currDist=(float) Position2D.getDistanceFromPosition2D(predPos, kbPredi.getData());
					if(currDist<minDist)
					{
						minDist=currDist;
					}
				}
				else if(kbPredi.getName().startsWith("Wall_At") && kbPredi.getData().equals(predicate.getData()))
				{
					cost+=Float.MAX_VALUE/2;
				}
			}
			cost+=minDist*importanceMultiplier;
		}
		return cost;
	}
	/**
	 * Generate a list of possible actions which satisfy the target predicate.
	 * 
	 * @param target - The target predicate.
	 * @return A list of possible actions which satisfy the target predicate.
	 */
	@Override
	public List<Action<Position2D>> getSatisfyingActions(Predicate<Position2D> target) {
		ArrayList<Action<Position2D>> actions=new ArrayList<>();
		String name=target.getName();
		
		Position2D targetPos=target.getData();//after: __@
		Integer x=target.getData().getX();
		Integer y=target.getData().getY();
		
		if(name.startsWith("MainCharacter_At"))
		{
			Searchable<Position2D> searchable=new Searchable<Position2D>() {
				
				@Override
				public State<Position2D> getInitialState() {
					State<Position2D> state=new State<Position2D>();
					kb.getComponents().forEach((p)->
					{
						if(p.getName().startsWith("MainCharacter_At"))
						{
							state.setLayout(p.getData());
						}
					});
					return state;
				}
				
				@Override
				public State<Position2D> getGoalState() {
					return new State<Position2D>(target.getData());
				}
				
				@Override
				public PriorityQueue<State<Position2D>> getAllPossibleStates(State<Position2D> s) {
					Integer x=s.getLayout().getX();
					Integer y=s.getLayout().getY();
					
					PriorityQueue<State<Position2D>> pos = new PriorityQueue<>(new Comparator<State<Position2D>>() {

						@Override
						public int compare(State<Position2D> o1, State<Position2D> o2) {
							return o1.getCostFromParent() - o2.getCostFromParent();
						}
					});
						
					//left
					Position2D position1=new Position2D(x-1,y);
					if(isLegal(position1))
					{
						State<Position2D> state1=new State<Position2D>(position1);
						state1.setCameFromState(s);
						state1.setCostFromParent(1);
						pos.add(state1);
					}
					//right
					Position2D position2=new Position2D(x+1,y);
					if(isLegal(position2))
					{
						State<Position2D> state1=new State<Position2D>(position2);
						state1.setCameFromState(s);
						state1.setCostFromParent(1);
						pos.add(state1);
					}
					//up
					Position2D position3=new Position2D(x,y-1);
					if(isLegal(position3))
					{
						State<Position2D> state1=new State<Position2D>(position3);
						state1.setCameFromState(s);
						state1.setCostFromParent(1);
						pos.add(state1);
					}
					//down
					Position2D position4=new Position2D(x,y+1);
					if(isLegal(position4))
					{
						State<Position2D> state1=new State<Position2D>(position4);
						state1.setCameFromState(s);
						state1.setCostFromParent(1);
						pos.add(state1);
					}
					return pos;
				}
			};
			BestFirstSearcher<Position2D> BFS=new BestFirstSearcher<>();
			Solution<Position2D> sol=null;
			try {
				sol=BFS.search(searchable);
			} catch (GoalNotFoundException e) {
				e.printStackTrace();
			}
			
			LinkAction<Position2D> linkAction=new LinkAction<>("Move_Path");
			SimplePredicate<Position2D> effect1=new SimplePredicate<Position2D>(target);
			linkAction.setEffects(effect1);
			
			SimplePredicate<Position2D> precondition=new SimplePredicate<Position2D>("MainCharacter_At", searchable.getInitialState().getLayout());
			linkAction.setPreconditions(precondition);
			
			ArrayList<Action<Position2D>> lActions=new ArrayList<>();
			ArrayList<State<Position2D>> path=sol.getPathToVictory();
			Collections.reverse(path);
			for (State<Position2D> state : path) {
				if(state.getCameFromState()!=null)
				{
					Position2D afterAction=state.getLayout();
					Position2D beforeAction=state.getCameFromState().getLayout();
					Action<Position2D> generatedAct=new Action<>("Move_");
					if(afterAction.getX()==beforeAction.getX()+1)//right
					{
						generatedAct.setName(generatedAct.getName()+"Right");
						updateAction(generatedAct, beforeAction, afterAction);
					}
					else if(afterAction.getX()==beforeAction.getX()-1)//left
					{
						generatedAct.setName(generatedAct.getName()+"Left");
						updateAction(generatedAct, beforeAction, afterAction);
					}
					else if(afterAction.getY()==beforeAction.getY()+1)//down
					{
						generatedAct.setName(generatedAct.getName()+"Down");
						updateAction(generatedAct, beforeAction, afterAction);
					}
					else if(afterAction.getY()==beforeAction.getY()-1)//up
					{
						generatedAct.setName(generatedAct.getName()+"Up");
						updateAction(generatedAct, beforeAction, afterAction);
					}
					lActions.add(generatedAct);
				}
				else
				{
					
				}
			}
			linkAction.setActions(lActions);
			
			linkAction.getActions().get(0).setPreconditions(linkAction.getPreconditions());
			Action<Position2D> lastAction=linkAction.getActions().get(linkAction.getActions().size()-1);
			lastAction.setEffects(linkAction.getEffects());
			if(target.getName().startsWith("MainCharacter_At") && target.getData().equals(new Position2D(3,3)))
			{
				System.out.println();
			}
			for (Predicate<Position2D> p :lastAction.getPreconditions().getComponents()) {
				if(p.getName().startsWith("MainCharacter_At"))
				{
					lastAction.addEffect(new SimplePredicate<Position2D>("Clear_At",p.getData()));
				}
			}
			for (Predicate<Position2D> p : linkAction.getActions().get(linkAction.getActions().size()-1).getEffects().getComponents()) {
				if(p.getName().startsWith("MainCharacter_At"))
				{
					
				}
			}
			
			Position2D posToReplace=new Position2D();
			for (Predicate<Position2D> pred : linkAction.getPreconditions().getComponents()) {
				if(pred.getName().startsWith("MainCharacter_At"))
				{
					posToReplace=pred.getData();
				}
			}
			linkAction.getActions().get(linkAction.getActions().size()-1).addEffect(new SimplePredicate<Position2D>("Clear_At", posToReplace));
			Collections.reverse(linkAction.getActions());
			ArrayList<Action<Position2D>> actions1=new ArrayList<>();
			actions1.add(linkAction);
			return actions1;
		}
		else if(name.startsWith("Crate_At"))
		{
			
			Position2D reqPlayerPos=new Position2D();//CHANGE FOR EACH CASE: before: A__
			Position2D reqCratePos=new Position2D();//before: _@_
			
			
			
			ArrayList<Action<Position2D>> actionList=new ArrayList<>();
			
			//right
			reqPlayerPos=new Position2D(x-2,y);
			reqCratePos=new Position2D(x-1,y);
			Action<Position2D> act1=new Action<>("Move_Right");
			updateAction(act1, reqPlayerPos, reqCratePos,targetPos);
			actionList.add(act1);
			
			//left
			reqPlayerPos=new Position2D(x+2,y);
			reqCratePos=new Position2D(x+1,y);
			
			Action<Position2D> act2=new Action<>("Move_Left");
			updateAction(act2, reqPlayerPos, reqCratePos,targetPos);
			actionList.add(act2);
			
			//up
			reqPlayerPos=new Position2D(x,y+2);
			reqCratePos=new Position2D(x,y+1);
			Action<Position2D> act3=new Action<>("Move_Up");
			updateAction(act3, reqPlayerPos, reqCratePos,targetPos);
			actionList.add(act3);
			
			//down
			reqPlayerPos=new Position2D(x,y-2);
			reqCratePos=new Position2D(x,y-1);
			Action<Position2D> act4=new Action<>("Move_Down");
			updateAction(act4, reqPlayerPos, reqCratePos,targetPos);
			actionList.add(act4);
			
			actions.addAll(actionList);
		}
		
		else if(name.startsWith("Clear_At"))
		{
			Predicate<Position2D> pred=new SimplePredicate<Position2D>("Clear_At");
			for (Predicate<Position2D> predicate : kb.getComponents()) {
				if(predicate.getData().equals(target.getData()))
				{
					pred=predicate;
				}
			}
			if(pred.getName().startsWith("Crate_At"))
			{
				
			}
			else if(pred.getName().startsWith("MainCharacter_At"))
			{
				Action<Position2D> actRight=new Action<>("Move_Right");
				Action<Position2D> actLeft=new Action<>("Move_Left");
				Action<Position2D> actDown=new Action<>("Move_Down");
				Action<Position2D> actUp=new Action<>("Move_Up");

				updateActionMove(actRight, new Position2D(x+1,y), pred.getData());
				updateActionMove(actLeft, new Position2D(x-1,y), pred.getData());
				updateActionMove(actDown, new Position2D(x,y+1), pred.getData());
				updateActionMove(actUp, new Position2D(x,y-1), pred.getData());
				
				actions.add(actRight);
				actions.add(actLeft);
				actions.add(actDown);
				actions.add(actUp);
			}
		}
		return actions;
	}
	
	private boolean isLegal(Position2D pos)
	{
		for (Predicate<Position2D> p : kb.getComponents()) {
			if(p.getName().startsWith("Clear_At") && p.getData().equals(pos))
			{
				return true;
			}
		}
		return false;
	}
	
	private void updateAction(Action<Position2D> action,Position2D playerPos,Position2D cratePos,Position2D targetPos)
	{
		AndPredicate<Position2D> preconditions=new AndPredicate<>();
		preconditions.add(new SimplePredicate<Position2D>("MainCharacter_At", playerPos));
		preconditions.add(new SimplePredicate<Position2D>("Crate_At", cratePos));
		preconditions.add(new SimplePredicate<Position2D>("Clear_At", targetPos));
		action.setPreconditions(preconditions);
		
		AndPredicate<Position2D> effects=new AndPredicate<>();
		effects.add(new SimplePredicate<Position2D>("Clear_At", playerPos));
		effects.add(new SimplePredicate<Position2D>("MainCharacter_At", cratePos));
		effects.add(new SimplePredicate<Position2D>("Crate_At", targetPos));
		action.setEffects(effects);
	}
	
	private void updateActionMove(Action<Position2D> action,Position2D to,Position2D from)
	{
		AndPredicate<Position2D> preconditions=new AndPredicate<>();
		preconditions.add(new SimplePredicate<Position2D>("MainCharacter_At", from));
		preconditions.add(new SimplePredicate<Position2D>("Clear_At", to));
		action.setPreconditions(preconditions);
		
		AndPredicate<Position2D> effects=new AndPredicate<>();
		effects.add(new SimplePredicate<Position2D>("Clear_At", from));
		effects.add(new SimplePredicate<Position2D>("MainCharacter_At", to));
		action.setEffects(effects);
	}
	
	/**
	 * Returns the position of a given game object in a given level
	 * @param obj - The game object to search for.
	 * @param level - The level.
	 * @return The object's position in the level.
	 */
	private Position2D getPosOf(GameObject obj,Level2D level)
	{
		for (Position2D pos : level.getPositionObjectLayout().keySet()) {
			if (level.getPositionObjectLayout().get(pos).contains(obj))
				return pos;
		}
		return null;
	}
	/**
	 * Find a single action which satisfies the target predicate.
	 * This is done using the "difFromKB" function to determine which action is better, so there's a need to change the function according to the algorithm.
	 * @param target - The target predicate.
	 * @return An action which satisfies the target predicate.
	 */
	@Override
	public Action<Position2D> getSatisfyingAction(Predicate<Position2D> target) {
		
		List<Action<Position2D>> actions=getSatisfyingActions(target);
		Action<Position2D> minAction=actions.get(0);
		float minCost=difFromKB(minAction);
		for (Action<Position2D> action : actions) {
			float currCost=difFromKB(action);
			if(currCost<minCost)
			{
				minAction=action;
				minCost=currCost;
			}
		}
		return minAction;
	}
	
	/**
	 * Returns whether the left predicate <b>contradicts</b> the right predicate, meaning <b>p1 -> !p2</b>
	 * This might be changed according to the game rules.
	 * @param p1 - The left side predicate.
	 * @param p2 - The right side predicate.
	 * @return true if p1 contradicts p2, false otherwise.
	 */
	@Override
	public boolean contradicts(Predicate<Position2D> p1, Predicate<Position2D> p2) {//p1 -> ~p2
		String name1 = p1.getName();
		String name2 = p2.getName();
		
		if(p1 instanceof SimplePredicate)
		{
			if(p2 instanceof AndPredicate)
			{
				AndPredicate<Position2D> temp=(AndPredicate<Position2D>)p2;
				for (Predicate<Position2D> predicate : temp.getComponents()) {
					if(contradicts(p1, predicate)) return true;
				}
			}
			if(p2 instanceof SimplePredicate)
			{
				if(p1.equals(p2)) return false;
				if(p1.getData().equals(p2.getData()))
				{
					if(name1.startsWith("Wall_At"))
					{
						if(name2.startsWith("Crate_At")) return true;
						else if(name2.startsWith("MainCharacter_At")) return true;
						else if(name2.startsWith("GoalPoint_At")) return true;
					}
					else if(name1.startsWith("MainCharacter_At"))
					{
						if(name2.startsWith("Wall_At")) return true;
						else if(name2.startsWith("Crate_At")) return true;
						else if(name2.startsWith("Clear_At")) return true;
					}
					else if(name1.startsWith("Crate_At"))
					{
						if(name2.startsWith("Wall_At")) return true;
						else if(name2.startsWith("MainCharacter_At")) return true;
						else if(name2.startsWith("Clear_At")) return true;
					}
					else if(name1.startsWith("GoalPoint_At"))
					{
						if(name2.startsWith("GoalPoint_At") &&  !(name1.equals(name2)))  return true;
						else if(name2.startsWith("Wall_At")) return true;
					}
					else if(name1.startsWith("BlankSpace_At"))
					{
						if(name2.startsWith("Wall_At")) return true; 
					}
					else if(name1.startsWith("Clear_At"))
					{
						if(name2.startsWith("Crate_At")) return true;
						else if(name2.startsWith("MainCharacter_At")) return true;
					}
				}
				else
				{
					if(name1.startsWith("MainCharacter_At") &&
							name2.startsWith("MainCharacter_At") &&
							!p1.getData().equals(p2.getData()))
					{
						return true;
					}
				}
				return false;
			}
		}
		if(p1 instanceof AndPredicate )
		{
			if(p2 instanceof SimplePredicate)
			{
				AndPredicate<Position2D> temp=new AndPredicate<Position2D>((AndPredicate<Position2D>)p1);
				for (Predicate<Position2D> pred : temp.getComponents()) {
					if(contradicts(pred,p2)) 
					{
						return true;
					}
				}
				return false;
				
			}
			else if( p2 instanceof AndPredicate )
			{
				AndPredicate<Position2D> temp1=(AndPredicate<Position2D>)p1;
				boolean doesContra=false;
				for (Predicate<Position2D> p11 : temp1.getComponents()) {
					doesContra=contradicts(p11,p2);
				}
				return doesContra;
			}
		}
		return false;
	}

	/**
	 * Returns whether the left predicate <b>satisfies</b> the right predicate, meaning <b>p1 -> p2</b>
	 * This might be changed according to the game rules.
	 * @param p1 - The left side predicate.
	 * @param p2 - The right side predicate.
	 * @return true if p1 satisfies p2, false otherwise.
	 */
	@Override
	public boolean satisfies(Predicate<Position2D> p1, Predicate<Position2D> p2) { //if p1 -> p2
		try {
			if (p1 == null || p2 == null) {
				throw new Exception("Null Predicate");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(contradicts(p1, p2)) return false;
		
		
		if(p1 instanceof AndPredicate)
		{
			if(p2 instanceof SimplePredicate)
			{
				AndPredicate<Position2D> s=((AndPredicate<Position2D>) p1);
				for (Predicate<Position2D> p : s.getComponents()) {
					if(satisfies(p, p2)) return true;
				}
				return false;
			}
			else if(p2 instanceof AndPredicate)
			{
				for (Predicate<Position2D> pre : ((AndPredicate<Position2D>) p2).getComponents()) {
					if(!satisfies(p1, pre))
					{
						return false;
					}
				}
				return true;
			}
		}
		
		else if(p1 instanceof SimplePredicate)
		{
			if(p2 instanceof SimplePredicate)
			{
				if(p1.equals(p2)) return true;
				if(p2.getName().startsWith("Clear_At"))
				{
					if(p1.getName().startsWith("GoalPoint_At")) return true;
					if(p1.getName().startsWith("BlankSpace_At")) return true;
				}
			}
			else if(p2 instanceof AndPredicate)
			{
				for (Predicate<Position2D> pre : ((AndPredicate<Position2D>) p2).getComponents()) {
					if(!satisfies(p1, pre))
					{
						return false;
					}
				}
				return true;
			}
		}
		return p1.equals(p2);
	}

	/**
	 * Returns whether the knowledge base <B>satisfies</b> a predicate.
	 * @param p - The predicate to be checked.
	 * @return true if <B>kb satisfies p</B>, false otherwise.
	 */
	@Override
	public boolean kbSatisfies(Predicate<Position2D> p) {
		return satisfies(kb, p);
	}
	
	/**
	 * Returns whether the knowledge base <B>contradicts</b> a predicate.
	 * @param p - The predicate to be checked.
	 * @return true if <B>kb contradicts p</B>, false otherwise.
	 */
	@Override
	public boolean kbContradicts(Predicate<Position2D> p2)
	{
		return contradicts(kb,p2);
	}
	
	/**
	 * Simulates the execution of effects on the knowledge base.
	 * The knowledge base will change based on the effects.
	 * @param effects - The effects to be simulated.
	 */
	@Override
	public void updateKb(AndPredicate<Position2D> effects) {
		effects.getComponents().forEach((p) -> {
			
			kb.getComponents().removeIf((pr) -> {
				boolean cont=contradicts(p, pr);
				return cont;
			});
			kb.add(p);
		});
	}

}
