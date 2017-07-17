package solver;

import java.util.List;

import algorithm.Strips;
import commons.Level2D;
import model.data.Position2D;
import model.policy.MySokobanPolicy;

public class SokobanSolver {
	
	List<String> solveLevel(Level2D level)
	{
		Strips<Position2D> strips=new Strips<>();
		LevelPlannable plannable=new LevelPlannable(level, new MySokobanPolicy());
		strips.plan(plannable);
		return null;
		
	}
}
