package model;

import java.io.File;
import java.util.ArrayList;

public class CS extends Player {
	
	private static final File spriteFileCSU = new File(GameObject.class.getResource("/resources/sprites/CS_U.png").getFile());
	private static final File spriteFileCSR = new File(GameObject.class.getResource("/resources/sprites/CS_R.png").getFile());
	private static final File spriteFileCSD = new File(GameObject.class.getResource("/resources/sprites/CS_D.png").getFile());
	private static final File spriteFileCSL = new File(GameObject.class.getResource("/resources/sprites/CS_L.png").getFile());
	
	private static final File spriteFilePCSU = new File(GameObject.class.getResource("/resources/sprites/P_CS_U.png").getFile());
	private static final File spriteFilePCSR = new File(GameObject.class.getResource("/resources/sprites/P_CS_R.png").getFile());
	private static final File spriteFilePCSD = new File(GameObject.class.getResource("/resources/sprites/P_CS_D.png").getFile());
	private static final File spriteFilePCSL = new File(GameObject.class.getResource("/resources/sprites/P_CS_L.png").getFile());
	
	private static final int maxHealthCS = 50;
	private static final int maxManaCS = 10;
	private static final int luckCS = 1;
	//private static final int maxMana = 10;
	
	@SuppressWarnings("serial")
	private static final ArrayList<File> fileList = new ArrayList<File>() {{
	    add(spriteFileCSU);
	    add(spriteFileCSR);
	    add(spriteFileCSD);
	    add(spriteFileCSL);
	    add(spriteFilePCSU);
	    add(spriteFilePCSR);
	    add(spriteFilePCSD);
	    add(spriteFilePCSL);
	}};

	public CS(int x, int y, Game game) {
		super(x, y, game, Sprite.makeSpriteList(spriteFileCSU,0,-0.25,1),fileList,maxHealthCS,maxManaCS,luckCS);
		setLuck(3);
	}
	
	@Override
	public void setInventory(Inventory inventory) {
		//Add weapons and items to the Player at the beginning of the Game.
		try {
			inventory.addWeapon(new Sword(0,0,this.getGame()));
			inventory.addConsumable(new Potion(Potion.potionType.vie, 0, 0, this.getGame())); //Note the type of potion
			inventory.addSpell(new Fire(0,0,game,true));
			inventory.addSpell(new Thunder(0,0,game,true));
			inventory.addSpell(new Ice(0,0,game,true));
		}catch (Exception e){}
		
		inventory.setWeaponIndex(0); //Select The Sword as the beginning weapon at start.
	}
	
	@Override
	public void specialAbility() {
		
	}

}
