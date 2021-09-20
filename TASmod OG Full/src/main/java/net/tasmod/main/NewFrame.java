package net.tasmod.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.tasmod.TASmod;
import net.tasmod.replayer.Replayer;
import net.tasmod.tools.TickrateChanger;

/**
 * A new Frame for Minecraft with some more gui stuff do it
 * @author Pancake
 */
public class NewFrame extends Frame {

	private static final long serialVersionUID = 3759537254483840058L;

	public static Thread mcThread;
	public static NewFrame window;
	public static JMenuBar bar;
	public static JLabel label;
	
	public static Component mcCanvas;
	public static Panel gamePanel;
	
	public NewFrame(String title) {
		super(title);
		window = this;
		getInsets().set(0, 0, 0, 0);
		bar = new JMenuBar();
		// create jmenubar
		JMenu file = new JMenu("File");
		JMenu game = new JMenu("Game");
		JMenu help = new JMenu("Help");
		
		JMenuItem source = new JMenuItem("Source");
		JMenuItem wiki = new JMenuItem("Wiki");
		source.addActionListener(e -> {
			try {
				if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI("https://github.com/MCPfannkuchenYT/TASmod-OG"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		wiki.addActionListener(e -> {
			try {
				if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI("https://github.com/MCPfannkuchenYT/TASmod-OG/wiki"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		help.add(source);
		help.add(wiki);
		
		JMenuItem faster = new JMenuItem("Faster");
		JMenuItem slower = new JMenuItem("Slower");
		JMenuItem pause = new JMenuItem("Pause/Resume");
		faster.addActionListener(e -> {
			TickrateChanger.faster();
		});
		slower.addActionListener(e -> {
			TickrateChanger.slower();
		});
		pause.addActionListener(e -> {
			try {
				TickrateChanger.toggleTickadvance();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		game.add(faster);
		game.add(slower);
		game.add(pause);
		
		JMenuItem load = new JMenuItem("Load TAS");
		JMenuItem create = new JMenuItem("Create TAS");
		JMenuItem save = new JMenuItem("Save TAS");
		save.addActionListener(e -> {
			if (TASmod.isRecording()) {
				String out = JOptionPane.showInputDialog("Enter a name for the TAS", "");
				if (out == null) return;
				TASmod.getRecording().endRecording();
				try {
					TASmod.getRecording().saveTo(new File(out));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				save.setEnabled(false);
			}
		});
		load.addActionListener(e -> {
			String out = JOptionPane.showInputDialog("Enter the name for the TAS to load", "");
			if (out == null) return;
			File tasFile = new File(out);
			try {
				TASmod.playback = new Replayer(tasFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			int width = Integer.parseInt(Start.resolution.split("x")[0]);
	        int height = Integer.parseInt(Start.resolution.split("x")[1]);
			mcCanvas.setBounds(0, 0, width, height);
			gamePanel.setBounds(0, 0, width, height);
			pack();
			setLocationRelativeTo(null);
			Start.shouldStart = true;
			TASmod.startPlayback = true;
			create.setEnabled(false);
			load.setEnabled(false);
		});
		create.addActionListener(e -> {
			String out = JOptionPane.showInputDialog("Select a screen resolution for Minecraft", "854x480");
			if (out == null) return;
			
			Start.resolution = out;
			try {
				int width = Integer.parseInt(Start.resolution.split("x")[0]);
		        int height = Integer.parseInt(Start.resolution.split("x")[1]);	
				mcCanvas.setBounds(0, 0, width, height);
				gamePanel.setBounds(0, 0, width, height);
			} catch (Exception error) {
				return;
			}
			pack();
			setLocationRelativeTo(null);
			Start.shouldStart = true;
			
			TASmod.startRecording = true;
			create.setEnabled(false);
			load.setEnabled(false);
		});
		file.add(load);
		file.add(create);
		file.add(save);
		
		bar.add(file);
		bar.add(game);
		if (Desktop.isDesktopSupported()) bar.add(help);
		// create jlabel
		label = new JLabel("Loading...") {
			private static final long serialVersionUID = -4459139147755002132L;

			@Override
			protected void paintComponent(Graphics g) {
				BufferedImage img = new BufferedImage(label.getWidth(), label.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D gr = img.createGraphics();
				super.paintComponent(gr);
				g.drawImage(img, 2, 0, null);
			}
		};
	}
	
	/**
	 * Changes the Canvas to have an extra Panel around it
	 */
	@Override
	public void add(Component comp, Object constraints) {
		if ("Center".equals(constraints)) {
			Panel p = new Panel(null);
			gamePanel = p;
			mcCanvas = comp;
			comp.setBounds(0, 0, 854, 480);
			p.setBounds(0, 0, 854, 480);
			p.add(comp);
			super.add(p, BorderLayout.CENTER);
			super.add(bar, BorderLayout.NORTH);
			super.add(label, BorderLayout.SOUTH);
			return;
		}
		super.add(comp, constraints);
	}
	
}
