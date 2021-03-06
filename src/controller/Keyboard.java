package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import model.Direction;
import model.Game;

public class Keyboard implements KeyListener{
	private Game game;

	public Keyboard(Game game){
		this.game = game;
	}

	public void updateGame(Game game) {
		this.game = game;
	}

	public void keyPressed(KeyEvent event) {
		int key = event.getKeyCode();
		if (game.getState() == Game.STATE.RUN){ //Only when in the game and not in the menu
			switch (key) {
			case KeyEvent.VK_RIGHT: 
				game.movePlayer(Direction.East);
				break;
			case KeyEvent.VK_LEFT:
				game.movePlayer(Direction.West);
				break;
			case KeyEvent.VK_DOWN:
				game.movePlayer(Direction.South);
				break;
			case KeyEvent.VK_UP:
				game.movePlayer(Direction.North);
				break;
			case KeyEvent.VK_Z:
				game.playerUseWeapon(Direction.North);
				break;
			case KeyEvent.VK_Q:
				game.playerUseWeapon(Direction.West);
				break;
			case KeyEvent.VK_S:
				game.playerUseWeapon(Direction.South);
				break;
			case KeyEvent.VK_D:
				game.playerUseWeapon(Direction.East);
				break;
			case KeyEvent.VK_A:
				game.playerOpenChest();
				break;
			case KeyEvent.VK_E:
				game.playerPickUpItem();
				break;
			case KeyEvent.VK_SHIFT:
				game.playerChangeSpell();
				break;
			case KeyEvent.VK_P:
				game.setState(Game.STATE.PAUSE);
				game.updateWindow();
				break;
			case KeyEvent.VK_ENTER:
				game.changeMap();
				break;
			case KeyEvent.VK_K:
				game.saveGame();
				break;
			case KeyEvent.VK_L:
				game.loadSavedGame();
				break;
			case KeyEvent.VK_O:
				game.changeDrop();
			case 49:
			case 50:
			case 51:
			case 52:
			case 53:
			case 54:
			case 55:
			case 56:
			case 57:
			case 58:
				game.itemAtIndex(key-49); //numbers set items in inventory, begin with 1 to 9
				break;
			case 48: 
				game.itemAtIndex(key-38); // 0 is equal to item #10
				break;
			}
		}
		else if(game.getState() == Game.STATE.CLASS){
			switch(key){
			case 49:
			case 50:
			case 51:
				game.ChooseClass(key-48);
			}
		}
		else if(game.getState() == Game.STATE.PAUSE){
			switch(key){
			case KeyEvent.VK_P:
				game.setState(Game.STATE.RUN);
				game.updateWindow();
				break;
			}
		}
		else if(game.getState() == Game.STATE.STORY){
			switch(key){
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				game.setState(Game.STATE.MENU);
				game.updateWindow();
				break;
			}
		}
		else if(game.getState() == Game.STATE.OVER || game.getState() == Game.STATE.WIN){
			switch(key){
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				game.setState(Game.STATE.MENU);
				game.updateWindow();
				break;
			}
		}

	}
	
	////////////////////////////////////////////
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}

}
