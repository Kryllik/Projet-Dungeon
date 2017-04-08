package model;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Skeleton extends Mob {

	public Skeleton(int x, int y, Game game) throws IOException {
		super(x, y, game, ImageIO.read(new File(GameObject.class.getResource("/resources/sprites/Skeleton.jpg").getFile())));
		
	}

}
