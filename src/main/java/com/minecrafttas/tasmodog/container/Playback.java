package com.minecrafttas.tasmodog.container;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.minecrafttas.tasmodog.KeyboardHelper;
import com.minecrafttas.tasmodog.TASmod;
import com.minecrafttas.tasmodog.main.EmulatorFrame;
import com.minecrafttas.tasmodog.virtual.VirtualKeyboard;
import com.minecrafttas.tasmodog.virtual.VirtualKeyboard.VirtualKeyEvent;
import com.minecrafttas.tasmodog.virtual.VirtualMouse;
import com.minecrafttas.tasmodog.virtual.VirtualMouse.VirtualMouseEvent;

/**
 * Input playback container
 * @author Pancake
 */
public class Playback implements Container {

	private TASmod tasmod;
	private BufferedReader reader;
	private File file;
	private int currentTick;
	private int recordFrom;

	/**
	 * Initialize Playback
	 * @param recordFrom Tick to start recording from 
	 * @throws IOException Filesystem exception
	 */
	public Playback(final File name, int recordFrom) throws Exception {
		this.file = name;
		this.recordFrom = recordFrom;
		this.reader = new BufferedReader(new FileReader(this.file));
	}


	/**
	 * Post Initialize Playback Container with TASmod instance
	 * @param tasmod TASmod instance
	 */
	@Override
	public void init(TASmod tasmod) {
		this.tasmod = tasmod;
		VirtualKeyboard.hack = true;
		VirtualMouse.hack = true;
	}
	
	/**
	 * Tick playback container
	 */
	public void tick() throws Exception {
		// tick playback
		this.tickKeyboad();
		this.tickMouse();
		this.currentTick++;
		
		// switch to recording if requested
		if (this.recordFrom == this.currentTick || KeyboardHelper.isKeyPress(Keyboard.KEY_F12)) {
			this.tasmod.getTickrateChanger().updateGamespeed(0f);
			VirtualKeyboard.hack = VirtualMouse.hack = false;
			this.tasmod.updateInputContainer(new Recording(this.currentTick, this.file));
			EmulatorFrame.save.setEnabled(true);
			this.tasmod.getInputContainer().init(this.tasmod);
		}
	}
	
	/**
	 * Tick keyboard playback
	 * @throws Exception Filesystem exception
	 */
	private void tickKeyboad() throws Exception {
		String line = this.reader.readLine();
		if (line != null) {
			if (line.isEmpty())
				return;
			
			Queue<VirtualKeyEvent> queue = VirtualKeyboard.keyEventsForTick;
			queue.clear();
			queue.addAll(Arrays.asList(line.split(":")).stream().map(VirtualKeyEvent::fromString).collect(Collectors.toSet()));
		} else {
			VirtualKeyboard.hack = false;
			TASmod.instance.updateInputContainer(null);
		}
	}
	
	/**
	 * Tick mouse playback
	 * @throws Exception Filesystem exception
	 */
	private void tickMouse() throws Exception {
		String line = this.reader.readLine();
		if (line != null) {
			if (line.isEmpty())
				return;
			
			Queue<VirtualMouseEvent> queue = VirtualMouse.mouseEventsForTick;
			queue.clear();
			Arrays.asList(line.split(":")).forEach(c -> {
				VirtualMouseEvent event = VirtualMouseEvent.fromString(c);
				queue.add(event);
				VirtualMouse.dX = VirtualMouse.fdX = event.dX;
				VirtualMouse.dY = VirtualMouse.fdX = event.dY;
			});
		} else {
			VirtualMouse.hack = false;
		}
	}
	
	/**
	 * Whether the hud should be visible
	 */
	public boolean isVisible() {
		return true;
	}
}
