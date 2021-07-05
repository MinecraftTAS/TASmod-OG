package net.tasmod.virtual;

import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.tasmod.TASmod;
import net.tasmod.Utils;

/**
 * This is an interface of the Mouse Class.
 * This File records or manipulates these Calls
 * @author ScribbleLP, Pancake
 */
public class VirtualMouse {

	/**
	 * Internal Mouse Event used to save the Mouse to a File
	 * @author ScribbleLP
	 */
	public static final class VirtualMouseEvent {
		
		/** The current position */
		public int posX;

		/** The current position */
		public int posY;
		
		/** Is the Cursor grabbed */
		public boolean grabbed;
		
		public int dX;
		public int dY;
		
		/** Scroll wheel */
		public int eventWheel;
		
		/** Left, Right, Middle etc..*/
		public int eventButton;
		
		/* EventButton down */
		public boolean eventState;
		
		public VirtualMouseEvent(final int i, final int j, final boolean b, final int k, final int l, final boolean c, final int dX, final int dY) {
			this.posX = i;
			this.posY = j;
			this.grabbed = b;
			this.eventWheel = k;
			this.eventButton = l;
			this.eventState = c;
			this.dX = dX;
			this.dY = dY;
		}
		
		@Override
		public final String toString() {
			return posX + "!" + posY + "!" + grabbed + "!" + eventWheel + "!" + eventButton + "!" + eventState + "!" + dX + "!" + dY;
		}
		
		public static final VirtualMouseEvent fromString(final String object) {
			return new VirtualMouseEvent(Integer.parseInt(object.split("!")[0]), Integer.parseInt(object.split("!")[1]), Boolean.parseBoolean(object.split("!")[2]), Integer.parseInt(object.split("!")[3]), Integer.parseInt(object.split("!")[4]), Boolean.parseBoolean(object.split("!")[5]), Integer.parseInt(object.split("!")[6]), Integer.parseInt(object.split("!")[7]));
		}
		
	}
	
	/**
	 * General Idea: LWJGL uses Events, these Events contain a bit of Information {@link VirtualMouseEvent}. You go trough them by running next() and then you access their Information using getEvent****. 
	 * So every time next() is called, a new VirtualMouseEvent is being created, that is slowly being filled with data, by Minecraft Code that is accessing them. So if the MC Code asks for the Mouse Button, then our Code puts that Information into 
	 * the VirtualMouseEvent. Replaying works the same, but instead of listening, it hacks the LWJGL Mouse like a Man In The Middle
	 */
	
	public final static Queue<VirtualMouseEvent> mouseEventsForTick = new LinkedList<>();
	public static VirtualMouseEvent currentMouseEvents = new VirtualMouseEvent(0, 0, false, 0, 0, false, 0, 0);
	
	public static int dX;
	public static int dY;
	
	/**
	 * For the Playback Versions I have implemented an override key, that whenever you hold Left Alt, your inputs will be recognized
	 */
	public final static boolean next() {
		boolean b = Mouse.next(); // We can poll the Mouse Events to free up ram usage
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			currentMouseEvents = new VirtualMouseEvent(Mouse.getEventX(), Mouse.getEventY(), Mouse.isGrabbed(), Mouse.getEventDWheel(), Mouse.getEventButton(), Mouse.getEventButtonState(), Mouse.getDX(), Mouse.getDY());
			if (b) Utils.lazyMouse();
			return b;
		}
		if (mouseEventsForTick.isEmpty()) return false;
		currentMouseEvents = mouseEventsForTick.poll();
		Utils.lazyMouse();
		return true;
	}
	
	public final static void setGrabbed(final boolean b) {
		Mouse.setGrabbed(currentMouseEvents.grabbed);
	}

	public final static void setCursorPosition(final int i, final int j) {
		Mouse.setCursorPosition(currentMouseEvents.posX, currentMouseEvents.posY);
	}

	public final static boolean getEventButtonState() {
		return currentMouseEvents.eventState;
	}

	public final static int getEventDWheel() {
		return currentMouseEvents.eventWheel;
	}

	public final static int getEventButton() {
		return currentMouseEvents.eventButton;
	}

	public final static int getEventX() {
		return currentMouseEvents.posX;
	}

	public final static int getEventY() {
		return currentMouseEvents.posY;
	}
	
	/**
	 * This Method is being called at only one point, that one being EntityRenderer, which is frame based. To make it be tick based, we replace this value with one, that is based on Ticks
	 */
	public final static int getX() {
		return Utils.lastX;
	}
	/**
	 * This Method is being called at only one point, that one being EntityRenderer, which is frame based. To make it be tick based, we replace this value with one, that is based on Ticks
	 */
	public final static int getY() {
		return Utils.lastY;
	}
	
	/**
	 * To make the Interpolation work, we are not asking LWJGL, but a Custom Util, that adds all the LWJGL Calls per Tick. 
	 * Does not change anything with the Mouse
	 */
	public final static int getDX() {
		return dX;
	}

	/**
	 * To make the Interpolation work, we are not asking LWJGL, but a Custom Util, that adds all the LWJGL Calls per Tick. 
	 * Does not change anything with the Mouse
	 */
	public final static int getDY() {
		return dY;
	}
	
	/**
	 * isButtonDown does not use the Packets, instead it looks through all passed Packets (aka. see if the button is actually down on the Mouse)
	 *
	 * Update: ._. This is frame based and messes up Mouse Inputs entirely.
	 * Solution: Yeet this, and do a lazy play in next(). 
	 * Problem with that is, that officially left and right clicking in all Slot Menus (Singleplayer, Stats, Texture Pack, etc) is working a bit less.
	public final static boolean isButtonDown(final int i) {
		if (!hack) {
			boolean val = Mouse.isButtonDown(i);
			if (listen) {
				currentlyListening.eventButton = i;
				currentlyListening.eventState = val;
			}
			return val;
		}
		for (final VirtualMouseEvent virtualMouseEvent : mouseEventsForTick) 
			if (virtualMouseEvent.eventState == true && virtualMouseEvent.eventButton == i) return true;
		return false;
	}
	 */
	public final static boolean isButtonDown(final int i) {
		switch (i) {
			case 0: return isButton0Down;
		}
		throw new RuntimeException("Unhandled Key...");
	}
	
	public static boolean isButton0Down;
	
}
