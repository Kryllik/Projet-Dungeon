package Main;

import controller.Keyboard;
import model.Game;
import view.*;

public class Main {
	public static void main(String[] args) {
		System.out.println("hello");
		Window window = new Window();
		window.update();

		// Game game = new Game(window);
		// Keyboard keyboard = new Keyboard(game);
		// window.setKeyListener(keyboard);

		//System.out.println(Map.loadMap("map_1.txt"));

	}
}
