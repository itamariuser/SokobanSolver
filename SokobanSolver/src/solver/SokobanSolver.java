package solver;

import java.util.List;

import algorithm.Strips;
import commons.Level2D;
import model.data.Position2D;

public class SokobanSolver {
	
	List<String> solveLevel(Level2D level)
	{
		Strips<Position2D> strips=new Strips<>();
		LevelPlannable plannable=new LevelPlannable(level);
		strips.plan(plannable);
		return null;
		
	}
}
