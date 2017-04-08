package model;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import CONSTANTS.CONSTANTS;

public class Player extends Character {
	
	private boolean alive = true;
	
	public Player(int x, int y, Game game) throws IOException {
		super(x, y, game, ImageIO.read(new File(GameObject.class.getResource("/resources/sprites/Hugues_Head.jpg").getFile())));
		this.itemType = 1;
	}

	@Override
	public boolean isObstacle() {
		return true;
	}
	
	public void changeTool() {
		if (this.itemType == 1) {
			this.itemType = 2;
			System.out.println("pickaxe");
		} else {
			this.itemType = 1;
			System.out.println("weapon");
		}
	}
	
	public void hit(int xHit,int yHit) {
		
		switch (this.itemType) {
			case 1: this.attack(xHit, yHit);
				break;
			case 2: this.mine(xHit,yHit);
				break;
		}
	}
	
	public void attack(int xAttack,int yAttack) {
		int newPosX = this.posX+CONSTANTS.BLOCK_SIZE*xAttack;
		int newPosY = this.posY+CONSTANTS.BLOCK_SIZE*yAttack;
		
		for (GameObject object:this.getGame().getGameObjects()) {
			if (object.getPosX() == newPosX && object.getPosY() == newPosY) {
				if (object instanceof Mob) {
					System.out.println("mob attacked");
					Mob mob = (Mob) object;
					mob.wasHit();
					break;
				}
			}
		}
	}
	
	public void wasHit() {
		this.health--;
		System.out.println("HEALTH : " + this.health + "\n");
		if (this.health<=0) {
			die();
		}
	}
	
	public boolean isAlive() {
		return this.alive;
	}
	
	public void die() {
		this.alive = false;
		this.getGame().getGameObjects().remove(this);
		this.getGame().updateWindow();
		System.out.println("GAME OVER-------GET REKT-------YOU MAD BRO??");
	}
	
	
	public void mine(int xMine,int yMine) {
		int newPosX = this.posX+CONSTANTS.BLOCK_SIZE*xMine;
		int newPosY = this.posY+CONSTANTS.BLOCK_SIZE*yMine;
		
		for (GameObject object:this.getGame().getGameObjects()) {
			if (object.getPosX() == newPosX && object.getPosY() == newPosY) {
				if (object instanceof BlockBreakable) {
					System.out.println("break");
					BlockBreakable block = (BlockBreakable) object;
					block.toBreak();
					break;
				}
			}
		}
	}
	
	
}
