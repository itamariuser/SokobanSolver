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
import data.GoalNotFoundException;
import data.Searchable;
import data.Searcher;
import data.Solution;
import data.State;
import gameObjects.GameObject;
import gameObjects.GoalPoint;
import gameObjects.Position2D;
import model.Level2D;

public class SokobanSolver implements Plannable<Position2D> {//Generates a new searchable, everytime goalState= where we want to go for "Move player" action
	Searcher<Position2D> searcher;
	AndPredicate<Position2D> kb;
	AndPredicate<Position2D> goal;
	public SokobanSolver(Level2D level, Searcher<Position2D> s) {
		kb=new AndPredicate<Position2D>();
		//generate knowledgebase
		HashMap<Position2D, ArrayList<GameObject>> layout=level.getPositionObjectLayout();
		layout.keySet().forEach((p) -> level.getGameObjectArrayOf(p).forEach((gameObj)->kb.add(generatePredicate(gameObj,level))));
		
		//generate goal
		AndPredicate<Position2D> pToReturn = new AndPredicate<Position2D>();
		level.getObjReferences().forEach((g)-> { 
			if(g instanceof GoalPoint) 
				level.getPositionsOfObject(g).forEach((p)->pToReturn.add(new SimplePredicate<Position2D>("Crate_At",p)));});
		goal = pToReturn;
		
		this.searcher=s;
	}
	
	
	/**
	 * return true only if p1 contradicts p2
	 *  
	 */
	@Override
	public boolean contradicts(Predicate<Position2D> p1, Predicate<Position2D> p2) {//p1 => ~p2
		String name1 = p1.getName();
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
						else if(name2.startsWith("Player1_At")) return true;
						else if(name2.startsWith("GoalPoint_At")) return true;
						else if(name2.startsWith("Wall_At")) return true;
					}
					else if(name1.startsWith("Player1_At"))
					{
						if(name2.startsWith("Wall_At")) return true;
						else if(name2.startsWith("Crate_At")) return true;
					}
					else if(name1.startsWith("Crate_At"))
					{
						if(name2.startsWith("Wall_At")) return true;
						else if(name2.startsWith("Player1_At")) return true;
						else if(name2.startsWith("Crate_At") &&  !(name1.equals(name2)))  return true;
					}
					else if(name1.startsWith("GoalPoint_At"))
					{
						if(name2.startsWith("GoalPoint_At") &&  !(name1.equals(name2)))  return true;
						else if(name2.startsWith("Wall_At")) return true;
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
					if(contradicts(pred,p2)) return true;
				}
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
	public Predicate<Position2D> getGoal() {
		return this.goal;
	}
	@Override
	public AndPredicate<Position2D> getKnowledgebase() {
		return this.kb;
	}
	
	private Predicate<Position2D> generatePredicate(GameObject gameObj,Level2D level)//TODO: generate predicate for each game object
	{
		
		return new SimplePredicate<Position2D>(gameObj.getClass().getSimpleName()+"_At",getPosOf(gameObj,level) );
	}
	
	private Position2D getPosOf(GameObject obj,Level2D level)
	{
		for(Position2D pos : level.getPositionObjectLayout().keySet()) {
			if(level.getPositionObjectLayout().get(pos).contains(obj)) return pos;
		}
		return null;
	}
	
	@Override
	public Action<Position2D> getSatisfyingAction(Predicate<Position2D> arg0) {
		return getSatisfyingActions(arg0).get(0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Action<Position2D>> getSatisfyingActions(Predicate<Position2D> top) {
		int x=top.getData().getX();
		int y=top.getData().getY();
		if(top.getName().startsWith("Crate_At"))// change to fit class CratePredicate<Position>
		{
			ArrayList<Action<Position2D>> possibleActions= new ArrayList<>();//this list will be returned
			ArrayList<Predicate<Position2D>> toGenerate=new ArrayList<>();//all items in this list will be generated for each action
			ArrayList<Predicate<Position2D>> toRemove=new ArrayList<>();//items in this list will be removed after each generation of action
			Action<Position2D> act=new Action<Position2D>("Move_Crate_To");
			act.setEffects(new AndPredicate<>(top));//set effects to be "Crate at position "(x,y)", "No non solid at position (x,y)" (which means crate is in pos)
			SimplePredicate<Position2D> player1IsAtPosition=new SimplePredicate<Position2D>("Player1_At",null);//add player in position to push the crate
			
			SimplePredicate<Position2D> wallAtNextPos=new SimplePredicate<Position2D>("Wall_At",new Position2D(x,y));
			SimplePredicate<Position2D> crateAtPosition=new SimplePredicate<>("Crate_At",new Position2D(x,y));
			
			if(contradicts(kb,wallAtNextPos))//check if no wall at nextPos
			{
				if(!satisfies(kb, crateAtPosition))
				{
					SimplePredicate<Position2D> playerNewPos=new SimplePredicate<Position2D>("Player1_At");
					
					SimplePredicate<Position2D> CrateIsAtPosition=new SimplePredicate<Position2D>("Crate_At",null);//crate is at position to be pushed
					SimplePredicate<Position2D> wallAtPlayer
					//push right
					if(!satisfies(kb, p2))
					{
						CrateIsAtPosition.setData(new Position2D(x-1,y));
						player1IsAtPosition.setData(new Position2D(x-2,y));
						
						playerNewPos.setData(new Position2D(x-1,y));
						updateLists(toGenerate,toRemove,new SimplePredicate<>(CrateIsAtPosition),new SimplePredicate<>(player1IsAtPosition));
						act.setEffects(new AndPredicate<>(top,new SimplePredicate<Position2D>(playerNewPos)));
						act.setPreconditions(new AndPredicate<Position2D>(toGenerate));
						possibleActions.add(new Action<Position2D>(act.getName(),act.getPreconditions(),act.getEffects()));
					}
					//down
					
					CrateIsAtPosition.setData(new Position2D(x,y-1));
					player1IsAtPosition.setData(new Position2D(x,y-2));
					playerNewPos.setData(new Position2D(x,y-1));
					updateLists(toGenerate,toRemove,new SimplePredicate<>(CrateIsAtPosition),new SimplePredicate<>(player1IsAtPosition));
					act.setEffects(new AndPredicate<>(top,new SimplePredicate<Position2D>(playerNewPos)));
					act.setPreconditions(new AndPredicate<Position2D>(toGenerate));
					possibleActions.add(new Action<Position2D>(act.getName(),act.getPreconditions(),act.getEffects()));
					
					//left
					
					CrateIsAtPosition.setData(new Position2D(x+1,y));
					player1IsAtPosition.setData(new Position2D(x+2,y));
					playerNewPos.setData(new Position2D(x+1,y));
					updateLists(toGenerate,toRemove,new SimplePredicate<>(CrateIsAtPosition),new SimplePredicate<>(player1IsAtPosition));
					act.setEffects(new AndPredicate<>(top,new SimplePredicate<Position2D>(playerNewPos)));
					act.setPreconditions(new AndPredicate<Position2D>(toGenerate));
					possibleActions.add(new Action<Position2D>(act.getName(),act.getPreconditions(),act.getEffects()));
					
					//up
					
					CrateIsAtPosition.setData(new Position2D(x,y+1));
					player1IsAtPosition.setData(new Position2D(x,y+2));
					playerNewPos.setData(new Position2D(x,y+1));
					updateLists(toGenerate,toRemove,new SimplePredicate<>(CrateIsAtPosition),new SimplePredicate<>(player1IsAtPosition));
					act.setEffects(new AndPredicate<>(top,new SimplePredicate<Position2D>(playerNewPos)));
					act.setPreconditions(new AndPredicate<Position2D>(toGenerate));
					possibleActions.add(new Action<Position2D>(act.getName(),act.getPreconditions(),act.getEffects()));
				}
				else if(satisfies(kb, crateAtPosition))//there's a crate in the position
				{
					ArrayList<Action<Position2D>> list=new ArrayList<>();
					list.add(getSatisfyingAction(new SimplePredicate<>("Crate_At",new Position2D(x+1,y))));
					list.add(getSatisfyingAction(new SimplePredicate<>("Crate_At",new Position2D(x-1,y))));
					list.add(getSatisfyingAction(new SimplePredicate<>("Crate_At",new Position2D(x,y+1))));
					list.add(getSatisfyingAction(new SimplePredicate<>("Crate_At",new Position2D(x,y-1))));
					return list;
					
				}
			}
			/*debug
			StringBuilder actionsToString= new StringBuilder();
			for (Action<Position2D> action : possibleActions) {
				actionsToString.append(action.toString()+"\n");
			}
			
			System.out.println("GENERATED FOR PREDICATE:\n"+top.toString()+"\nNEW ACTIONS: \n"+actionsToString.toString());
			*/
			System.out.println(possibleActions);
			return possibleActions;
		}
		if(top.getName().startsWith("Player1")){
			for (Predicate<Position2D> p : kb.getComponents()) {
				if(p.getName().startsWith("Player1_At")){
					Searchable<Position2D> searchable=new Searchable<Position2D>() {
						
						@Override
						public State<Position2D> getInitialState() {
							return new State<Position2D>(new Position2D(p.getData())); 
						}
						
						@Override
						public State<Position2D> getGoalState() {
							return new State<Position2D>(new Position2D(top.getData()));
						}
						
						@Override
						public PriorityQueue<State<Position2D>> getAllPossibleStates(State<Position2D> state) {
							int x=state.getLayout().getX();
							int y=state.getLayout().getY();
							PriorityQueue<State<Position2D>> pq=new PriorityQueue<>();
							SimplePredicate<Position2D> wallAtNextPos=new SimplePredicate<Position2D>("Wall_At",null);
							
							wallAtNextPos.setData(new Position2D(x+1,y));//right state
							if(contradicts(kb,wallAtNextPos))
							{
								State<Position2D> state1=new State<>(new Position2D(x+1,y));
								pq.add(state1);
							}
							
							wallAtNextPos.setData(new Position2D(x-1,y));//left state
							if(contradicts(kb,wallAtNextPos))
							{
								State<Position2D> state1=new State<>(new Position2D(x-1,y));
								pq.add(state1);
							}
							
							wallAtNextPos.setData(new Position2D(x,y+1));//up state
							if(contradicts(kb,wallAtNextPos))
							{
								State<Position2D> state1=new State<>(new Position2D(x,y+1));
								pq.add(state1);
							}
							
							wallAtNextPos.setData(new Position2D(x,y-1));//down state
							if(contradicts(kb,wallAtNextPos))
							{
								State<Position2D> state1=new State<>(new Position2D(x,y-1));
								pq.add(state1);
							}
							return pq;
						}
					};
					try {
						searcher.search(searchable);
					} catch (GoalNotFoundException e) {
						e.getMessage();
					}
					Solution<Position2D> solution=searcher.backtrace(searchable.getGoalState(), searchable.getInitialState());
					ArrayList<Predicate<Position2D>> precList=new ArrayList<>();
					solution.getPathToVictory().forEach((state)->precList.add(new SimplePredicate<>("No_Crate_At",state.getLayout())));
					AndPredicate<Position2D> preconditions=new AndPredicate<>(precList);
					Action<Position2D> action=new Action<>("Move_Player1_To", preconditions, new AndPredicate<>(top));
					ArrayList<Action<Position2D>> arr=new ArrayList<>();
					arr.add(action);
					return arr;
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	/*
	 * add 
	 */
	private void updateLists(List<Predicate<Position2D>> toGenerate,List<Predicate<Position2D>> toRemove,Predicate<Position2D>...predicates )
	{
		toRemove.forEach((p)->toGenerate.remove(p));
		toRemove.clear();
		
		for (Predicate<Position2D> predicate : predicates) {
			toGenerate.add(predicate);
			toRemove.add(predicate);
		}
	}
	
	
	@Override
	public boolean satisfies(Predicate<Position2D> p1, Predicate<Position2D> p2) {
		//if p1 -> p2
		if(contradicts(p1, p2)) return false;
		
		if(p1 instanceof AndPredicate)
		{
			if(p2 instanceof SimplePredicate)
			{
				return  ((AndPredicate<Position2D>) p1).getComponents().contains(p2);
			}
			if(p2 instanceof AndPredicate)
			{
				return ((AndPredicate<Position2D>) p1).getComponents().containsAll(((AndPredicate<Position2D>) p2).getComponents());
			}
		}
		return false;
	}
}
