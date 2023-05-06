package com.minecrafttas.tasmodog;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import com.minecrafttas.tasmodog.main.Main;

/**
 * Window containing minecraft and tasmod elements
 * @author Pancake
 */
public class MinecraftWindow extends Frame {

	// normal cursor and blank cursor
	public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	public static final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
	
	private JMenuBar menuBar;
	private JMenu fileMenu, gameMenu, helpMenu;
	private JMenuItem openSourceItem, openWikiItem;
	private JMenuItem increaseGamespeedItem, decreaseGamespeedItem, toggleTickadvanceItem;
	private JMenuItem loadItem, createItem, saveItem, launchItem;

	// screen for fullscreen
	private Frame fullscreenFrame;
	private Canvas fullscreenCanvas;
	
	/**
	 * Initialize Minecraft Window
	 * @param title Window title
	 */
	public MinecraftWindow(String title) {
		super(title);
		this.getInsets().set(0, 0, 0, 0);
		this.setResizable(false);
		
		// create fullscreen frame
		this.fullscreenFrame = new Frame(title);
		this.fullscreenFrame.setUndecorated(true);
		this.fullscreenFrame.setExtendedState(MAXIMIZED_BOTH);
		this.fullscreenCanvas = new Canvas();
		this.fullscreenFrame.add(this.fullscreenCanvas);
		
		// create menus
		this.fileMenu = new JMenu("File");
		this.gameMenu = new JMenu("Game");
		this.helpMenu = new JMenu("Help");

		// create help section
		this.openSourceItem = new JMenuItem("Source");
		this.openWikiItem = new JMenuItem("Wiki");
		
		this.helpMenu.add(this.openSourceItem);
		this.helpMenu.add(this.openWikiItem);
		
		// create game section
		this.increaseGamespeedItem = new JMenuItem("Increase Gamespeed");
		this.increaseGamespeedItem.setEnabled(false);;
		this.decreaseGamespeedItem = new JMenuItem("Decrease Gamespeed");
		this.decreaseGamespeedItem.setEnabled(false);
		this.toggleTickadvanceItem = new JMenuItem("Toggle Tickadvance");
		this.toggleTickadvanceItem.setEnabled(false);
		
		this.gameMenu.add(this.increaseGamespeedItem);
		this.gameMenu.add(this.decreaseGamespeedItem);
		this.gameMenu.add(this.toggleTickadvanceItem);

		// create file section
		this.loadItem = new JMenuItem("Load TAS");
		this.createItem = new JMenuItem("Create TAS");
		this.saveItem = new JMenuItem("Save TAS");
		this.saveItem.setEnabled(false);
		this.launchItem = new JMenuItem("Launch normally");
		
		this.fileMenu.add(this.loadItem);
		this.fileMenu.add(this.createItem);
		this.fileMenu.add(this.saveItem);
		this.fileMenu.add(this.launchItem);

		// create menu bar
		this.menuBar = new JMenuBar();
		this.menuBar.add(this.fileMenu);
		this.menuBar.add(this.gameMenu);
		this.menuBar.add(this.helpMenu);
		
		// setup button actions
		this.openSourceItem.addActionListener(this.tryRun(() -> Desktop.getDesktop().browse(URI.create("https://github.com/MinecraftTAS/TASmod-OG"))));
		this.openWikiItem.addActionListener(this.tryRun(() -> Desktop.getDesktop().browse(URI.create("https://github.com/MinecraftTAS/TASmod-OG/wiki"))));
		this.increaseGamespeedItem.addActionListener(this.tryRun(() -> TASmod.instance.getTickrateChanger().increaseGamespeed()));
		this.decreaseGamespeedItem.addActionListener(this.tryRun(() -> TASmod.instance.getTickrateChanger().decreaseGamespeed()));
		this.toggleTickadvanceItem.addActionListener(this.tryRun(() -> TASmod.instance.getTickrateChanger().toggleTickadvance()));
		this.saveItem.addActionListener(this.tryRun(() -> System.out.println("todo")/* TODO TASmod.instance.getInputContainer().interactiveSave() */));
		this.loadItem.addActionListener(this.tryRun(() -> {
			String tasName = JOptionPane.showInputDialog("Enter the name for the TAS to load", "");
			if (tasName == null || tasName.isEmpty())
				return;
			
			Main.setupFilestructure(true);
			TASmod.instance = new TASmod(this);
			TASmod.instance.getInputContainer().load(new File(TASmod.TAS_DIR, tasName + ".tas"));
			this.updateButtons();
		}));
		this.createItem.addActionListener(this.tryRun(() -> {
			Main.setupFilestructure(true);
			TASmod.instance = new TASmod(this);
			TASmod.instance.getInputContainer().setRecording(true);
			this.updateButtons();
			this.enableSaveButton();
		}));
		this.launchItem.addActionListener(this.tryRun(() -> {
			Main.setupFilestructure(false);
			TASmod.instance = new TASmod(this);
			TASmod.instance.getInputContainer().disable();
			this.updateButtons();
		}));
	}
	
	/**
	 * Simple helper runnable that can throw an exception
	 */
	private interface Runnable {
		public void run() throws Exception;
	}
	
	/**
	 * Simple helper for creating an action listener without catching exceptions
	 * @param e Action listener
	 * @return Try-catch action listener
	 */
	private ActionListener tryRun(Runnable r) {
		return _e -> {
			try {
				r.run();
			} catch (Exception e) {
				System.err.println("Exception in Minecraft Window");
				e.printStackTrace();
			}
		};
	}
	
	/**
	 * Update button states after launching minecraft
	 */
	private void updateButtons() {
		this.loadItem.setEnabled(false);
		this.createItem.setEnabled(false);
		this.launchItem.setEnabled(false);
		
		this.increaseGamespeedItem.setEnabled(true);
		this.decreaseGamespeedItem.setEnabled(true);
		this.toggleTickadvanceItem.setEnabled(true);
	}
	
	/**
	 * Set preferred size to 854, 480 - 51 and ignore the parameter
	 */
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(new Dimension(854, 480 - 51));
	}
	
	/**
	 * Add item to frame
	 * and modify the minecraft canvas layout
	 */
	@Override
	public void add(Component comp, Object constraints) {
		if ("Center".equals(constraints)) {
			Panel panel = new Panel(null);
			comp.setBounds(0, 0, 854, 480);
			panel.setBounds(0, 0, 854, 480);
			panel.add(comp);
			super.add(panel, BorderLayout.CENTER);
			super.add(this.menuBar, BorderLayout.NORTH);
			return;
		}
		super.add(comp, constraints);
	}

	/**
	 * Set window location relative to component
	 * and wait until the game is supposed to launch before returning this method and effectively launching the game.
	 */
	@Override
	public void setLocationRelativeTo(Component c) {
		super.setLocationRelativeTo(c);
		this.setVisible(true);
		this.getComponent(0).setPreferredSize(new Dimension(854, 480));
		
       	while (TASmod.instance == null)
       		Thread.yield();
	}

	/**
	 * Disable save button
	 */
	public void disableSaveButton() {
		this.saveItem.setEnabled(false);
	}

	/**
	 * Enable save button
	 */
	public void enableSaveButton() {
		this.saveItem.setEnabled(true);
	}
	
	/**
	 * Toggle fullscreen
	 * @param fullscreen Fullscreen state
	 * @throws LWJGLException LWJGL Exception
	 */
	public void toggleFullscreen(boolean fullscreen) throws LWJGLException {
		this.setVisible(!fullscreen);
		this.fullscreenFrame.setVisible(fullscreen);
		
		if (fullscreen) {
			Display.setParent(this.fullscreenCanvas);
		} else {
			Display.setParent((Canvas) ((Panel) this.getComponent(0)).getComponent(0));
		}
	}
	
	@Override
	public void setCursor(Cursor cursor) {
		this.fullscreenFrame.setCursor(cursor);
		super.setCursor(cursor);
	}
	
}
