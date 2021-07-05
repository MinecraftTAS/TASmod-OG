package net.tasmod.virtual;

import java.util.LinkedList;
import java.util.Queue;

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
	public static boolean hack = false;
	
	public static boolean listen = false;
	public static VirtualMouseEvent currentlyListening = new VirtualMouseEvent(-1, -1, false, -1, -1, false, 0, 0);
	
	public static int dX;
	public static int dY;
	
	public final static boolean next() {
		if (listen) {
			if (currentlyListening != null) {
				TASmod.mouseTick(currentlyListening);
				currentlyListening = new VirtualMouseEvent(currentlyListening.posX, currentlyListening.posY, false, 0, -1, false, 0, 0);
			} else {
				currentlyListening = new VirtualMouseEvent(-1, -1, false, 0, -1, false, 0, 0);
			}
		}
		if (!hack) {
			boolean b = Mouse.next();
			if (b) Utils.lazyMouse();
			return b;
		}
		if (mouseEventsForTick.isEmpty()) return false;
		currentMouseEvents = mouseEventsForTick.poll();
		Utils.lazyMouse();
		return true;
	}
	
	public final static void setGrabbed(final boolean b) {
		if (!hack) {
			Mouse.setGrabbed(b);
			if (listen) {
				currentlyListening.grabbed = true;
			}
			return;
		}
		Mouse.setGrabbed(currentMouseEvents.grabbed);
	}

	public final static void setCursorPosition(final int i, final int j) {
		if (!hack) {
			Mouse.setCursorPosition(i, j);
			if (listen) {
				currentlyListening.posX = i;
				currentlyListening.posY = j;
			}
			return;
		}
		Mouse.setCursorPosition(currentMouseEvents.posX, currentMouseEvents.posY);
	}

	public final static boolean getEventButtonState() {
		if (!hack) {
			boolean val = Mouse.getEventButtonState();
			if (listen) {
				currentlyListening.eventState = val;
			}
			return val;
		}
		return currentMouseEvents.eventState;
	}

	public final static int getEventDWheel() {
		if (!hack) {
			int val = Mouse.getEventDWheel();
			if (listen) {
				currentlyListening.eventWheel = val;
			}
			return val;
		}
		return currentMouseEvents.eventWheel;
	}

	public final static int getEventButton() {
		if (!hack) {
			int val = Mouse.getEventButton();
			if (listen) {
				currentlyListening.eventButton = val;
			}
			return val;
		}
		return currentMouseEvents.eventButton;
	}

	public final static int getEventX() {
		if (!hack) {
			int val = Mouse.getEventX();
			if (listen) {
				currentlyListening.posX = val;
			}
			return val;
		}
		return currentMouseEvents.posX;
	}

	public final static int getEventY() {
		if (!hack) {
			int val = Mouse.getEventY();
			if (listen) {
				currentlyListening.posY = val;
			}
			return val;
		}
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
		if (!hack) {
			int val = Utils.getDX();
			if (listen) {
				dX = val;
			}
			return val;
		}
		return dX;
	}

	/**
	 * To make the Interpolation work, we are not asking LWJGL, but a Custom Util, that adds all the LWJGL Calls per Tick. 
	 * Does not change anything with the Mouse
	 */
	public final static int getDY() {
		if (!hack) {
			int val = Utils.getDY();
			if (listen) {
				dY = val;
			}
			return val;
		}
		return dY;
	}
	
	/**
	 * isButtonDown does not use the Packets, instead it looks through all passed Packets (aka. see if the button is actually down on the Mouse)
	 *
	 * Update: ._. This is frame based and messes up Mouse Inputs entirely.
	 * Solution: Yeet this, and do a lazy play in next()
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
