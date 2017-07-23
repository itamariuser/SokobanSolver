package solver;

import java.util.ArrayList;
import java.util.List;

import algorithm.Action;
import algorithm.Plan;
import algorithm.Strips;
import common.Level2D;
import common.Position2D;


public class SokobanSolver {
	/**
	 * Returns a solution for a given level.
	 * @param level - The level to be solved.
	 * @return A list of commands represented in strings.
	 */
	public List<String> solveLevel(Level2D level)
	{
		ArrayList<String> strings=new ArrayList<>();
		Strips<Position2D> strips=new Strips<>();
		LevelPlannable plannable=new LevelPlannable(level);
		Plan<Position2D> plan=strips.plan(plannable);
		for (Action<Position2D> action : plan.getActions()) {
			switch(action.getName())
			{
				case("Move_Right"): strings.add("r"); break;
				case("Move_Left"): strings.add("l"); break;
				case("Move_Down"): strings.add("d"); break;
				case("Move_Up"): strings.add("u"); break;
				default: break;
			}
		}
		return strings;
	}
}
