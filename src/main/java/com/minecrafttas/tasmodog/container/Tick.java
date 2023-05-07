package com.minecrafttas.tasmodog.container;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.LinkedList;

import com.minecrafttas.tasmodog.virtual.structs.KeyEvent;
import com.minecrafttas.tasmodog.virtual.structs.MouseEvent;

/**
 * TASmod data for a tick
 * @author Pancake
 */
public class Tick implements Serializable {

	private LinkedList<KeyEvent> keyboardInputs;
	private LinkedList<MouseEvent> mouseInputs;
	private transient RandomAccessFile state;
	public int dx, dy;
	
	/**
	 * Initialize empty tick struct
	 */
	public Tick() {
		this.keyboardInputs = new LinkedList<>();
		this.mouseInputs = new LinkedList<>();
	}

	/**
	 * Poll key event from queue
	 * @return Key event
	 */
	public KeyEvent pollKeyEvent() {
		return this.keyboardInputs.poll();
	}
	
	/**
	 * Poll mouse event from queue
	 * @return Mouse event
	 */
	public MouseEvent pollMouseEvent() {
		return this.mouseInputs.poll();
	}
	
	/**
	 * Has next mouse event
	 * @return Mouse event available
	 */
	public boolean hasMouseEvent() {
		return !this.mouseInputs.isEmpty();
	}
	
	/**
	 * Has next key event
	 * @return Key event available
	 */
	public boolean hasKeyEvent() {
		return !this.keyboardInputs.isEmpty();
	}
	
	/**
	 * Clear mouse and keyboard inputs
	 */
	public void clearInputs() {
		this.keyboardInputs.clear();
		this.mouseInputs.clear();
	}
	
	/**
	 * Add key event to queue
	 * @param event Key event
	 */
	public void addKeyEvent(KeyEvent event) {
		this.keyboardInputs.add(event);
	}
	
	/**
	 * Add mouse event to queue
	 * @param event Mouse event
	 */
	public void addMouseEvent(MouseEvent event) {
		this.mouseInputs.add(event);
	}
	
	/**
	 * Initialize random access file with state
	 * @param state State data
	 * @throws Exception Filesystem exception
	 */
	public void initializeState(byte[] state) throws Exception {
		this.state = new RandomAccessFile(File.createTempFile("savestate", ".dat"), "rw");
		this.state.writeInt(state.length);
		this.state.write(state);
	}

	/**
	 * Return state data
	 * @return State data or null
	 * @throws Exception Filesystem exception
	 */
	public byte[] getState() throws Exception {
		if (this.state == null)
			return null;
		
		this.state.seek(0);
		byte[] data = new byte[this.state.readInt()];
		this.state.read(data);
		return data;
	}
	
	@Override
	public Tick clone() {
		Tick tick = new Tick();
		tick.keyboardInputs = new LinkedList<>(this.keyboardInputs);
		tick.mouseInputs = new LinkedList<>(this.mouseInputs);
		tick.dx = this.dx;
		tick.dy = this.dy;
		return tick;
	}
	
}
