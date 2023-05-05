package com.minecrafttas.tasmodog.structs;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import com.minecrafttas.tasmodog.virtual.VirtualKeyboard.VirtualKeyEvent;
import com.minecrafttas.tasmodog.virtual.VirtualMouse.VirtualMouseEvent;

/**
 * TASmod data for a tick
 * @author Pancake
 */
public class Tick implements Serializable {

	private Queue<VirtualKeyEvent> keyboardInputs;
	private Queue<VirtualMouseEvent> mouseInputs;

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
	public VirtualKeyEvent pollKeyEvent() {
		return this.keyboardInputs.poll();
	}
	
	/**
	 * Poll mouse event from queue
	 * @return Mouse event
	 */
	public VirtualMouseEvent pollMouseEvent() {
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
	public void addKeyEvent(VirtualKeyEvent event) {
		this.keyboardInputs.add(event);
	}
	
	/**
	 * Adds mouse event to queue
	 * @param event Mouse event
	 */
	public void addMouseEvent(VirtualMouseEvent event) {
		this.mouseInputs.add(event);
	}
	
}
