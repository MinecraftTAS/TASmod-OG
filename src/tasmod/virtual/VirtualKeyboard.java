package net.tasmod.virtual;

import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.input.Keyboard;

import net.tasmod.TASmod;

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
	public static boolean hack = false;
	
	public static boolean listen = false;
	public static VirtualKeyEvent currentlyListening;
	
	/**
	 * isKeyDown does not use the Packets, instead it looks through all passed Packets (aka. see if the button is actually down on the Keyboard)
	 */
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
	
	public final static boolean next() {
		if (listen) {
			if (currentlyListening != null) {
				TASmod.keyboardTick(currentlyListening);
			}
			currentlyListening = new VirtualKeyEvent(-1, -1, false);
		}
		if (!hack) {
			
			return Keyboard.next();
		}
		if (keyEventsForTick.isEmpty()) return false;
		currentKeyEvent = keyEventsForTick.poll();
		return true;
	}

	public final static int getEventKey() {
		if (!hack) {
			final int val = Keyboard.getEventKey();
			if (listen) {
				currentlyListening.key = val;
			}
			return val;
		}
		return currentKeyEvent.key;
	}

	public final static char getEventCharacter() {
		if (!hack) {
			final int val = Keyboard.getEventCharacter();
			if (listen) {
				currentlyListening.character = val;
			}
			return (char) val;
		}
		return (char) currentKeyEvent.character;
	}

	public final static boolean getEventKeyState() {
		if (!hack) {
			final boolean val = Keyboard.getEventKeyState();
			if (listen) {
				currentlyListening.state = val;
			}
			return val;
		}
		return currentKeyEvent.state;
	}
	
}
