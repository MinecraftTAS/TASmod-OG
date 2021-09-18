package net.tasmod.virtual;

import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.input.Keyboard;

import net.tasmod.Utils;

/**
 * This is an interface of the Keyboard Class.
 * This File records or manipulates these Calls
 * @author ScribbleLP, Pancake
 */
public final class VirtualKeyboard {

	/**
	 * Internal Keyboard Event used to save the Keyboard to a File
	 * @author ScribbleLP
	 */
	public static final class VirtualKeyEvent {
		
		/** The current keyboard character being examined */
		public int character;

		/** The current keyboard event key being examined */
		public int key;

		/** The current state of the key being examined in the event queue */
		public boolean state;
		
		public VirtualKeyEvent(final int character, final int key, final boolean state) {
			this.character = character;
			this.key = key;
			this.state = state;
		}
		
		@Override
		public final String toString() {
			return character + "!" + key + "!" + state;
		}
		
		public static final VirtualKeyEvent fromString(final String object) {
			return new VirtualKeyEvent(Integer.parseInt(object.split("!")[0]), Integer.parseInt(object.split("!")[1]), Boolean.parseBoolean(object.split("!")[2]));
		}
		
	}
	
	/**
	 * General Idea: LWJGL uses Events, these Events contain a bit of Information {@link VirtualKeyEvent}. You go trough them by running next() and then you access their Information using getEvent****. 
	 * So every time next() is called, a new VirtualKeyEvent is being created, that is slowly being filled with data, by Minecraft Code that is accessing them. So if the MC Code asks for the Event Key, then our Code puts that Information into 
	 * the VirtualKeyEvent. Replaying works the same, but instead of listening, it hacks the LWJGL Keyboard like a Man In The Middle
	 */
	
	public final static Queue<VirtualKeyEvent> keyEventsForTick = new LinkedList<>();
	public static VirtualKeyEvent currentKeyEvent;

	public static boolean isKey61Down;
	public static boolean isKey60Down;
	public static boolean isKey54Down;
	public static boolean isKey42Down;
	public static boolean isKey37Down;
	public static boolean isKey51Down;
	public static boolean isKey52Down;
	public static boolean isKey65Down;
	
	/**
	 * For the Playback Versions I have implemented an override key, that whenever you hold Left Alt, your inputs will be recognized
	 */
	public final static boolean next() {
		boolean b = Keyboard.next(); // We can poll keyboard events to free up ram usage
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			currentKeyEvent = new VirtualKeyEvent(Keyboard.getEventCharacter(), Keyboard.getEventKey(), Keyboard.getEventKeyState());
			if (b) Utils.lazyKeyboard();
			return b;
		}
		if (keyEventsForTick.isEmpty()) return false;
		currentKeyEvent = keyEventsForTick.poll();
		Utils.lazyKeyboard();
		return true;
	}

	public final static int getEventKey() {
		return currentKeyEvent.key;
	}

	public final static char getEventCharacter() {
		return (char) currentKeyEvent.character;
	}

	public final static boolean getEventKeyState() {
		return currentKeyEvent.state;
	}
	
	/**
	 * isKeyDown does not use the Packets, instead it looks through all passed Packets (aka. see if the button is actually down on the Keyboard)
	 * Update: ._. This is frame based and messes up Mouse Inputs entirely.
	 * Solution: Yeet this, and do a lazy play in next()
	 * Problem with that is, that officially left and right clicking in all Slot Menus (Singleplayer, Stats, Texture Pack, etc) is working a bit less.
	public final static boolean isKeyDown(final int i) {
		if (!hack) {
			final boolean val = Keyboard.isKeyDown(i);
			if (listen) {
				TASmod.keyboardTick(new VirtualKeyEvent(i, i, val));
			}
			return val;
		}
		for (final VirtualKeyEvent virtualKeyEvent : keyEventsForTick) 
			if (virtualKeyEvent.key == i && virtualKeyEvent.state == true) return true;
		return false;
	}
	*/
	public final static boolean isKeyDown(final int i) {
		switch (i) {
			case 61: return isKey61Down;
			case 60: return isKey60Down;
			case 54: return isKey54Down;
			case 42: return isKey42Down;
			case 37: return isKey37Down;
			case 51: return isKey51Down;
			case 52: return isKey52Down;
			case 65: return isKey65Down;
		}
		throw new RuntimeException("Unhandled Key...");
	}
	
}
