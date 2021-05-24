package net.tasmod.virtual;

import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import net.tasmod.TASmod;

public final class VirtualKeyboard {

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
	
	// TODO: Reimplement isKeyDown with a Virtual Keyboard
	
	public final static Queue<VirtualKeyEvent> keyEventsForTick = new LinkedList<>();
	public static VirtualKeyEvent currentKeyEvent;
	public static boolean hack = false;
	
	public static boolean listen = false;
	public static VirtualKeyEvent currentlyListening;
	
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

	/* ======================================== Methods below this don't have to be edited. ======================================== */
	
	public final static String getKeyName(final int i) {
		return Keyboard.getKeyName(i);
	}
	
	public final static void destroy() {
		Keyboard.destroy();
	}
	
	public final static void create() throws LWJGLException {
		Keyboard.create();
	}

	// TODO: Yeah, this needs to be edited
	public final static void enableRepeatEvents(final boolean b) {
		Keyboard.enableRepeatEvents(b);
	}
	
}
