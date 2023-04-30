package net.tasmod.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.tasmod.TASmod;
import net.tasmod.recorder.Recorder;
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
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		getInsets().set(0, 0, 0, 0);
		addWindowStateListener(l -> {
			if (l.getOldState() == JFrame.MAXIMIZED_BOTH)
				setState(JFrame.MAXIMIZED_BOTH);
		});
		addWindowListener(new WindowListener() {
			@Override public void windowOpened(WindowEvent e) {}
			@Override public void windowIconified(WindowEvent e) {}
			@Override public void windowDeiconified(WindowEvent e) {}
			@Override public void windowDeactivated(WindowEvent e) {}
			@Override public void windowClosed(WindowEvent e) {}
			@Override public void windowActivated(WindowEvent e) {}
			@Override 
			public void windowClosing(WindowEvent e) {
				if (TASmod.mc == null || !TASmod.mc.running || TickrateChanger.isTickAdvance)
					System.exit(0);
			}
		});
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
				if (TASmod.mc.running) {
					TASmod.wait = true;
				} else {
					Recorder.saveTAS();
				}
			}
		});
		load.addActionListener(e -> {
			final String out = JOptionPane.showInputDialog("Enter the name for the TAS to load", "");
			if (out == null) return;
			final File tasFile = new File(Start.tasDir, out + ".tas");
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
			Start.shouldStart = true;
			TASmod.startPlayback = true;
			create.setEnabled(false);
			start.setEnabled(false);
			load.setEnabled(false);
		});
		create.addActionListener(e -> {
			Start.shouldStart = true;

			TASmod.startRecording = true;
			create.setEnabled(false);
			save.setEnabled(true);
			start.setEnabled(false);
			load.setEnabled(false);
		});
		start.addActionListener(e -> {
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
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(new Dimension(1920, 1080 - 51));
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
			comp.setBounds(0, -21, 1920, 1080);
			p.setBounds(0, -21, 1920, 1080);
			p.add(comp);
			super.add(p, BorderLayout.CENTER);
			super.add(bar, BorderLayout.NORTH);
			return;
		}
		super.add(comp, constraints);
	}

}
