package com.minecrafttas.tasmodog.virtual;

import org.lwjgl.input.Keyboard;

import com.minecrafttas.tasmodog.TASmod;
import com.minecrafttas.tasmodog.container.InputContainer;
import com.minecrafttas.tasmodog.container.InputContainer.State;
import com.minecrafttas.tasmodog.virtual.structs.KeyEvent;

/**
 * Virtual keyboard replacing lwjgl's keyboard class
 * Emulates a scriptable keyboard in addition to the normal keyboard
 */
public class VirtualKeyboard {
	
	private static KeyEvent keyEvent;
	private static boolean[] keysPressed = new boolean[256+256];
	
	public static boolean next() {
		InputContainer inputContainer = TASmod.instance.getInputContainer();
		KeyEvent nextKeyEvent = inputContainer.getState() == State.RECORDING ? null : inputContainer.getCurrentTickData().pollKeyEvent();
		boolean next = nextKeyEvent != null;
		
		// update from real keyboard if not playing back
		if (inputContainer.getState() != State.PLAYBACK) {
			// fetch event
			next = Keyboard.next();
			nextKeyEvent = new KeyEvent(Keyboard.getEventCharacter(), Keyboard.getEventKey(), Keyboard.getEventKeyState());
			
			// push event if recording
			if (next && inputContainer.getState() == State.RECORDING)
				inputContainer.getCurrentTickData().addKeyEvent(nextKeyEvent);
		}
		
		// update keyboard if new event
		if (next) {
			keyEvent = nextKeyEvent;
			keysPressed[nextKeyEvent.getKey()+256] = nextKeyEvent.getState();
		}
		
		return next;
	}
	
	public static boolean isKeyDown(int i) {
		return keysPressed[i+256];
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
