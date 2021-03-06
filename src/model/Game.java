package model;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.Serializable;

import model.Player;

import view.*;

import model.GameObject;
import java.util.Random;

import CONSTANTS.CONSTANTS;
import Main.Main;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Game implements RedrawObservable, Serializable {
	private static final long serialVersionUID = 42L;

	private ArrayList<RedrawObserver> listRedrawObservers = new ArrayList<RedrawObserver>();
	private Window window;

	private int mapCounter = 1;
	private ArrayList<Integer> roomsDone = new ArrayList<Integer>();
	private int savedBlockSize = 0;
	private boolean savedDarkness = false;
	private int savedLineOfSight = 3;
	private boolean drop = false;

	private Player player;

	private static final File musicFile = new File(GameObject.class.getResource("/resources/audio/Chant_CP.m4a").getFile());
	private static final Media musicMedia = new Media(musicFile.toURI().toString());
	private static final transient MediaPlayer musicPlayer = new MediaPlayer(musicMedia);
	private static final File uneMineFile = new File(GameObject.class.getResource("/resources/audio/Une_Mine.wav").getFile());
	private static final Media uneMineMedia = new Media(uneMineFile.toURI().toString());
	private static final transient MediaPlayer uneMinePlayer = new MediaPlayer(uneMineMedia);

	private static boolean bossBool = false;
	Random random = new Random();

	private ArrayList<GameObject> objects = new ArrayList<GameObject>();

	public enum STATE{ //The states for the game
		MENU,
		CLASS,
		RUN,
		OVER,
		WIN,
		PAUSE,
		STORY,
	};

	private STATE state = STATE.MENU; //Set the initial state to titlescreen

	public Game(Window window) throws IOException {
		gameInit(window);
	}

	public void gameInit(Window window) {
		this.window = window;
		window.setGameObjects(objects);
		updateWindow();
		setMusicPlayer();
		Weapon.setSwordMediaPlayer();
	}

	public synchronized void ChooseClass(int c){ //is called when in the menu to choose the class
		switch(c){
		case 1:
			player = new CP(0, 0, this);
			Penne.initSprites(0);
			break;
		case 2 :
			player = new CM(0, 0, this);
			Penne.initSprites(1);
			break;
		case 3:
			player = new CS(0, 0, this);
			Penne.initSprites(2);
			break;
		}
		objects.add(player); //The 1st object of the list is the player in order to handle its position in the list
		this.gameStart(false);
	}

	public void gameStart(boolean save){//Launch the game when NewGame from titleScreen is selected
		if ((state != STATE.RUN)||(save)){
			if (save) {
				relaunchThreads();
			} else {
				if (bossBool) {
					loadMap("map_boss.txt");
				} else {
					loadMap("map_0.txt");
				}
				this.setState(STATE.RUN);
			}
			window.setPlayer(this.player);
			musicPlayer.play();
			updateWindow();
		}
	}

	public void relaunchThreads() {
		ArrayList<GameObject> clone = (ArrayList<GameObject>) objects.clone();
		for(GameObject object: clone){
			if (object.isAttackable()) {
				Mob mob = (Mob) object;
				mob.relaunch();
			}
		}
	}

	public synchronized void removeGameObject(GameObject object) {
		objects.remove(object);
	}

	public void setMusicPlayer() {
		musicPlayer.setVolume(0.7);
		musicPlayer.setOnEndOfMedia(new Runnable() {
			public void run() {
				musicPlayer.seek(Duration.ZERO);
			}
		});
	}

	public void movePlayer(Direction direction) {
		if (player.isAlive()) {
			player.tryToMove(direction);
		}
	}

	public void playerUseWeapon(Direction direction) {
		if (player.isAlive()) {
			player.useWeapon(direction);
			updateWindow();
		}
	}

	public void itemAtIndex(int index) { //use item in inventory
		player.selectItemAtIndex(index,drop);
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
		}
		else if(state == STATE.MENU) {
			window.redrawMenu(); 
		}
		else if(state == STATE.CLASS){
			window.redrawClass();
		}
		else if(state == STATE.OVER){
			window.redrawGameOver();
		}
		else if(state == STATE.STORY){
			window.redrawStory();
		}
		else if(state == STATE.WIN){
			window.redrawWin();
		}
	}

	public void updateWindow(Window window) { //Overcharge when loading the game from a save
		this.window = window;
	}

	public void saveGame() {
		Main.save(this);
	}

	public void loadSavedGame() {
		try {
			Main.loadRunning(window);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void interruptThreads() {
		ArrayList<GameObject> clone = (ArrayList<GameObject>) objects.clone(); //Clone() allows to create a DEEPCOPY of the list to get the variables without actually blocking the real list
		for(GameObject object: clone){
			if (object.isAttackable()) {
				Mob mob = (Mob) object;
				mob.die(); //Kill and remove every mob of the game
			}
		}
	}

	public synchronized void changeMap(){ //is called when the player gets through a door
		synchronized (objects) {
			String map_name = "";
			int map_number_random;
			boolean found = false;
			ArrayList<GameObject> clone = (ArrayList<GameObject>) objects.clone(); //Clone() allows to create a DEEPCOPY of the list to get the variables without actually blocking the real list
			for(GameObject object: clone){
				if (object.isAttackable()) {
					Mob mob = (Mob) object;
					mob.die(); //Kill and remove every mob of the game
				}
			}
			this.objects.subList(1, this.objects.size()).clear();//Suppress the blocks of the previous map (except the player in index 1).
			if(this.mapCounter == 1){ //After the introduction map_0, the next is map_1 
				loadMap("map_1.txt");
			}
			else if(this.mapCounter >5){ //After 5 different maps, the boss room
				bossBool = true;
				loadMap("map_boss.txt");
				uneMinePlayer.play();
			}
			else{
				while(!found){ //Pick a map from a random list of available maps and avoid duplicates
					map_number_random = random.nextInt(4)+2;
					if (!roomsDone.contains(map_number_random)){
						roomsDone.add(map_number_random);
						map_name = String.format("map_%d.txt", map_number_random);
						loadMap(map_name);
						found = true;

					}
				}
			}
		}
		this.mapCounter +=1;
		this.updateWindow();
	}

	private synchronized void loadMap(String fileName) { // Read the MAP.TXT and load every object in the GameObjects list
		try {
			int playerLine = 0;
			int playerColumn = 0;
			ArrayList<Integer> emptyCasesX = new ArrayList<>();
			ArrayList<Integer> emptyCasesY = new ArrayList<>();

			File file = new File(Map.class.getResource("/resources/map/" + fileName).getFile());
			String line = null;

			String map_block_width = ""; //To define the dimension of the map from arguments in the map file
			String map_block_height = "";	

			String darkness = "";

			FileReader fileReader = new FileReader(file);

			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int currentLine = -10; // the -10 is due to parameters in the beginning of map files (10 lines) which must NOT be taken into account for positioning
			while ((line = bufferedReader.readLine()) != null) {
				for (int column = 0; column < line.length(); column++) {
					char c = line.charAt(column);
					if(currentLine == -10){ //Reads the 1st line of the map.txt file as the width of the map
						map_block_width = map_block_width + c;
					}
					else if (currentLine == -9){
						map_block_height = map_block_height +c;
					}

					else if(currentLine == -2){
						darkness = darkness + c;
					}
					switch (c) {
					case '*':
						this.objects.add(new BlockNotBreakable(column, currentLine, this));
						break;
					case '$':
						this.objects.add(new BlockBreakable(column, currentLine, this));
						break;
						// Read position of the Player
					case 'P':
						playerLine = currentLine;
						playerColumn = column;
						break;
					case 'M':
						this.objects.add(new BlockMoveable(column, currentLine, this));
						break;
					case 'C':
						this.objects.add(new Chest(column, currentLine, this));
						break;
					case 'D':
						this.objects.add(new Door(column, currentLine, this));
						break;
					case '/':
						emptyCasesX.add(column);
						emptyCasesY.add(currentLine);
						break;
					default:
						break;
					}
				}
				player.setPos(playerColumn, playerLine); // set position of the player
				currentLine++;
			}
			try{
				CONSTANTS.setMAP_BLOCK_WIDTH(Integer.valueOf(map_block_width)); //Defines the dimension of the map from arguments in the map file
				CONSTANTS.setMAP_BLOCK_HEIGHT( Integer.valueOf(map_block_height));
				savedBlockSize = Math.min(CONSTANTS.getMAP_HEIGHT(),CONSTANTS.getMAP_WIDTH())/Math.max(CONSTANTS.getMAP_BLOCK_WIDTH(), CONSTANTS.getMAP_BLOCK_HEIGHT());
				CONSTANTS.setBLOCK_SIZE(savedBlockSize);
			}catch(NumberFormatException e){
				System.out.println("Les arguments de taille de map ne sont pas valides.");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			if(darkness.equals("DARKNESS")){
				CONSTANTS.setDARKNESS_MODIFIER(true);
				savedDarkness = true;
			}else{
				CONSTANTS.setDARKNESS_MODIFIER(false);
				savedDarkness = false;
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

	private synchronized void loadMobs(ArrayList<Integer> emptyCasesX, ArrayList<Integer> emptyCasesY, int maxMobs) {

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

		for (int i = 0; i < mobXArray.size(); i++) {
			int posX = mobXArray.get(i);
			int posY = mobYArray.get(i);
			boolean randomIsZombie = random.nextBoolean();
			boolean randomBaptized = random.nextBoolean();
			try {
				if (bossBool) {
					Boss boss = new Boss(posX, posY, this);
					this.objects.add(boss);
					window.setBoss(boss);
				} else {
					if (randomIsZombie) {
						this.objects.add(new Zombie(posX, posY, i * 1000 / mobXArray.size(), this, randomBaptized));
					} else {
						this.objects.add(new Skeleton(posX, posY, i * 1000 / mobXArray.size(), this, randomBaptized));
					}
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public synchronized void loot(int x, int y, int objectLoot) {
		int totalLootLevel = (objectLoot+this.mapCounter)*player.getLuck();

		if (lootBool(totalLootLevel)) {
			int randomItemInt = random.nextInt(9); //between 0 and 5
			Item item = null;
			switch (randomItemInt) {
			case 0:
			case 1:
			case 2:
				item = new PotionVie(x, y, this);
				break;
			case 3:
			case 4:
				item = new PotionMana(x, y, this);
				break;
			case 5:
				item = new Penne(x, y, this, Penne.getFileRight());
				break;
			case 6:
				item = new Scepter(x,y,this);
				break;
			case 7:
				item = new Pickaxe(x,y,this);
				break;
			case 8:
				item = new Torch(x,y,this);
				break;
			}
			if (item!=null) {
				this.getGameObjects().add(item);
			}
		}

	}

	private boolean lootBool(int totalLootLevel) {
		boolean res = false;
		int randomInt = random.nextInt(10);
		if (randomInt<totalLootLevel) {
			res = true;
		}
		return res;
	}

	public void changeDrop() {
		this.drop = !this.drop;
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
		for (RedrawObserver ob : listRedrawObservers) {
			ob.redraw(this);
		}
	}

	public int getMapCounter() {
		return this.mapCounter;
	}
	
	public void setState(STATE state) {
		this.state = state;
	}

	public STATE getState(){
		return this.state;
	}

	public Window getWindow(){
		return this.window;
	}
	
	public synchronized ArrayList<GameObject> getGameObjects() {
		return this.objects;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public int getSavedBlockSize() {
		return this.savedBlockSize;
	}

	public boolean getSavedDarkness() {
		return this.savedDarkness;
	}
	
	public void setLineOfSight(int lineOfSight) {
		this.savedLineOfSight = lineOfSight;
	}

	public int getSavedLineOfSight() {
		return this.savedLineOfSight;
	}

}
