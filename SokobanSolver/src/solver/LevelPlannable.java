package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import algorithm.Action;
import algorithm.AndPredicate;
import algorithm.Plannable;
import algorithm.Predicate;
import algorithm.SimplePredicate;
import commons.Level2D;
import data.Searchable;
import data.State;
import model.data.BlankSpace;
import model.data.Crate;
import model.data.GameObject;
import model.data.GameObjectFactory;
import model.data.GoalPoint;
import model.data.MainCharacter;
import model.data.Position2D;
import model.data.Wall;
import model.policy.SokobanPolicy;

public class LevelPlannable implements Plannable<Position2D> {
	AndPredicate<Position2D> goal;
	AndPredicate<Position2D> kb;
	SokobanPolicy policy;
	
	public LevelPlannable(Level2D lev, SokobanPolicy policy) {
		this.policy=policy;
		goal=new AndPredicate<Position2D>();
		kb=new AndPredicate<Position2D>();
		//generate knowledgebase
		HashMap<Position2D, ArrayList<GameObject>> layout=lev.getPositionObjectLayout();
		addPredicates(layout,lev);
		//generate goal
		AndPredicate<Position2D> pToReturn = new AndPredicate<Position2D>();
		lev.getObjReferences().forEach((g)-> { 
			if(g instanceof GoalPoint) 
				getPositionsOfObject(g,lev).forEach((p)->pToReturn.add(new SimplePredicate<Position2D>("Crate_At",p)));});
		//AndPredicate<Position2D> z=kb;
		goal = pToReturn;
	}
	
	private void addPredicates(HashMap<Position2D, ArrayList<GameObject>> layout,Level2D lev)
	{

		for (Position2D p : layout.keySet()) {
			for (GameObject gameObj : lev.getGameObjectArrayOf(p)) {
				Position2D objPos=getPosOf(gameObj, lev);
				ArrayList<GameObject> objsAtPos=lev.getGameObjectArrayOf(objPos);
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
		layout.keySet().forEach((p) -> lev.getGameObjectArrayOf(p).forEach((gameObj)->kb.add(generatePredicate(gameObj,lev))));
	}
	
	private Predicate<Position2D> generatePredicate(GameObject gameObj,Level2D level)//TODO: generate predicate for each game object
	{
		
		return new SimplePredicate<Position2D>(gameObj.getClass().getSimpleName()+"_At",getPosOf(gameObj,level));
	}
	
//	private Level2D toLevel()
//	{
//		Level2D levelToReturn = new Level2D();
//		GameObjectFactory factory = new GameObjectFactory();
//		GameObject obj;
//		ArrayList<GameObject> objRef = new ArrayList<GameObject>();
//		ArrayList<GameObject> arr = new ArrayList<GameObject>();
//		for (Predicate<Position2D> p : kb.getComponents()) {
//			Position2D pos = p.getData();
//			if(p.getName().startsWith("Crate_At"))
//			{
//				obj = factory.createObject(Crate.class.getName().toString());
//				obj.setCurrentLocation(pos);
//				arr.add(obj);
//				levelToReturn.putInLayout(pos, arr);
//				objRef.add(obj);
//				levelToReturn.setObjReferences(objRef);
//			}
//			else if(p.getName().startsWith("MainCharacter_At"))
//			{
//				obj = factory.createObject(MainCharacter.class.getName().toString());
//				obj.setCurrentLocation(pos);
//				arr.add(obj);
//				levelToReturn.putInLayout(pos, arr);
//				objRef.add(obj);
//				levelToReturn.setObjReferences(objRef);
//			}
//			else if(p.getName().startsWith("Wall_At"))
//			{
//				obj = factory.createObject(Wall.class.getName().toString());
//				obj.setCurrentLocation(pos);
//				arr.add(obj);
//				levelToReturn.putInLayout(pos, arr);
//				objRef.add(obj);
//				levelToReturn.setObjReferences(objRef);
//			}
//			else if(p.getName().startsWith("Crate_At"))
//			{
//				obj = factory.createObject(Crate.class.getName().toString());
//				obj.setCurrentLocation(pos);
//				arr.add(obj);
//				levelToReturn.putInLayout(pos, arr);
//				objRef.add(obj);
//				levelToReturn.setObjReferences(objRef);
//			}
//			arr.clear();
//		}
//		return levelToReturn;
//	}
	
	@Override
	public List<Action<Position2D>> getSatisfyingActions(Predicate<Position2D> target) {
		ArrayList<Action<Position2D>> actions=new ArrayList<>();
//		Level2D level=this.toLevel();
		boolean legal=false;
		String name=target.getName();
		
		if(name.startsWith("MainCharacter_At"))
		{
			Searchable<Position2D> searchable=new Searchable<Position2D>() {
				
				@Override
				public State<Position2D> getInitialState() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public State<Position2D> getGoalState() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public PriorityQueue<State<Position2D>> getAllPossibleStates(State<Position2D> s) {
					
					return null;
				}
			};
		}
		else if(name.startsWith("Crate_At"))
		{
			Position2D targetPos=target.getData();//after: __@
			Position2D reqPlayerPos=new Position2D();//CHANGE FOR EACH CASE: before: A__
			Position2D reqCratePos=new Position2D();//before: _@_
			
			Integer x=target.getData().getX();
			Integer y=target.getData().getY();
			
			
			ArrayList<Action<Position2D>> actionList=new ArrayList<>();
			
			//right
			reqPlayerPos=new Position2D(x-2,y);
			reqCratePos=new Position2D(x-1,y);
			Action<Position2D> act1=new Action<>("Move_MainCharacter");
			updateAction(act1, reqPlayerPos, reqCratePos,targetPos);
			actionList.add(act1);
//			//left
			reqPlayerPos=new Position2D(x+2,y);
			reqCratePos=new Position2D(x+1,y);
			Action<Position2D> act2=new Action<>("Move_MainCharacter");
			updateAction(act2, reqPlayerPos, reqCratePos,targetPos);
			actionList.add(act2);
//			
//			//up
			reqPlayerPos=new Position2D(x,y+2);
			reqCratePos=new Position2D(x,y+1);
			Action<Position2D> act3=new Action<>("Move_MainCharacter");
			updateAction(act3, reqPlayerPos, reqCratePos,targetPos);
			actionList.add(act3);
//			
//			//down
			reqPlayerPos=new Position2D(x,y-2);
			reqCratePos=new Position2D(x,y-1);
			Action<Position2D> act4=new Action<>("Move_MainCharacter");
			updateAction(act4, reqPlayerPos, reqCratePos,targetPos);
			actionList.add(act4);
			
			
			actions.addAll(actionList);
		}
		
		else if(name.startsWith("Clear_At"))
		{
			//TODO: MOVE CRATE IF THERES A CRATE ON TARGET
		}
		return actions;
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
	
	
	@Override
	public Predicate<Position2D> getGoal() {
		return this.goal;
	}
	
	private ArrayList<Position2D> getPositionsOfObject(GameObject g,Level2D lev)
	{
		ArrayList<Position2D> posList=new ArrayList<>();getClass();
		lev.getPositionObjectLayout().keySet().forEach((pos)->{
			if(lev.getPositionObjectLayout().get(pos).contains(g))
			{
				posList.add(pos);
			}
		});
		return posList;
	}
	
	private Position2D getPosOf(GameObject obj,Level2D level)
	{
		for(Position2D pos : level.getPositionObjectLayout().keySet()) {
			if(level.getPositionObjectLayout().get(pos).contains(obj)) return pos;
		}
		return null;
	}
	

	@Override
	public AndPredicate<Position2D> getKnowledgebase() {
		return kb;
	}

	@Override
	public Action<Position2D> getSatisfyingAction(Predicate<Position2D> target) {
		//TODO: split getSatisfyingActions() into threads, return the fastest
		//TODO: for each thread: IF THERE'S NO WAY TO GET FROM A TO B, RETURN NULL (FAIL)
		return getSatisfyingActions(target).get(0);
	}
	
	

	@Override
	public boolean contradicts(Predicate<Position2D> p1, Predicate<Position2D> p2) {//p1 => ~p2
		String name1 = p1.getName();
		
		if(p2==null)
		{
			System.out.println();
		}
		String name2 = p2.getName();
		
		if(p1 instanceof SimplePredicate)
		{
			if(p2 instanceof AndPredicate)
			{
				boolean doesContra=false;
				AndPredicate<Position2D> temp=(AndPredicate<Position2D>)p2;
				for (Predicate<Position2D> predicate : temp.getComponents()) {
					doesContra=contradicts(p1, predicate);
				}
				return doesContra;
			}
			if(p2 instanceof SimplePredicate)
			{
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
					}
					else if(name1.startsWith("Crate_At"))
					{
						if(name2.startsWith("Wall_At")) return true;
						else if(name2.startsWith("MainCharacter_At")) return true;
						else if(name2.startsWith("Crate_At") &&  !(name1.equals(name2)))  return true;
					}
					else if(name1.startsWith("GoalPoint_At"))
					{
						if(name2.startsWith("GoalPoint_At") &&  !(name1.equals(name2)))  return true;
						else if(name2.startsWith("Wall_At")) return true;
					}
					else if(name1.startsWith("BlankSpace_At"))
					{
						if(name2.startsWith("Wall_At")) return true; 
						else if(name2.startsWith("Crate_At")) return true;
						else if(name2.startsWith("MainCharacter_At")) return true;
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
				//max
				int maxY=-1;
				int maxX=-1;
				for (Predicate<Position2D> pred : temp.getComponents()) {
					Position2D currentPos=pred.getData();
					if(maxY<currentPos.getY()) maxY=currentPos.getY();
					if(maxX<currentPos.getX()) maxX=currentPos.getX();
					if(contradicts(pred,p2)) return true;
				}
				if(p2.getData().getX()>maxX || p2.getData().getY()>maxY)
				{
					if(name2.startsWith("Wall_At")) return false;
					return true;
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

	@Override
	public boolean satisfies(Predicate<Position2D> p1, Predicate<Position2D> p2) {
		//if p1 -> p2
		if(p1==null || p2==null)
		{
			System.out.println();
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
			//TODO: GAME LOGIC HERE
			
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

	
	@Override
	public boolean kbSatisfies(Predicate<Position2D> p2) {
		return satisfies(kb, p2);
	}
	@Override
	public boolean kbContradicts(Predicate<Position2D> p2)
	{
		return contradicts(kb,p2);
	}
	
	@Override
	public void updateKb(AndPredicate<Position2D> effects) {
		effects.getComponents().forEach((p) -> {
			kb.getComponents().removeIf((pr) -> contradicts(p, pr));
		});
	}

}
