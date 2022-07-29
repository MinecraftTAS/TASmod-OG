package net.tasmod.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
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
public class EmulatorFrame extends Frame {

	private static final long serialVersionUID = 3759537254483840058L;

	/** The Singleton of this File */
	public static EmulatorFrame window;
	/** The Top Bar of the window */
	public static JMenuBar bar;
	/** The Bottom Bar of the window */
	public static JLabel label;
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

		final JMenuItem faster = new JMenuItem("Faster");
		final JMenuItem slower = new JMenuItem("Slower");
		final JMenuItem pause = new JMenuItem("Pause/Resume");
		faster.addActionListener(e -> {
			if (Start.shouldStart && !TickrateChanger.isTickAdvance) TickrateChanger.faster();
		});
		slower.addActionListener(e -> {
			if (Start.shouldStart && !TickrateChanger.isTickAdvance) TickrateChanger.slower();
		});
		pause.addActionListener(e -> {
			try {
				if (Start.shouldStart) TickrateChanger.toggleTickadvance();
			} catch (final Exception e1) {
				e1.printStackTrace();
			}
		});
		game.add(faster);
		game.add(slower);
		game.add(pause);

		final JMenuItem load = new JMenuItem("Load TAS");
		final JMenuItem create = new JMenuItem("Create TAS");
		save = new JMenuItem("Save TAS");
		save.setEnabled(false);
		final JMenuItem start = new JMenuItem("Launch normally");
		save.addActionListener(e -> {
			if (TASmod.recording != null) {
				TASmod.wait = true;
			}
		});
		load.addActionListener(e -> {
			final String out = JOptionPane.showInputDialog("Enter the name for the TAS to load", "");
			if (out == null) return;
			final File tasFile = new File(Start.tasDir, out);
			String tick = JOptionPane.showInputDialog("Enter tick to rerecord at (leave empty for full playback): ", "");
			if (tick == null) return;
			if (!tick.isEmpty()) {
				TASmod.pauseAt = Integer.parseInt(tick);
			}
			try {
				TASmod.playback = new Replayer(tasFile);
			} catch (final Exception e1) {
				e1.printStackTrace();
			}
			final int width = Integer.parseInt(Start.resolution.split("x")[0]);
			final int height = Integer.parseInt(Start.resolution.split("x")[1]);
			mcCanvas.setBounds(0, 0, width, height);
			gamePanel.setBounds(0, 0, width, height);
			pack();
			setLocationRelativeTo(null);
			Start.shouldStart = true;
			TASmod.startPlayback = true;
			create.setEnabled(false);
			start.setEnabled(false);
			load.setEnabled(false);
		});
		create.addActionListener(e -> {
			final String out = JOptionPane.showInputDialog("Select a screen resolution for Minecraft", "1728x972");
			if (out == null) return;

			Start.resolution = out;
			try {
				final int width = Integer.parseInt(Start.resolution.split("x")[0]);
				final int height = Integer.parseInt(Start.resolution.split("x")[1]);
				mcCanvas.setBounds(0, 0, width, height);
				gamePanel.setBounds(0, 0, width, height);
			} catch (final Exception error) {
				return;
			}
			pack();
			setLocationRelativeTo(null);
			Start.shouldStart = true;

			TASmod.startRecording = true;
			create.setEnabled(false);
			save.setEnabled(true);
			start.setEnabled(false);
			load.setEnabled(false);
		});
		start.addActionListener(e -> {
			Start.resolution = "1728x972";
			Start.shouldStart = true;
			Start.isNormalLaunch = true;
			start.setEnabled(false);
			create.setEnabled(false);
			load.setEnabled(false);
		});
		file.add(load);
		file.add(create);
		file.add(save);
		file.add(start);

		bar.add(file);
		bar.add(game);
		if (Desktop.isDesktopSupported()) bar.add(help);
		// create jlabel
		label = new JLabel("Loading...") {
			private static final long serialVersionUID = -4459139147755002132L;

			@Override
			protected void paintComponent(final Graphics g) {
				final BufferedImage img = new BufferedImage(label.getWidth(), label.getHeight(), BufferedImage.TYPE_INT_ARGB);
				final Graphics2D gr = img.createGraphics();
				super.paintComponent(gr);
				g.drawImage(img, 2, 0, null);
			}
		};
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
			super.add(label, BorderLayout.SOUTH);
			return;
		}
		super.add(comp, constraints);
	}

}
