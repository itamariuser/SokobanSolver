package algorithm;

import java.util.ArrayList;
import java.util.List;

public class AndPredicate<T> extends ComplexPredicate<T> {
	public AndPredicate(String name, List<Predicate<T>> components) {
		super(name, components);
	}
	@SafeVarargs
	public AndPredicate(Predicate<T>... preds) {
		super("Default AndPredicate",new ArrayList<Predicate<T>>());
		for (Predicate<T> predicate : preds) {
			this.add(predicate);
		}
	}
	
	public AndPredicate(AndPredicate<T> pred)
	{
		super(pred.name, pred.components);
	}
	
	public AndPredicate(List<Predicate<T>> preds) {
		super("Default AndPredicate",new ArrayList<Predicate<T>>());
		for (Predicate<T> predicate : preds) {
			this.add(predicate);
		}
	}
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for (Predicate<T> predicate : components) {
			sb.append("\n"+predicate.toString()+"\n");
		}
		return "** 'And' PREDICATE, Name: "+this.name+", preds:"+sb.toString();
	}
}
