package algorithm;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Strips<T> implements Planner<T> {
	@Override
	public Plan<T> plan(Plannable<T> plannable) {
		LinkedList<Action<T>> actionsToDo=new LinkedList<Action<T>>();
		Stack<Predicate<T>> stack = new Stack<>();
		stack.push(plannable.getGoal());
		
		while(!stack.isEmpty())
		{
			System.out.println(stack);
			Predicate<T> top=stack.peek();
			if(!(top instanceof Action))
			{
				//System.out.println(!plannable.getKnowledgebase().satisfies(top));
				
				if(!plannable.satisfies(plannable.getKnowledgebase(), top))
				{
					if(top instanceof AndPredicate)//if complex then unpack components to stack
					{
						//TODO: add a way to decide order of insertion (comparator)
						AndPredicate<T> andPred=(AndPredicate<T>)top;
						for (Predicate<T> predicate : andPred.getComponents()) {
							stack.push(predicate);
						}
					}
					else if(top instanceof SimplePredicate)//
					{
						stack.pop();
						Action<T> action=plannable.getSatisfyingAction(top);//Change to a set of actions
						//TODO: 
						stack.push(action);
						stack.push(action.getPreconditions());
					}
				}
				else//satisfied
				{
					stack.pop();
				}
			}
			else//top is an action
			{
				stack.pop();//remove from top
				Action<T> a=(Action<T>) top;
				plannable.getKnowledgebase().update(a.getEffects());
				actionsToDo.add(a);
			}
		}
		return new Plan<T>(actionsToDo);
	}


}
