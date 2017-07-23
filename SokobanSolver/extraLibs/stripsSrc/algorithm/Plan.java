package algorithm;

import java.util.ArrayList;
import java.util.List;
/**
 * A class containing a list of actions for solving a plannable.
 * @param <T> - The domain.
 */
public class Plan<T> {
	List<Action<T>> actions;

	public Plan() {
		actions=new ArrayList<>();
	}
	
	public List<Action<T>> getActions() {
		return actions;
	}

	public void setActions(List<Action<T>> actions) {
		this.actions = actions;
	}
	
	public void add(Action<T> action)
	{
		actions.add(action);
	}
	
	public void remove(Action<T> action)
	{
		actions.remove(action);
	}

	public Plan(List<Action<T>> actions) {
		super();
		this.actions = actions;
	}
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		int i=0;
		for (Action<T> action : actions) {
			sb.append("\nAction: "+(++i)+action.toString());
		}
		return sb.toString();
	}
	
	
}
