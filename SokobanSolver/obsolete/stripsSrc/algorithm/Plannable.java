package algorithm;

import java.util.List;

import model.data.Position2D;

public interface Plannable<T> {
	public Predicate<T> getGoal();
	public AndPredicate<T> getKnowledgebase();
	public List<Action<T>> getSatisfyingActions(Predicate<T> top);
	public Action<T> getSatisfyingAction(Predicate<T> top);
	public boolean contradicts(Predicate<T> pred1,Predicate<T> pred2);
	public boolean satisfies(Predicate<T> pred1,Predicate<T> pred2);
	public boolean kbSatisfies(Predicate<T> pred);
	public void updateKb(AndPredicate<T> effects);
	public boolean kbContradicts(Predicate<Position2D> p2);
}
