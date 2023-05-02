package com.minecrafttas.tasmodog.container;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;

import com.minecrafttas.tasmodog.KeyboardHelper;
import com.minecrafttas.tasmodog.TASmod;
import com.minecrafttas.tasmodog.virtual.VirtualKeyboard;
import com.minecrafttas.tasmodog.virtual.VirtualKeyboard.VirtualKeyEvent;
import com.minecrafttas.tasmodog.virtual.VirtualMouse;
import com.minecrafttas.tasmodog.virtual.VirtualMouse.VirtualMouseEvent;

/**
 * Input recorder container
 * @author Pancake
 */
public class Recording implements Container {

	private TASmod tasmod;
	private File rerecord;

	private Queue<String> lines = new LinkedList<>();
	private volatile List<VirtualKeyEvent> keyEvents = new ArrayList<>();
	private volatile List<VirtualMouseEvent> mouseEvents = new ArrayList<>();
	
	public int currentTick, startingTick;
	
	/**
	 * Initialize recording container
	 */
	public Recording() {

	}
	
	/**
	 * Initialize recording container
	 * @param tick Tick to continue from
	 * @param rerecord File to continue from
	 */
	public Recording(int tick, File rerecord) {
		this.currentTick = this.startingTick = tick;
		this.rerecord = rerecord;
	}

	/**
	 * Post Initialize Recording container with TASmod instance
	 * @param tasmod TASmod instance
	 */
	public void init(TASmod tasmod) {
		this.tasmod = tasmod;
		VirtualKeyboard.listen = true;
		VirtualMouse.listen = true;
	}
	
	/**
	 * Tick recording container
	 */
	public void tick() {
		// save on f12
		if (KeyboardHelper.isKeyPress(Keyboard.KEY_F12))
			this.interactiveSave();
		
		// write keyboard events
		while (true) {
			String events = "";
			if (this.keyEvents.size() == 0) {
				this.lines.add("");
				break;
			}
			
			for (final VirtualKeyEvent virtualKeyEvent : this.keyEvents)
				events += virtualKeyEvent + ":";
			
			this.lines.add(events.substring(0, events.length() - 1));
			this.keyEvents.clear();
			break;
		}
		
		// write mouse events
		while (true) {
			String events = "";
			if (this.mouseEvents.size() == 0) {
				this.lines.add("");
				break;
			}
			
			for (final VirtualMouseEvent virtualMouseEvent : this.mouseEvents) {
				virtualMouseEvent.dX = VirtualMouse.dX;
				virtualMouseEvent.dY = VirtualMouse.dY;
				events += virtualMouseEvent + ":";
			}
			
			this.lines.add(events.substring(0, events.length() - 1));
			this.mouseEvents.clear();
			break;
		}
		
		this.currentTick++;
	}

	/**
	 * Add key event
	 * @param event Key event
	 */
	public void keyboardTick(VirtualKeyEvent event) {
		if (event.key != -1)
			this.keyEvents.add(event);
	}

	/**
	 * Add mouse event
	 * @param event Mouse event
	 */
	public void mouseTick(VirtualMouseEvent event) {
		if (event.posX != -1 || event.eventButton != -1)
			this.mouseEvents.add(event);
	}
	
	/**
	 * Save TAS to file
	 * @param file File
	 * @throws Exception Filesystem exception
	 */
	public void save(File file) throws Exception {
		List<String> lines = new ArrayList<>();

		// create file
		if (!file.exists())
			file.createNewFile();
		
		// load lines from previous file
		if (this.rerecord != null)
			lines = Files.readAllLines(this.rerecord.toPath()).subList(0, (this.startingTick*2));
		
		// write file
		FileWriter f = new FileWriter(file);
		for (String line : lines)
			f.write(line + "\n");
		
		for (String line : this.lines)
			f.write(line + "\n");
		f.close();
	}

	/**
	 * Interactively ask for a filename and save the TAS
	 */
	public void interactiveSave() {
		// ask user for filename
		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
		String out = JOptionPane.showInputDialog("Enter a name for the TAS", "");
		if (out == null) return;
		
		// end playback
		VirtualKeyboard.listen = false;
		VirtualMouse.listen = false;
		this.tasmod.getMinecraftWindow().disableSaveButton();
		
		// save file
		try {
			File file = new File(TASmod.TAS_DIR, out + ".tas");
			file.getParentFile().mkdirs();
			this.save(file);
		} catch (Exception e) {
			System.err.println("Unable to save TAS");
			e.printStackTrace();
		}
	}

}
