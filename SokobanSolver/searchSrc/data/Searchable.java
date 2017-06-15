package data;

import java.util.Queue;

public interface Searchable<T> {
	State<T> getInitialState();
	State<T> getGoalState();
	Queue<State<T>> getAllPossibleStates(State<T> s);
	//int getCostBetween(State<T> src, State<T> dest);
	//Action<T> getActionBetween(State<T> src, State<T> dest);
}

//
//public class Action<T>
//{
//	int cost;
//	src;
//	dest;
//	public int getCost
//	{
//		return cost;
//	}
//	
//}
