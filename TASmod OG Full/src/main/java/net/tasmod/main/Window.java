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
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;

public class Window extends JFrame implements KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -4893558106947932185L;
	
	Image dirt;
	Image btn;
	Image btnh;
	ControlGui gui;
	static Window self;
	static List<Button> buttons = new ArrayList<>();
	public static int gameWindowSizeX;
	public static int gameWindowSizeY;
	
	public Window(String name) {
		super(name);
		self = this;
		gui = new ControlGui();
		setResizable(false);
		setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setUndecorated(true);
		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		setExtendedState(MAXIMIZED_BOTH);
		try {
			dirt = ImageIO.read(Window.class.getResourceAsStream("dirt.jpg"));
			btn = ImageIO.read(Window.class.getResourceAsStream("button.png"));
			btnh = ImageIO.read(Window.class.getResourceAsStream("hovered_button.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		gui.init(getWidth(), getHeight());
		gui.requestRepaint();
	}
	
	@Override
	public void paint(Graphics gr) {
		// Render background
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		
		for (int x1 = 0; x1 < getWidth() - gameWindowSizeX; x1+=64) for (int y1 = 0; y1 < getHeight(); y1+=64) g.drawImage(dirt, x1, y1, 64, 64, null);
		for (int x1 = 0; x1 < getWidth(); x1+=64) for (int y1 = 0; y1 < getHeight() - gameWindowSizeY; y1+=64) g.drawImage(dirt, x1, gameWindowSizeY + y1, 64, 64, null);
		
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(5.0f));
		g.drawRect(getWidth() - gameWindowSizeX - 3, -5, gameWindowSizeX + 5, gameWindowSizeY + 7);
		
		for (Button button : buttons) {
			int texW = (button.w - 6)*3;
			g.drawImage(button.isHovered ? btnh : btn, button.x, button.y, button.x+texW, button.y+button.h*3, 0, 0, texW, 60, null);
			g.drawImage(button.isHovered ? btnh : btn, button.x+texW, button.y, button.x+texW+6, button.y+(button.h*3), 594, 0, 600, 60, null);
		}
		
		gui.render((Graphics2D) g);
		gr.drawImage(img, 0, 0, this);
	}

	
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseDragged(MouseEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		gui.key(e.getKeyCode());
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		for (Button button : buttons) 
			if (e.getX() > button.x && e.getY() > button.y) 
				if (e.getX() < (button.x + button.w * 3) && e.getY() < (button.y + button.h * 3)) {
					try {
						Clip c = AudioSystem.getClip();
						AudioInputStream s = AudioSystem.getAudioInputStream(Window.class.getResourceAsStream("click.wav"));
						c.open(s);
						c.start();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					button.onclick.run();
				}			
		gui.click(e.getX(), e.getY());
	}
	
	@Override 
	public void mouseMoved(MouseEvent e) {
		for (Button button : buttons) {
			button.isHovered = false;
			if (e.getX() > button.x && e.getY() > button.y) 
				if (e.getX() < (button.x + button.w * 3) && e.getY() < (button.y + button.h * 3))
					button.isHovered = true;
		}
		gui.requestRepaint();
		gui.hover(e.getX(), e.getY());
	}
	
	public static class Button {
		Runnable onclick;
		int x;
		int y;
		int w;
		int h;
		boolean isHovered;
		public Button(int x, int y, int w, int h, Runnable onclick) {
			this.onclick = onclick;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}
	
	public static abstract class Gui {
		public Gui() {}
		public abstract void init(int width, int height);
		public abstract void render(Graphics2D g);
		public abstract void click(int x, int y);
		public abstract void hover(int x, int y);
		public abstract void key(int keycode);
		protected void addButton(Button b) { buttons.add(b); }
		protected void requestRepaint() {
			self.repaint();
		};
	}
}
