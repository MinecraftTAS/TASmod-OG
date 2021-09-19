package net.tasmod.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class Window extends JFrame implements KeyListener, MouseListener {

	private static final long serialVersionUID = -4893558106947932185L;
	
	Image img;
	public static int gameWindowSizeX;
	public static int gameWindowSizeY;
	
	public Window(String name) {
		super(name);
		setResizable(false);
		setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setUndecorated(true);
		addMouseListener(this);
		addKeyListener(this);
		setExtendedState(MAXIMIZED_BOTH);
		try {
			img = ImageIO.read(Window.class.getResourceAsStream("background.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		// Render background
		for (int x1 = 0; x1 < getWidth() - gameWindowSizeX; x1+=64) for (int y1 = 0; y1 < getHeight(); y1+=64) g.drawImage(img, x1, y1, 64, 64, null);
		for (int x1 = 0; x1 < getWidth(); x1+=64) for (int y1 = 0; y1 < getHeight() - gameWindowSizeY; y1+=64) g.drawImage(img, x1, gameWindowSizeY + y1, 64, 64, null);
		g.setColor(Color.black);
		((Graphics2D) g).setStroke(new BasicStroke(5.0f));
		g.drawRect(getWidth() - gameWindowSizeX - 3, -5, gameWindowSizeX + 5, gameWindowSizeY + 7);
	}

	
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	
}
