package algorithm;

import java.util.Stack;
/**
 * A class implementing planner, using the STRIPS algorithm.
 * @param <T> - The domain.
 */
public class Strips<T> implements Planner<T> {
	@Override
	public Plan<T> plan(Plannable<T> plannable) {
		Plan<T> plan=new Plan<>();
		Stack<StripsItem<T>> stack=new Stack<>();
		stack.push(plannable.getGoal());
 		while(!stack.isEmpty())
		{
			StripsItem<T> top=stack.peek();
			if(top instanceof AndPredicate)
			{
				((AndPredicate<T>) top).getComponents().forEach((p)->{
					if(!plannable.kbSatisfies(p)) {stack.push(p);}
				});
			}
			else if(top instanceof SimplePredicate)
			{
				Predicate<T> predTop=(Predicate<T>) top;
				if(!plannable.kbSatisfies(predTop))
				{
					Action<T> act=plannable.getSatisfyingAction(predTop);
					stack.push(act);
					act.getPreconditions().getComponents().forEach((p)->stack.push(p));
				}
			}
			if(top instanceof Action)
			{
				stack.pop();
				if(!(top instanceof LinkAction))
				{
					Action<T> actionTop=(Action<T>) top;
					plannable.updateKb(actionTop.getEffects());
					plan.add(actionTop);
				}
				else//top instanceof LinkAction
				{
					LinkAction<T> linkActionTop=(LinkAction<T>) top;
					linkActionTop.getActions().forEach((a)->stack.push(a));
				}
			}
			else if(plannable.kbSatisfies((Predicate<T>) top))
			{
				stack.pop();
			}
		}
		return plan;
	}
}
