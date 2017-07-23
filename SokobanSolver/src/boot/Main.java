package boot;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import algorithm.Strips;
import common.Level2D;
import common.Position2D;
import model.data.TextLevel2DLoader;
import solver.LevelPlannable;
import solver.SokobanSolver;

public class Main {
	/**
	 * An example of using the sokoban solver to solve a level and printing the solution.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)  {
		TextLevel2DLoader loader=new TextLevel2DLoader();
		Level2D level=null;
		try {
			level = (Level2D) loader.loadLevelFromStream(new FileInputStream(new File("./levels/level 4.txt")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		LevelPlannable p=new LevelPlannable(level);
		
		Strips<Position2D> strips=new Strips<>();
		strips.plan(p);
		
		SokobanSolver ss=new SokobanSolver();
		List<String> sol=ss.solveLevel(level);
		for (String string : sol) {
			System.out.println(string);
		}
	}
}
