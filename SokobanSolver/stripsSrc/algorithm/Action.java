package algorithm;

import java.util.ArrayList;

import model.data.Position2D;

public class Action<T> extends StripsItem<T> {
	

	protected AndPredicate<T> preconditions;
	
	protected AndPredicate<T> effects;
	
	
	public AndPredicate<T> getPreconditions() {
		return preconditions;
	}


	public void setPreconditions(AndPredicate<T> preconditions) {
		this.preconditions = preconditions;
	}


	public AndPredicate<T> getEffects() {
		return effects;
	}
	
	public void addEffect(SimplePredicate<T> effect)
	{
		if(this.effects==null)
		{
			this.effects=new AndPredicate<>();
		}
		this.effects.add(effect);
	}
	
	
	public void addPrecondition(SimplePredicate<T> prec)
	{
		if(this.preconditions==null)
		{
			this.preconditions=new AndPredicate<>();
		}
		this.preconditions.add(prec);
	}

	public void setEffects(AndPredicate<T> effects) {
		this.effects = effects;
	}
	
	public void setPreconditions(SimplePredicate<T> precondition) {
		AndPredicate<T> and=new AndPredicate<>(precondition);
		this.preconditions=and;
	}
	
	
	public void setEffects(SimplePredicate<T> effect) {
		AndPredicate<T> and=new AndPredicate<>(effect);
		this.effects=and;
	}


	public Action(String name) {
		super(name);
	}
	
	public Action(String name,ArrayList<String> sub) {
		super(name);
	}



	@Override
	public String toString() {
		return "Name: "+this.name+"\n Preconditions: "+this.preconditions.toString()+"\n Effects: "+this.effects.toString();
	}
	public Action(String name,AndPredicate<T> preconditions,AndPredicate<T> effects) {
		super("DEFAULT");
		this.preconditions=preconditions;
		this.effects=effects;
		this.name=name;
	}
}
