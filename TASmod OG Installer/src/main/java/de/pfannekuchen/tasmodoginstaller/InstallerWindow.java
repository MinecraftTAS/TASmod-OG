package de.pfannekuchen.tasmodoginstaller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class InstallerWindow extends JFrame implements MouseListener {

	private static final long serialVersionUID = -6865515862194100993L;

	public static int stage = 0;
	public static boolean selected = false;
	public static String title1 = "TASmod OG ";
	public static String title2 = "Which TASmod OG Version do you want?";
	public static String choice1 = "Full";
	public static String choice2 = "Light";
	public static int offsetX = 0;
	
	private static boolean fullOrLight;
	private static boolean earlyAccessOrNot;
	private static boolean cacheOrNot;
	
	public InstallerWindow() throws IOException {
		super("Installer");
		
		File potionFile = File.createTempFile("potion", ".png");
		Files.copy(new URL("https://data.mgnet.work/potion.png").openStream(), potionFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		setIconImage(new ImageIcon(Files.readAllBytes(potionFile.toPath())).getImage());
		
		setSize(380, 200);
		addMouseListener(this);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		BufferedImage img = new BufferedImage(380, 200, BufferedImage.TYPE_INT_RGB);
		Graphics2D gr = img.createGraphics();
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setColor(new Color(20, 23, 41));
		gr.fillRect(0, 0, 380, 200);
		gr.setColor(new Color(89, 93, 114));
		gr.fillRect(20, 120, 340, 5);
		
		if (stage == 0) gr.setColor(new Color(31, 144, 245));
		else gr.setColor(new Color(204, 204, 204));
		gr.fillOval(17, 117, 11, 11);
		
		if (stage == 1) gr.setColor(new Color(31, 144, 245));
		else gr.setColor(new Color(204, 204, 204));
		gr.fillOval(129, 117, 11, 11);
		
		if (stage == 2) gr.setColor(new Color(31, 144, 245));
		else gr.setColor(new Color(204, 204, 204));
		gr.fillOval(241, 117, 11, 11);
		
		if (stage == 3) gr.setColor(new Color(31, 144, 245));
		else gr.setColor(new Color(204, 204, 204));
		gr.fillOval(353, 117, 11, 11);
		
		gr.setColor(new Color(0, 71, 119));
		RoundRectangle2D round = new RoundRectangle2D.Float(15, 40, 60, 60, 25, 25);
		gr.fill(round);
		
		gr.setColor(new Color(254, 254, 254));
		gr.setFont(new Font("Segoe UI", 0, 14));
		gr.drawString(title1, 84, 60);
		
		if (stage < 3) {
			if (selected) gr.setColor(Color.LIGHT_GRAY);
			else gr.setColor(new Color(254, 254, 254));
			gr.drawString(choice1, 70 + offsetX, 164);
			if (!selected) gr.setColor(Color.LIGHT_GRAY);
			else gr.setColor(new Color(254, 254, 254));
			gr.drawString(choice2, 165, 164);
			
			gr.setColor(new Color(60, 64, 85));
			RoundRectangle2D r2 = new RoundRectangle2D.Float(105, 150, 50, 18, 18, 18);
			gr.fill(r2);
			
			gr.setColor(new Color(31, 144, 245));
			gr.fillOval(selected ? 135 : 105, 148, 22, 22);
			
			gr.setColor(new Color(88, 92, 113));
			RoundRectangle2D r3 = new RoundRectangle2D.Float(285, 145, 75, 30, 15, 15);
			gr.fill(r3);
			
			gr.setColor(new Color(204, 204, 204));
			gr.setFont(new Font("Segoe UI", 0, 14));
			gr.drawString("Continue", 295, 164);
		}
		
		gr.setColor(Color.LIGHT_GRAY);
		gr.drawString(title2, 84, 84);
		
		g.drawImage(img, 0, 0, null);
	}
	
	public static void main(String[] args) throws IOException {
		new InstallerWindow();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int posX = e.getX();
		int posY = e.getY();
		if (posX > 105 && posX < 155) {
			if (posY > 145 && posY < 165) {
				selected = !selected;
				repaint();
			}
		}
		if (posX > 285 && posX < 360) {
			if (posY > 145 && posY < 175) {
				stage++;
				switch (stage) {
				case 1:
					title1 += (selected ? "Light " : "Full ");
					title2 = "Which TASmod OG Channel do you want?";
					offsetX = -20;
					choice1 = "Release";
					choice2 = "Early Access";
					fullOrLight = !selected;
					break;
				case 2:
					title1 += selected ? "- Early Access" : "- Release";
					title2 = "Should the installer use temporary files?";
					choice1 = "Keep";
					choice2 = "Remove";
					offsetX = 0;
					earlyAccessOrNot = selected;
					break;
				case 3:
					title2 = "Downloading TASmod OG";
					cacheOrNot = !selected;
					new Thread(() -> {
						try {
							InstallerBackend.download(fullOrLight, earlyAccessOrNot, cacheOrNot);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}).start();
					break;
				}
				selected = false;
				repaint();
			}
		}
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
}
