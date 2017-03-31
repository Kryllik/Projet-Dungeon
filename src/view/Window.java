package view;
import model.GameObject;

import java.awt.Color;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Window {
	private Map map = new Map();
	
	public Window() throws IOException{	    
	    JFrame window = new JFrame("HaelterMINE");
	    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    window.setBounds(0, 0, 1000, 1020);
	    window.setIconImage(ImageIO.read(new File(GameObject.class.getResource("/resources/sprites/Haelti_Head.jpg").getFile())));
	    window.getContentPane().setBackground(Color.gray);
	    window.getContentPane().add(this.map);
	    window.setVisible(true);
	    map.loadMap("map_1.txt");
	}	
	
	public void setGameObjects(ArrayList<GameObject> objects){
		this.map.setObjects(objects);
		this.map.redraw();
	}
	
	public void update(){
		this.map.redraw();
	}
	
	public void setKeyListener(KeyListener keyboard){
	    this.map.addKeyListener(keyboard);
	}
}
