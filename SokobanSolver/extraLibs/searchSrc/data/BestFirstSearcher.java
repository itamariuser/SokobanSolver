//package data;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.PriorityQueue;
//
//import model.data.Position2D;
//
//public class BestFirstSearcher<T> extends CommonSearcher<T> {
//	
//	private HashSet<State<T>> closed;
//	HashMap<State<T>,Integer> distances;
//	HashMap<State<T>,State<T>> cameFromStates=new HashMap<>();
//
//	public BestFirstSearcher() {
//		 closed = new HashSet<State<T>>();
//		 distances=new HashMap<>();
//		 openList=new PriorityQueue<>(new Comparator<State<T>>() {
//			@Override
//			public int compare(State<T> arg0, State<T> arg1) {
//				if(distances.get(arg0)==null)
//				{
//					System.out.println();
//				}
//				if(distances.get(arg1)==null)
//				{
//					System.out.println();
//				}
//				return distances.get(arg0)-distances.get(arg1);
//			}
//		});
//	}
//	
//	private boolean containsData(Collection<State<T>> list,State<T> s)
//	{
//		for (State<T> state : list) {
//			if(s.getLayout().equals(state.getLayout()))
//				return true;
//		}
//		return false;
//	}
//	private Integer getEqualData(HashMap<State<T>,Integer> map,State<T> state)
//	{
//		if(state.getLayout().equals(new Position2D(1,2)))
//		{
//			System.out.println();
//		}
//		for (State<T> s1 : map.keySet()) {
//			if(s1.getLayout().equals(state.getLayout())){
//				return map.get(state);
//			}
//		}
//		
//		return null;
//	}
//	
//	@Override
//	public Solution<T> search(Searchable<T> s) throws GoalNotFoundException {
//		State<T> initialState=s.getInitialState();
//		State<T> goalState=s.getGoalState();
//		
//		openList.add(initialState);
//		distances.put(initialState, 0);
//		while (!openList.isEmpty())
//		{
//			State<T> state = openList.poll();
//			closed.add(state);
//			if (state.equals(goalState)) 
//			{
//				return backtrace(state, initialState);
//			}
//			else 
//			{
//				if(state.getLayout().equals(new Position2D(3,3)))
//				{
//					System.out.println();
//				}
//				PriorityQueue<State<T>> nextStates = s.getAllPossibleStates(state);
//				for (State<T> tempState : nextStates)
//				{
//					
//					boolean eq1=containsData(closed, tempState);
//					if(!eq1)
//					{
//						if(tempState.getLayout().equals(new Position2D(1,2)))
//						{
//							System.out.println(tempState);
//						}
//						
//						boolean eq2=containsData(openList, tempState);
//						if (!eq2) 
//						{
//							cameFromStates.put(tempState, state);
//							tempState.setCameFromState(state);
//							distances.put(tempState, distances.get(state)+tempState.getCostFromParent());
//							openList.add(tempState);
//						}
//						//else if(getEqualData(distances, tempState)> getEqualData(distances, state) + tempState.getCostFromParent())//if the new path to tempState is better than the previous path to tempState
//						else 
//						{
//							if(distances.get(tempState)==null)
//							{
//								distances.put(tempState,Integer.MAX_VALUE/2);
//							}
//							int d1=distances.get(tempState);
//							int d2=distances.get(state);
//							int d3=tempState.getCostFromParent();
//							if(d1>d2+d3)
//							{
//								if(!openList.contains(tempState))
//								{
//									openList.add(tempState);
//								}
//								else
//								{
//									openList.remove(tempState);
//									distances.put(tempState, distances.get(state)+tempState.getCostFromParent());
//									openList.add(tempState);
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		throw new GoalNotFoundException();
//	}
//
//	@Override
//	public Solution<T> backtrace(State<T> goalState,State<T> initialState) {
//		ArrayList<State<T>> pathToVictory = new ArrayList<State<T>>();
//		int cost=0;
//		State<T> tempState = goalState;
//		do
//		{
//			pathToVictory.add(tempState);
//			cost++;
//			tempState.setCameFromState(s(cameFromStates, tempState));
//			tempState=s(cameFromStates, tempState);
//			if(tempState.equals(initialState))
//			{
//				pathToVictory.add(initialState);
//			}
//		}while(s(cameFromStates, tempState)!=null);
//		return new Solution<>(pathToVictory,cost);
//	}
//	
//	private State<T> s(HashMap<State<T>,State<T>> fck, State<T> ths)
//	{
//		for (State<T> state : fck.keySet()) {
//			if(ths.equals(state))
//			{
//				return cameFromStates.get(state);
//			}
//		}
//		return null;
//	}
//}
package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import common.Position2D;

public class BestFirstSearcher<T> extends CommonSearcher<T> {
	
	private HashSet<State<T>> closed;
	HashMap<State<T>,Integer> distances;
	HashMap<State<T>,State<T>> cameFromStates=new HashMap<>();
	State<T> goalState;
	public BestFirstSearcher() {
		 closed = new HashSet<State<T>>();
		 distances=new HashMap<>();
		 openList=new PriorityQueue<>(new Comparator<State<T>>() {
			@Override
			public int compare(State<T> arg0, State<T> arg1) {
				if(distances.get(arg0)==null)
				{
					System.out.println();
				}
				if(distances.get(arg1)==null)
				{
					System.out.println();
				}
//				return distances.get(arg0)-distances.get(arg1);
				return evalDist(arg0)-evalDist(arg1);
			}
		});
	}
	@SuppressWarnings("unchecked")
	private int evalDist(State<T> state)
	{
		if(state.getLayout().equals(new Position2D(3,3)) && goalState.getLayout().equals(new Position2D(5,3)))
		{
			System.out.println();
		}
		State<Position2D> s1=(State<Position2D>) state;
		State<Position2D> goalS=(State<Position2D>) goalState;
		return Math.abs(s1.getLayout().getX()-goalS.getLayout().getX()) +
				Math.abs(s1.getLayout().getY()-goalS.getLayout().getY());
	}
	
	private boolean containsData(Collection<State<T>> list,State<T> s)
	{
		for (State<T> state : list) {
			if(s.getLayout().equals(state.getLayout()))
				return true;
		}
		return false;
	}
	private Integer getEqualData(HashMap<State<T>,Integer> map,State<T> state)
	{
		if(state.getLayout().equals(new Position2D(1,2)))
		{
			System.out.println();
		}
		for (State<T> s1 : map.keySet()) {
			if(s1.getLayout().equals(state.getLayout())){
				return map.get(state);
			}
		}
		
		return null;
	}
	
	@Override
	public Solution<T> search(Searchable<T> s) throws GoalNotFoundException {
		State<T> initialState=s.getInitialState();
		goalState=s.getGoalState();
		
		openList.add(initialState);
		distances.put(initialState, 0);
		while (!openList.isEmpty())
		{
			State<T> state = openList.poll();
			closed.add(state);
			if (state.equals(goalState)) 
			{
				return backtrace(state, initialState);
			}
			else 
			{
				if(state.getLayout().equals(new Position2D(3,3)))
				{
					System.out.println();
				}
				PriorityQueue<State<T>> nextStates = s.getAllPossibleStates(state);
//				boolean b=true;
//				for (State<T> state2 : nextStates) {
//					if(!containsData(openList, state2))
//					{
//						b=false;
//					}
//				}
//				if(b==true)
//				{
//					closed.add(state);
//				}
				for (State<T> tempState : nextStates)
				{
					boolean eq1=containsData(closed, tempState);
					if(!containsData(closed, tempState) && !containsData(openList, tempState))
					{
							cameFromStates.put(tempState, state);
							tempState.setCameFromState(state);
							distances.put(tempState, Integer.MAX_VALUE/2);
							openList.add(tempState);
					}
					else 
					{	
						if(distances.get(tempState)==null)
						{
							distances.put(tempState, Integer.MAX_VALUE/2);
						}
						int d1=distances.get(tempState);
						int d2=distances.get(state);
						int d3=tempState.getCostFromParent();
						if(d1>d2+d3)
						{
							distances.put(tempState, d2+d3);
							if(!containsData(openList, tempState))
							{
								openList.add(tempState);
							}
						}
//						int d1=distances.get(tempState);
//						int d2=distances.get(state);
//						int d3=tempState.getCostFromParent();
//						if(d1>d2+d3)
//						{
//							if(!openList.contains(tempState))
//							{
//								openList.add(tempState);
//							}
//							else
//							{
//								openList.remove(tempState);
//								distances.put(tempState, distances.get(state)+tempState.getCostFromParent());
//								openList.add(tempState);
//							}
//						}
					}
				}
			}
		}
		throw new GoalNotFoundException();
	}
	
	@Override
	public Solution<T> backtrace(State<T> goalState,State<T> initialState) {
		ArrayList<State<T>> pathToVictory = new ArrayList<State<T>>();
		int cost=0;
		State<T> tempState = goalState;
		do
		{
			pathToVictory.add(tempState);
			cost++;
			tempState.setCameFromState(s(cameFromStates, tempState));
			tempState=s(cameFromStates, tempState);
			if(tempState.equals(initialState))
			{
				pathToVictory.add(initialState);
			}
		}while(s(cameFromStates, tempState)!=null);
		return new Solution<>(pathToVictory,cost);
	}
	
	private State<T> s(HashMap<State<T>,State<T>> fck, State<T> ths)
	{
		for (State<T> state : fck.keySet()) {
			if(ths.equals(state))
			{
				return cameFromStates.get(state);
			}
		}
		return null;
	}
}
