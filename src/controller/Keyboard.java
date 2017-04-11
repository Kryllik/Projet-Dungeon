package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import model.Game;

public class Keyboard implements KeyListener{
	private Game game;
	
	public Keyboard(Game game){
		this.game = game;
	}

	//@Override
	public void keyPressed(KeyEvent event) {
		int key = event.getKeyCode();
		//System.out.println(key);
		switch (key){
			case KeyEvent.VK_RIGHT: 
				game.movePlayer(1, 0); /* TODO: replace all with direction enum */
				break;
			case KeyEvent.VK_LEFT:
				game.movePlayer(-1, 0);
				break;
			case KeyEvent.VK_DOWN:
				game.movePlayer(0, 1);
				break;
			case KeyEvent.VK_UP:
				game.movePlayer(0, -1);
				break;
			case KeyEvent.VK_Z:
				System.out.println("key hit up");
				game.playerUseTool(0, -1);
				break;
			case KeyEvent.VK_Q:
				System.out.println("key hit left");
				game.playerUseTool(-1, 0);
				break;
			case KeyEvent.VK_S:
				System.out.println("key hit down");
				game.playerUseTool(0, 1);
				break;
			case KeyEvent.VK_D:
				System.out.println("key hit right");
				game.playerUseTool(1, 0);
				break;
			case KeyEvent.VK_A:
				game.playerChangeTool();
				/*
			case KeyEvent.VK_SPACE:
				game.dropBomb("nuke", player1);
				break;
			case KeyEvent.VK_B:
				game.dropBomb("bomb", player1);
				*/
		}
		
	}

	//@Override
	public void keyTyped(KeyEvent e) {
	}

	//@Override
	public void keyReleased(KeyEvent e) {
	}
	public void test(){
		
	}
}
