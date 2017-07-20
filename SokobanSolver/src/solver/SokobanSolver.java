package solver;

import java.util.ArrayList;
import java.util.List;

import algorithm.Action;
import algorithm.Strips;
import commons.Level2D;
import model.data.Position2D;

public class SokobanSolver {
	
	public List<String> solveLevel(Level2D level)
	{
		ArrayList<String> strings=new ArrayList<>();
		Strips<Position2D> strips=new Strips<>();
		LevelPlannable plannable=new LevelPlannable(level);
		for (Action<Position2D> action : strips.plan(plannable).getActions()) {
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
