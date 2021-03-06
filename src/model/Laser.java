package model;

import java.io.File;


public class Laser extends Mob implements IObstacle {
	private static final long serialVersionUID = 42L;

	private int waitTime;
	private int liveTime;
	private int time = 0;

	public Laser(int x, int y, Game game, File file,int phase) {
		super(x, y, game, Sprite.makeSpriteList(file,0,0,11),10000,false);
		this.liveTime = 500/phase;
		this.waitTime = 300/phase;
	}
	
	@Override
	public void run(){
		try{
			Player player = this.getGame().getPlayer();
			while(player.isAlive()){
				if (game.getState() == Game.STATE.RUN) {
					
					attackPattern();
					
					if (time>liveTime) {
						this.die();
						this.getGame().updateWindow();
					}
					Thread.sleep(waitTime);
					time+=waitTime;
				}
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}; 
	}
	
	
	@Override
	public boolean isObstacle() {
		return false;
	}
	
	@Override
	public void attackPattern() {
		attack(0,0);
	}
	
	@Override
	public void loot() {
		this.getGame().loot(this.getPosX(), this.getPosY(), -10);
	}

	@Override
	public void removeAttackSprites() {
		
	}
	
}
