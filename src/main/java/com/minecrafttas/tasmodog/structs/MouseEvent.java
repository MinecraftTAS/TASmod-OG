package com.minecrafttas.tasmodog.structs;

import java.io.Serializable;

/**
 * Mouse event
 * @author Pancake
 */
public class MouseEvent implements Serializable {

	private char character;
	private int key;
	private boolean state; 
	
	/**
	 * Initialize key event
	 * @param character Key character if available
	 * @param key Key code
	 * @param state Key state
	 */
	public MouseEvent(char character, int key, boolean state) {
		this.character = character;
		this.key = key;
		this.state = state;
	}
	
	/**
	 * Get key event character
	 * @return Key event character
	 */
	public char getCharacter() {
		return this.character;
	}
	
	/**
	 * Get key event key
	 * @return Key event key
	 */
	public int getKey() {
		return this.key;
	}
	
	/**
	 * Get key event state
	 * @return Key event state
	 */
	public boolean getState() {
		return this.state;
	}
	
}
