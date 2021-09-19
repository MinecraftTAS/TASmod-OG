package net.tasmod.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Panel;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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
		file.add(load);
		file.add(create);
		file.add(save);
		
		bar.add(file);
		bar.add(game);
		if (Desktop.isDesktopSupported()) bar.add(help);
		// create jlabel
		label = new JLabel("Loading...");
	}
	
	/**
	 * Changes the Canvas to have an extra Panel around it
	 */
	@Override
	public void add(Component comp, Object constraints) {
		if ("Center".equals(constraints)) {
			Panel p = new Panel(null);
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
