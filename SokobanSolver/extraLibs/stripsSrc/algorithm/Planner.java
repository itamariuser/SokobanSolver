package algorithm;
/**
 * An interface for classes which can solve a plannables
 * @param <T> - The domain.
 */
public interface Planner<T> {
	/**
	 * Solves a plannable.
	 * @param plannable - The plannable to solve.
	 * @return A plan for the given level.
	 */
	public Plan<T> plan(Plannable<T> plannable);
}
