package boot;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import algorithm.Strips;
import data.BestFirstSearcher;
import gameObjects.Position2D;
import model.TextLevel2DLoader;
import solver.SokobanSolver;

public class Main {

	public static void main(String[] args) throws FileNotFoundException  {
		TextLevel2DLoader loader=new TextLevel2DLoader();
		try{
			Strips<Position2D> strips=new Strips<>();
			strips.plan(new SokobanSolver(loader.loadLevelFromStream(new FileInputStream("/levels/level1")), new BestFirstSearcher<>()));
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
