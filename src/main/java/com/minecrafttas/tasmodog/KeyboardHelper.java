package com.minecrafttas.tasmodog;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

/**
 * Keyboard helper for keybinds
 * @author Pancake
 */
public class KeyboardHelper {

	private static ArrayList<Integer> pressedKeys = new ArrayList<>();
	
	/**
	 * Check if a key was just pressed
	 * @param keycode Keypress
	 */
	public static boolean isKeyPress(int keycode) {
		boolean isKeyDown = Keyboard.isKeyDown(keycode);
		boolean isKeyPress = !pressedKeys.contains(keycode) && isKeyDown;
		
		if (!pressedKeys.contains(keycode) && isKeyDown)
			pressedKeys.add(keycode);

		if (pressedKeys.contains(keycode) && !isKeyDown)
			pressedKeys.remove((Integer) keycode);
		
		return isKeyPress;
	}
	
}
