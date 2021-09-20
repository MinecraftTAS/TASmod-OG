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

	/**
	 * Initializes the Menu Bar and Bottom Label such as their Actions
	 * @param title Title of the window
	 */
	public EmulatorFrame(final String title) {
		super(title);
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
			TickrateChanger.faster();
		});
		slower.addActionListener(e -> {
			TickrateChanger.slower();
		});
		pause.addActionListener(e -> {
			try {
				TickrateChanger.toggleTickadvance();
			} catch (final Exception e1) {
				e1.printStackTrace();
			}
		});
		game.add(faster);
		game.add(slower);
		game.add(pause);

		final JMenuItem load = new JMenuItem("Load TAS");
		final JMenuItem create = new JMenuItem("Create TAS");
		final JMenuItem save = new JMenuItem("Save TAS");
		final JMenuItem start = new JMenuItem("Launch normally");
		save.addActionListener(e -> {
			if (TASmod.recording != null) {
				final String out = JOptionPane.showInputDialog("Enter a name for the TAS", "");
				if (out == null) return;
				TASmod.recording.endRecording();
				try {
					TASmod.recording.saveTo(new File(out));
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
				save.setEnabled(false);
			}
		});
		load.addActionListener(e -> {
			final String out = JOptionPane.showInputDialog("Enter the name for the TAS to load", "");
			if (out == null) return;
			final File tasFile = new File(out);
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
			load.setEnabled(false);
		});
		create.addActionListener(e -> {
			final String out = JOptionPane.showInputDialog("Select a screen resolution for Minecraft", "854x480");
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
			load.setEnabled(false);
		});
		start.addActionListener(e -> {
			Start.resolution = "854x480";
			Start.shouldStart = true;
			Start.isNormalLaunch = true;
			create.setEnabled(false);
			save.setEnabled(false);
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
