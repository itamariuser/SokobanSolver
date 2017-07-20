package algorithm;

import java.util.LinkedList;

import model.data.Position2D;
//For consecutive actions
public class LinkAction<T> extends Action<T>{
	LinkedList<Action<T>> actions;
	public LinkAction(String name) {
		super(name);
		actions=new LinkedList<Action<T>>();
	}
	public LinkedList<Action<T>> getActions() {
		return actions;
	}
	public void setActions(LinkedList<Action<T>> actions) {
		this.actions = actions;
	}
	
}
