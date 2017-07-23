package algorithm;

import java.util.List;

import common.Position2D;
/**
 * An interface for classes which can be solved using a planner.
 * @param <T> - The domain.
 */
public interface Plannable<T> {
	
	/** @return The goal predicate.	 */
	public Predicate<T> getGoal();
	
	/** @return The plannable's knowledge base. */
	public AndPredicate<T> getKnowledgebase();
	
	/**
	 * Generate a list of possible actions which satisfy the target predicate.
	 * @param target - The target predicate.
	 * @return A list of possible actions which satisfy the target predicate.
	 */
	public List<Action<T>> getSatisfyingActions(Predicate<T> target);
	
	/**
	 * Find a single action which satisfies the target predicate.
	 * @param target - The target predicate.
	 * @return An action which satisfies the target predicate.
	 */
	public Action<T> getSatisfyingAction(Predicate<T> target);
	
	/**
	 * Returns whether the left predicate <b>contradicts</b> the right predicate, meaning <b>p1 -> !p2</b>
	 * This might be changed according to the game rules.
	 * @param p1 - The left side predicate.
	 * @param p2 - The right side predicate.
	 * @return true if p1 contradicts p2, false otherwise.
	 */
	public boolean contradicts(Predicate<T> pred1,Predicate<T> pred2);
	
	/**
	 * Returns whether the left predicate <b>satisfies</b> the right predicate, meaning <b>p1 -> p2</b>
	 * This might be changed according to the game rules.
	 * @param p1 - The left side predicate.
	 * @param p2 - The right side predicate.
	 * @return true if p1 satisfies p2, false otherwise.
	 */
	public boolean satisfies(Predicate<T> pred1,Predicate<T> pred2);
	
	/**
	 * Returns whether the knowledge base <B>satisfies</b> a predicate.
	 * @param p - The predicate to be checked.
	 * @return true if <B>kb satisfies p</B>, false otherwise.
	 */
	public boolean kbSatisfies(Predicate<T> p);
	
	/**
	 * Returns whether the knowledge base <B>contradicts</b> a predicate.
	 * @param p - The predicate to be checked.
	 * @return true if <B>kb contradicts p</B>, false otherwise.
	 */
	public boolean kbContradicts(Predicate<Position2D> p2);
	
	/**
	 * Simulates the execution of effects on the knowledge base.
	 * The knowledge base will change based on the effects.
	 * @param effects - The effects to be simulated.
	 */
	public void updateKb(AndPredicate<T> effects);
}
