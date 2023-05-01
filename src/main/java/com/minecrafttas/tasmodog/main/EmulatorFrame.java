package com.minecrafttas.tasmodog.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.minecrafttas.tasmodog.TASmod;
import com.minecrafttas.tasmodog.container.Recording;
import com.minecrafttas.tasmodog.container.Playback;

/**
 * A new Frame for Minecraft with some more gui stuff do it
 * @author Pancake
 */
public class EmulatorFrame extends Frame {

	/** The Singleton of this File */
	public static EmulatorFrame window;
	/** The Top Bar of the window */
	public static JMenuBar bar;
	/** The Canvas of the Minecraft Game */
	public static Component mcCanvas;
	/** The Panel that holds the Minecraft Canvas at a specific resolution */
	public static Panel gamePanel;
	/** The Original Cursor to avoid a cursor madness */
	public static Cursor origCursor;
	/** The Original Cursor to avoid a cursor madness */
	public static Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
	/** The Save TAS button */
	public static JMenuItem save;
	
	/**
	 * Initializes the Menu Bar and Bottom Label such as their Actions
	 * @param title Title of the window
	 */
	public EmulatorFrame(final String title) {
		super(title);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		origCursor = getCursor();
		window = this;
		getInsets().set(0, 0, 0, 0);
		bar = new JMenuBar();
		// create jmenubar
		final JMenu file = new JMenu("File");
		final JMenu game = new JMenu("Game");
		final JMenu help = new JMenu("Help");

		final JMenuItem source = new JMenuItem("Source");
		final JMenuItem wiki = new JMenuItem("Wiki");	
		source.addActionListener(e -> {
			try {
				if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI("https://github.com/MinecraftTAS/TASmod-OG"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		wiki.addActionListener(e -> {
			try {
				if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI("https://github.com/MinecraftTAS/TASmod-OG/wiki"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		help.add(source);
		help.add(wiki);

		final JMenuItem faster = new JMenuItem("Faster");
		final JMenuItem slower = new JMenuItem("Slower");
		final JMenuItem pause = new JMenuItem("Pause/Resume");
		faster.addActionListener(e -> {
			if (TASmod.instance != null)
				TASmod.instance.getTickrateChanger().increaseGamespeed();
		});
		slower.addActionListener(e -> {
			if (TASmod.instance != null)
				TASmod.instance.getTickrateChanger().decreaseGamespeed();
		});
		pause.addActionListener(e -> {
			if (TASmod.instance != null)
				TASmod.instance.getTickrateChanger().toggleTickadvance();
		});
		game.add(faster);
		game.add(slower);
		game.add(pause);

		final JMenuItem load = new JMenuItem("Load TAS");
		final JMenuItem create = new JMenuItem("Create TAS");
		save = new JMenuItem("Save TAS");
		save.setEnabled(false);
		final JMenuItem start = new JMenuItem("Launch normally");
		save.addActionListener(e -> ((Recording) TASmod.instance.getInputContainer()).interactiveSave());
		load.addActionListener(e -> {
			final String out = JOptionPane.showInputDialog("Enter the name for the TAS to load", "");
			if (out == null) return;
			
			final File tasFile = new File(TASmod.TAS_DIR, out + ".tas");
			String tick = JOptionPane.showInputDialog("Enter tick to rerecord at (leave empty for full playback): ", "");
			if (tick == null) return;
			
			try {
				Start.setupFilestructure(true);

				if (!tick.isEmpty())
					TASmod.instance = new TASmod(new Playback(tasFile, Integer.parseInt(tick)));
				else
					TASmod.instance = new TASmod(new Playback(tasFile, -1));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			
			create.setEnabled(false);
			start.setEnabled(false);
			load.setEnabled(false);
		});
		create.addActionListener(e -> {
			try {
				Start.setupFilestructure(true);
				TASmod.instance = new TASmod(new Recording());

				create.setEnabled(false);
				save.setEnabled(true);
				start.setEnabled(false);
				load.setEnabled(false);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		start.addActionListener(e -> {
			try {
				Start.setupFilestructure(false);
				TASmod.instance = new TASmod(null);
				start.setEnabled(false);
				create.setEnabled(false);
				load.setEnabled(false);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		file.add(load);
		file.add(create);
		file.add(save);
		file.add(start);

		bar.add(file);
		bar.add(game);
		if (Desktop.isDesktopSupported()) bar.add(help);
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(new Dimension(854, 480 - 51));
	}
	
	/**
	 * Changes the Canvas to have an extra Panel around it
	 */
	@Override
	public void add(final Component comp, final Object constraints) {
		if ("Center".equals(constraints)) {
			final Panel p = new Panel(null);
			gamePanel = p;
			mcCanvas = comp;
			comp.setBounds(0, 0, 854, 480);
			p.setBounds(0, 0, 854, 480);
			p.add(comp);
			super.add(p, BorderLayout.CENTER);
			super.add(bar, BorderLayout.NORTH);
			return;
		}
		super.add(comp, constraints);
	}

	/**
	 * Wait until the game is supposed to launch before returning this method and effectively launching the game.
	 */
	@Override
	public void setLocationRelativeTo(Component c) {
		super.setLocationRelativeTo(c);
		this.setVisible(true);
		this.getComponent(0).setPreferredSize(new Dimension(854, 480));
		
       	while (TASmod.instance == null)
       		Thread.yield();
	}
	
}
