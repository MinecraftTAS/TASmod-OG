package com.minecrafttas.tasmodog.virtual.structs;

import java.io.Serializable;

/**
 * Mouse event
 * @author Pancake
 */
public class MouseEvent implements Serializable {

	private int button;
	private boolean state; 
	private int wheel;
	private int x, y;
	
	/**
	 * Initialize mouse event
	 * @param button Button id
	 * @param state Button state
	 * @param wheel Scroll wheel clicks
	 * @param x Mouse X
	 * @param y Mouse Y
	 */
	public MouseEvent(int button, boolean state, int wheel, int x, int y) {
		this.button = button;
		this.state = state;
		this.wheel = wheel;
		this.x = x;
		this.y = y;
	}
	
	
	/**
	 * Get mouse event button
	 * @return Mouse event button
	 */
	public int getButton() {
		return this.button;
	}
	
	/**
	 * Get button event state
	 * @return Button event state
	 */
	public boolean getState() {
		return this.state;
	}
	
	/**
	 * Get scroll wheel clicks
	 * @return Scroll wheel clicks
	 */
	public int getWheel() {
		return this.wheel;
	}
	
	/**
	 * Get mouse x position
	 * @return Mouse X
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * Get mouse y position
	 * @return Mouse Y
	 */
	public int getY() {
		return this.y;
	}
	
}
