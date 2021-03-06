package model;

import java.util.ArrayList;
import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Random;

public class Boss extends Mob {
	
	private static final long serialVersionUID = 42L;

	private static final int initWaitTime = 2000;
	private static final int soundWaitTime = 15000;
	private int soundTime = 0;
	private boolean punchline1Bool = true;
	private int waitTime = initWaitTime;
	private static final int maxHealth = 40;
	private int phase = 1;

	private static final File spriteFileU = new File(GameObject.class.getResource("/resources/sprites/Boss_U.png").getFile());
	private static final File spriteFileR = new File(GameObject.class.getResource("/resources/sprites/Boss_R.png").getFile());
	private static final File spriteFileD = new File(GameObject.class.getResource("/resources/sprites/Boss_D.png").getFile());
	private static final File spriteFileL = new File(GameObject.class.getResource("/resources/sprites/Boss_L.png").getFile());

	private static final File spriteFile1D = new File(GameObject.class.getResource("/resources/sprites/Laser_1_D.png").getFile());
	private static final File spriteFile1L = new File(GameObject.class.getResource("/resources/sprites/Laser_1_L.png").getFile());
	private static final File spriteFile1R = new File(GameObject.class.getResource("/resources/sprites/Laser_1_R.png").getFile());
	private static final File spriteFile1U = new File(GameObject.class.getResource("/resources/sprites/Laser_1_U.png").getFile());
	private static final File spriteFile2D = new File(GameObject.class.getResource("/resources/sprites/Laser_2_D.png").getFile());
	private static final File spriteFile2L = new File(GameObject.class.getResource("/resources/sprites/Laser_2_L.png").getFile());
	private static final File spriteFile2R = new File(GameObject.class.getResource("/resources/sprites/Laser_2_R.png").getFile());
	private static final File spriteFile2U = new File(GameObject.class.getResource("/resources/sprites/Laser_2_U.png").getFile());
	private static final File spriteFile3D = new File(GameObject.class.getResource("/resources/sprites/Laser_3_D.png").getFile());
	private static final File spriteFile3L = new File(GameObject.class.getResource("/resources/sprites/Laser_3_L.png").getFile());
	private static final File spriteFile3R = new File(GameObject.class.getResource("/resources/sprites/Laser_3_R.png").getFile());
	private static final File spriteFile3U = new File(GameObject.class.getResource("/resources/sprites/Laser_3_U.png").getFile());

	private static final File file1 = new File(GameObject.class.getResource("/resources/audio/Rotationnel2.m4a").getFile());
	private static final File file2 = new File(GameObject.class.getResource("/resources/audio/Produit_Scalaire.m4a").getFile());
	private static final File file3 = new File(GameObject.class.getResource("/resources/audio/En_Septembre.m4a").getFile());

	private static final Media punchline1Media = new Media(file1.toURI().toString());
	private static final Media punchline2Media = new Media(file2.toURI().toString());
	private static final Media punchline3Media = new Media(file3.toURI().toString());

	private static final transient MediaPlayer punchline1Player = new MediaPlayer(punchline1Media);
	private static final transient MediaPlayer punchline2Player = new MediaPlayer(punchline2Media);
	private static final transient MediaPlayer punchline3Player = new MediaPlayer(punchline3Media);

	public Boss(int x, int y, Game game) {
		super(x, y, game, Sprite.makeSpriteList(spriteFileU,0,0,0),maxHealth,false);
		punchline1Player.setVolume(0.8);
		punchline2Player.setVolume(0.8);
		punchline3Player.setVolume(0.8);
	}

	@Override
	public void run() {
		try{
			Player player = this.getGame().getPlayer();
			Random random = new Random();
			while(player.isAlive()){
				if (game.getState() == Game.STATE.RUN) {
					movePattern(player,random);
					updateSpriteDirection(spriteFileU,spriteFileR,spriteFileD,spriteFileL);
					this.getGame().updateWindow();
					Thread.sleep(waitTime/2);
					attackPattern();
					this.getGame().updateWindow();
				}
				Thread.sleep(waitTime/2);
				setAudio();
			}
			punchline1Player.stop();
			punchline2Player.stop();
			punchline3Player.play();

		}catch(Exception e){
			System.out.println(e.getMessage()); //Often "sleep interrupted" exception, because the thread is interrupted when the mob dies. Normal.
		}; 
	}

	@Override
	public void wasHit(int damage) {
		super.wasHit(damage);
		int newPhase = 0;
		if (this.health < 3*maxHealth/4) {
			newPhase = 2;
		}
		if (this.health < 2*maxHealth/4) {
			newPhase = 3;
		}
		if (this.health < 1*maxHealth/4) {
			newPhase = 4;
		}
		if (newPhase>phase) {
			phase = newPhase;
			waitTime = initWaitTime/phase;
		}
	}

	@Override
	public void attackPattern() {
		int posX = this.getPosX();
		int posY = this.getPosY();
		ArrayList<GameObject> objects = this.getGame().getGameObjects();
		for (int i = 1; i<phase+3; i++) {
			try {
				objects.add(new Laser(posX,posY+i,this.getGame(),fileD(i,phase+2),phase));
				objects.add(new Laser(posX,posY-i,this.getGame(),fileU(i,phase+2),phase));
				objects.add(new Laser(posX+i,posY,this.getGame(),fileR(i,phase+2),phase));
				objects.add(new Laser(posX-i,posY,this.getGame(),fileL(i,phase+2),phase));
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
	}

	private File fileU(int i, int maxI) {
		File file = spriteFile2U;
		if (i == 1) {
			file = spriteFile1U;
		}
		if (i == maxI) {
			file = spriteFile3U;
		}
		return file;
	}

	private File fileR(int i, int maxI) {
		File file = spriteFile2R;
		if (i == 1) {
			file = spriteFile1R;
		}
		if (i == maxI) {
			file = spriteFile3R;
		}
		return file;
	}

	private File fileD(int i, int maxI) {
		File file = spriteFile2D;
		if (i == 1) {
			file = spriteFile1D;
		}
		if (i == maxI) {
			file = spriteFile3D;
		}
		return file;
	}

	private File fileL(int i, int maxI) {
		File file = spriteFile2L;
		if (i == 1) {
			file = spriteFile1L;
		}
		if (i == maxI) {
			file = spriteFile3L;
		}
		return file;
	}

	private void setAudio() {
		soundTime+=waitTime;
		if (soundTime > soundWaitTime) {
			if (punchline1Bool) {
				punchline1Player.play();
				punchline2Player.stop();
			} else {
				punchline2Player.play();
				punchline1Player.stop();
			}
			punchline1Bool = !punchline1Bool;
			soundTime = 0;
		}

	}

	@Override
	public void removeAttackSprites() {

	}

	@Override
	public void die(){
		super.die();
		game.setState(Game.STATE.WIN);
		game.updateWindow();
	}

	public int getMaxHealth(){
		return maxHealth;
	}


}
