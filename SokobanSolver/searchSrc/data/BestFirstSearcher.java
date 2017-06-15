package data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

public class BestFirstSearcher<T> extends CommonSearcher<T> {
	
	private HashSet<State<T>> closed;
	HashMap<State<T>,Integer> distances;

	public BestFirstSearcher() {
		 closed = new HashSet<State<T>>();
		 distances=new HashMap<>();
		 openList=new PriorityQueue<>(new Comparator<State<T>>() {
			@Override
			public int compare(State<T> arg0, State<T> arg1) {
				return distances.get(arg0)-distances.get(arg1);
			}
		});
	}

	@Override
	public Solution<T> search(Searchable<T> s) throws GoalNotFoundException {
		openList.add(s.getInitialState());
		
		while (!openList.isEmpty())
		{
			State<T> state = openList.poll();
			closed.add(state);
			if (state.equals(s.getGoalState())) 
			{
				return backtrace(s.getGoalState(), s.getInitialState());
			}
			else 
			{
				Queue<State<T>> nextStates = s.getAllPossibleStates(state);
				for (State<T> tempState : nextStates)
				{
					if (!closed.contains(tempState) && !openList.contains(tempState)) 
					{
						tempState.setCameFromState(state);
						openList.add(tempState);
					}
					else if(distances.get(tempState)>distances.get(state)+tempState.getCostFromParent())//if the new path to tempState is better than the previous path to tempState
					{
						if(!openList.contains(tempState))
						{
							openList.add(tempState);
						}
						else
						{
							openList.remove(tempState);
							distances.put(tempState, distances.get(state)+tempState.getCostFromParent());
							openList.add(tempState);
						}
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
		do {
			pathToVictory.add(tempState);
			cost++;
			tempState = tempState.getCameFromState();
		} while (tempState.getCameFromState() != null);
		return new Solution<>(pathToVictory,cost);
	}
}
