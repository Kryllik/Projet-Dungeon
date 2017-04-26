package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;


import CONSTANTS.CONSTANTS;
import model.GameObject;

public class Menu extends JPanel{
	private static final long serialVersionUID = 1L;
	
	
	
	public Rectangle playButton = new Rectangle(CONSTANTS.WINDOW_PIXEL_WIDTH/2 -150, 450,300,80);
	public Rectangle optionsButton = new Rectangle(CONSTANTS.WINDOW_PIXEL_WIDTH/2 -150, 550,300,80);
	public Rectangle exitButton = new Rectangle(CONSTANTS.WINDOW_PIXEL_WIDTH/2 -150, 650,300,80);
	
	private BufferedImage mainScreen;

	
	public Menu() throws IOException {	
		this.setPreferredSize(new Dimension(CONSTANTS.WINDOW_PIXEL_WIDTH, CONSTANTS.WINDOW_PIXEL_HEIGHT));
		this.setFocusable(true);
		this.setEnabled(true);
		this.setRequestFocusEnabled(true);
		this.requestFocusInWindow();
		this.setLayout(null);
		
		mainScreen = ImageIO.read(new File(GameObject.class.getResource("/resources/sprites/main_screen.png").getFile()));



	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		this.setBackground(Color.BLACK);
		
		Font fnt0 = new Font("arial", Font.BOLD, 50);
		g.setFont(fnt0);
		g.setColor(Color.WHITE);
		g.drawString("Haeltermine", CONSTANTS.WINDOW_PIXEL_WIDTH /2 -120, 100);
		g2.draw(playButton);
		g2.draw(optionsButton);
		g2.draw(exitButton);
		g.drawString("New Game", playButton.x+20, playButton.y+60);
		g.drawString("Options", optionsButton.x+60, optionsButton.y+60);
		g.drawString("Quit Game", exitButton.x+25, exitButton.y+60);
		g.drawImage(mainScreen, 100, 32, 780, 400, null);
	}
	
	public void redraw(){
		this.requestFocusInWindow();
		this.repaint();
		
	}

}
