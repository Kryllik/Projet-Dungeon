package model;

import java.io.File;

public class Chest extends Block implements IOpenable{
	
	private static final File spriteFile = new File(GameObject.class.getResource("/resources/sprites/Chest.png").getFile());

	public Chest(int x, int y, Game game) {
		super(x, y, game, Sprite.makeSpriteList(spriteFile,0,0,0));
	}
	
	public synchronized void open() {
		this.getGame().loot(this.posX, this.posY, 10);
		this.getGame().getGameObjects().remove(this);
	}
	
	@Override
	public boolean isOpenable() {
		return true;
	}

}
