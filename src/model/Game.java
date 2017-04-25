package model;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import model.Player;

import view.*;

import model.GameObject;
import java.util.Random;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Game implements RedrawObservable {
	private ArrayList<GameObject> objects = new ArrayList<GameObject>();
	private ArrayList<RedrawObserver> listRedrawObservers = new ArrayList<RedrawObserver>();
	private Window window;
	private boolean gameRunning = false;
	private Player player = new CP(0, 0, this);
	
	private static final File musicFile = new File(GameObject.class.getResource("/resources/audio/Chant_CP.m4a").getFile());
	private static final Media musicMedia = new Media(musicFile.toURI().toString());
	private static final MediaPlayer musicPlayer = new MediaPlayer(musicMedia);

	private static final boolean bossBool = true;
	Random random = new Random();
	public enum STATE{ //The 2 states for the game
		MENU,
		RUN
	};
	
	public STATE state = STATE.MENU; //Set the initial state to titlescreen
	
	
	
	
	
	public Game(Window window) throws IOException {
		this.window = window;
		window.setGameObjects(objects);
		objects.add(player);
		updateWindow();

	}
	
	public void gameStart(){//Launch the game when NewGame from titleScreen is selected
		if (!gameRunning){
			if (bossBool) {
				loadMap("map_boss.txt");
			} else {
				loadMap("map_1.txt");
			}
			this.gameRunning = true;
			window.setPlayer(this.player);
			musicPlayer.play();
			updateWindow();
		}
	}

	public void setState(STATE state) {
		this.state = state;
	}

	public void removeGameObject(GameObject object) {
		objects.remove(object);
	}

	public ArrayList<GameObject> getGameObjects() {
		return this.objects;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void movePlayer(int xMove, int yMove) {
		if (player.isAlive()) {
			player.tryToMove(xMove, yMove);
		}
	}

	public void playerUseWeapon(int xUseWeapon, int yUseWeapon) {
		if (player.isAlive()) {
			player.useWeapon(xUseWeapon, yUseWeapon);
			updateWindow();
		}
	}

	public void itemAtIndex(int index) { //use item in inventory
		player.checkItemAtIndex(index);
		updateWindow();
	}
	
	public void playerPickUpItem() { //pick up item on the ground
		if (player.isAlive()) {
			player.pickUpItem();
		}
	}
	
	public void playerOpenChest() {
		if (player.isAlive()) {
			player.openChest();
		}
	}
	
	public void playerCastSpell() {
		player.castSpell();
	}
	
	public void playerChangeSpell() {
		player.changeSpell();
	}

	public void updateWindow() {

		if(state == STATE.RUN){
			notifyRedrawObserver();
		} else if (state == STATE.MENU) {
			// System.out.println("update menu");
			window.redrawMenu();
		}
		else if(state == STATE.MENU) {
			window.redrawMenu(); 
			}
	}

	private void loadMap(String fileName) { // Read the MAP.TXT and load every
											// object in the GameObjects list
		try {
			int playerLine = 0;
			int playerColumn = 0;
			ArrayList<Integer> emptyCasesX = new ArrayList<>();
			ArrayList<Integer> emptyCasesY = new ArrayList<>();

			File file = new File(Map.class.getResource("/resources/map/" + fileName).getFile());
			String line = null;
			FileReader fileReader = new FileReader(file);

			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int currentLine = 0;
			while ((line = bufferedReader.readLine()) != null) {
				for (int column = 0; column < line.length(); column++) {
					char c = line.charAt(column);
					switch (c) {
					case '*':
						this.objects.add(new BlockNotBreakable(column, currentLine - 10, this)); // the
																									// -10
																									// is
																									// due
																									// to
																									// parameters
																									// in
																									// the
																									// beginning
																									// of
																									// map
																									// files
																									// (10
																									// lines)
																									/// which
																									// must
																									// NOT
																									// be
																									// taken
																									// into
																									// account
																									// for
																									// positioning
						break;
					case '$':
						this.objects.add(new BlockBreakable(column, currentLine - 10, this));
						break;
					// Read position of the Player
					case 'P':
						playerLine = currentLine - 10;
						playerColumn = column;
						break;
					case 'M':
						this.objects.add(new BlockMoveable(column, currentLine - 10, this));
						break;
					case 'C':
						this.objects.add(new Chest(column, currentLine - 10, this));
						break;
					case '/':
						emptyCasesX.add(column);
						emptyCasesY.add(currentLine - 10);
						break;
					default:
						break;
					}
				}
				player.setPos(playerColumn, playerLine); // set position of the
															// player
				currentLine++;
			}
			bufferedReader.close();
			if (bossBool) {
				loadMobs(emptyCasesX, emptyCasesY, 1);
			} else {
				loadMobs(emptyCasesX, emptyCasesY, 4);
			}

		} catch (FileNotFoundException e) {
			System.out.println("Unable to open file '" + fileName + "'" + e);
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}
	}

	private void loadMobs(ArrayList<Integer> emptyCasesX, ArrayList<Integer> emptyCasesY, int maxMobs) {

		ArrayList<Integer> mobXArray = new ArrayList<>();
		ArrayList<Integer> mobYArray = new ArrayList<>();

		
		for (int i = 0; i < maxMobs; i++) {
			if (emptyCasesX.size() > 0) {
				int randomInt = random.nextInt(emptyCasesX.size()); //int between 0 (inclusive) and size (exclusive)
				int mobX = emptyCasesX.remove(randomInt);
				int mobY = emptyCasesY.remove(randomInt);
				mobXArray.add(mobX);
				mobYArray.add(mobY);
			} else {
				System.out.println("Not enough free cases for " + maxMobs + " mobs");
				break;
			}
		}
		// System.out.println(mobXArray);
		// System.out.println(mobYArray);

		for (int i = 0; i < mobXArray.size(); i++) {
			int posX = mobXArray.get(i);
			int posY = mobYArray.get(i);
			int randomInt = random.nextInt(2);
			try {
				if (bossBool) {
					Boss boss = new Boss(posX, posY, this);
					this.objects.add(boss);
					window.setBoss(boss);
				} else {
					if (randomInt == 0) {
						this.objects.add(new Zombie(posX, posY, i * 1000 / mobXArray.size(), this));
					} else {
						this.objects.add(new Skeleton(posX, posY, i * 1000 / mobXArray.size(), this));
					}
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	
	public void loot(int x, int y, int lootLevel, boolean alwaysLoot) {
		int randomInt = random.nextInt(lootLevel)-10; //int between -10 (inclusive) and lootLevel-10 (exclusive)
		if (alwaysLoot) {
			randomInt+=10;
		}
		int randomIntLimited = Math.min(randomInt, 2);
		Item item = null;
		switch (randomIntLimited) {
		case 0:
			item = new Potion(Potion.potionType.vie, x, y, this);
			break;
		case 1:
			item = new Potion(Potion.potionType.mana, x, y, this);
			break;
		case 2:
			item = new Sword(x, y, this);
			break;
		}
		if (item!=null) {
			this.getGameObjects().add(item);
		}
	}
	

	@Override
	public void addRedrawObserver(RedrawObserver obs) {
		this.listRedrawObservers.add(obs);

	}

	@Override
	public void removeRedrawObserver(RedrawObserver obs) {
		this.listRedrawObservers.remove(obs);

	}

	@Override
	public void notifyRedrawObserver() {
		// System.out.println("Notifying to the redrawObservers a request to
		// redraw");
		for (RedrawObserver ob : listRedrawObservers) {
			ob.redraw(this);
		}
	}

}
