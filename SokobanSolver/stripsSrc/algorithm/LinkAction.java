package algorithm;

import java.util.ArrayList;
//For consecutive actions
public class LinkAction<T> extends Action<T>{
	ArrayList<Action<T>> actions;
	public LinkAction(String name) {
		super(name);
		actions=new ArrayList<Action<T>>();
	}
	public ArrayList<Action<T>> getActions() {
		return actions;
	}
	public void setActions(ArrayList<Action<T>> actions) {
		this.actions = actions;
	}
	
}
