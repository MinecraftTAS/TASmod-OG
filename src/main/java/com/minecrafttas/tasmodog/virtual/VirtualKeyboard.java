package com.minecrafttas.tasmodog.virtual;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.minecrafttas.tasmodog.TASmod;
import com.minecrafttas.tasmodog.container.InputContainer;
import com.minecrafttas.tasmodog.virtual.structs.KeyEvent;

/**
 * Virtual keyboard replacing lwjgl's keyboard class
 * Emulates a scriptable keyboard in addition to the normal keyboard
 */
public class VirtualKeyboard {
	
	private static KeyEvent keyEvent;
	private static List<Integer> keysPressed = new ArrayList<>(256);
	
	public static boolean next() {
		InputContainer inputContainer = TASmod.instance.getInputContainer();
		KeyEvent nextKeyEvent = inputContainer.getCurrentTickData().pollKeyEvent();
		boolean next = nextKeyEvent != null;
		
		// update from real keyboard if recording
		if (inputContainer.isRecording() || !inputContainer.isActive()) {
			// fetch event
			next = Keyboard.next();
			nextKeyEvent = new KeyEvent(Keyboard.getEventCharacter(), Keyboard.getEventKey(), Keyboard.getEventKeyState());
			
			// push event
			if (next && inputContainer.isActive())
				inputContainer.getCurrentTickData().addKeyEvent(nextKeyEvent);
		}
		
		// update keyboard if new event
		if (next) {
			keyEvent = nextKeyEvent;
			if (nextKeyEvent.getState())
				keysPressed.add(nextKeyEvent.getKey());
			else
				keysPressed.remove((Integer) nextKeyEvent.getKey());
		}
		
		return next;
	}
	
	public static boolean isKeyDown(int i) {
		return keysPressed.contains((Integer) i);
	}
	
	public static boolean getEventKeyState() {
		return keyEvent.getState();
	}
	
	public static int getEventKey() {
		return keyEvent.getKey();
	}
	
	public static char getEventCharacter() {
		return keyEvent.getCharacter();
	}
	
}
