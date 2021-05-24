package net.tasmod.virtual;

import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.input.Mouse;

import net.tasmod.TASmod;
import net.tasmod.Utils;

public class VirtualMouse {

	public static final class VirtualMouseEvent {
		
		/** The current position */
		public int posX;

		/** The current position */
		public int posY;
		
		/** MAC only, is the Cursor grabbed */
		public boolean grabbed;
		
		public int dX;
		public int dY;
		
		/** Scroll wheel */
		public int wheel;
		
		/** Left, Right, Middle etc..*/
		public int eventButton;
		
		/* EventButton down */
		public boolean eventState;
		
		public VirtualMouseEvent(final int i, final int j, final boolean b, final int k, final int l, final boolean c, final int dX, final int dY) {
			this.posX = i;
			this.posY = j;
			this.grabbed = b;
			this.wheel = k;
			this.eventButton = l;
			this.eventState = c;
			this.dX = dX;
			this.dY = dY;
		}
		
		@Override
		public final String toString() {
			return posX + "!" + posY + "!" + grabbed + "!" + wheel + "!" + eventButton + "!" + eventState + "!" + dX + "!" + dY;
		}
		
		public static final VirtualMouseEvent fromString(final String object) {
			return new VirtualMouseEvent(Integer.parseInt(object.split("!")[0]), Integer.parseInt(object.split("!")[1]), Boolean.parseBoolean(object.split("!")[2]), Integer.parseInt(object.split("!")[3]), Integer.parseInt(object.split("!")[4]), Boolean.parseBoolean(object.split("!")[5]), Integer.parseInt(object.split("!")[6]), Integer.parseInt(object.split("!")[7]));
		}
		
	}
	
	public final static Queue<VirtualMouseEvent> mouseEventsForTick = new LinkedList<>();
	public static VirtualMouseEvent currentMouseEvents = new VirtualMouseEvent(0, 0, false, 0, 0, false, 0, 0);
	public static boolean hack = false;
	
	public static boolean listen = false;
	public static VirtualMouseEvent currentlyListening = new VirtualMouseEvent(-1, -1, false, -1, -1, false, 0, 0);
	
	public static int dX;
	public static int dY;
	
	public static boolean isButtonDown(final int i) {
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
	
	public static boolean next() {
		if (listen) {
			if (currentlyListening != null) {
				TASmod.mouseTick(currentlyListening);
				currentlyListening = new VirtualMouseEvent(currentlyListening.posX, currentlyListening.posY, false, 0, -1, false, 0, 0);
			} else {
				currentlyListening = new VirtualMouseEvent(-1, -1, false, 0, -1, false, 0, 0);
			}
		}
		if (!hack) return Mouse.next();
		if (mouseEventsForTick.isEmpty()) return false;
		currentMouseEvents = mouseEventsForTick.poll();
		return true;
	}
	
	public static void setGrabbed(final boolean b) {
		if (!hack) {
			Mouse.setGrabbed(b);
			if (listen) {
				currentlyListening.grabbed = true;
			}
			return;
		}
		Mouse.setGrabbed(currentMouseEvents.grabbed);
	}

	public static void setCursorPosition(final int i, final int j) {
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

	public static boolean getEventButtonState() {
		if (!hack) {
			boolean val = Mouse.getEventButtonState();
			if (listen) {
				currentlyListening.eventState = val;
			}
			return val;
		}
		return currentMouseEvents.eventState;
	}

	public static int getEventDWheel() {
		if (!hack) {
			int val = Mouse.getEventDWheel();
			if (listen) {
				currentlyListening.wheel = val;
			}
			return val;
		}
		return currentMouseEvents.wheel;
	}

	public static int getEventButton() {
		if (!hack) {
			int val = Mouse.getEventButton();
			if (listen) {
				currentlyListening.eventButton = val;
			}
			return val;
		}
		return currentMouseEvents.eventButton;
	}

	public static int getEventX() {
		if (!hack) {
			int val = Mouse.getEventX();
			if (listen) {
				currentlyListening.posX = val;
			}
			return val;
		}
		return currentMouseEvents.posX;
	}

	public static int getEventY() {
		if (!hack) {
			int val = Mouse.getEventY();
			if (listen) {
				currentlyListening.posY = val;
			}
			return val;
		}
		return currentMouseEvents.posY;
	}
	
	public static int getX() {
		return getEventX();
	}

	public static int getY() {
		return getEventY();
	}
	
	// Calls the Fake Utils
	public static int getDX() {
		if (!hack) {
			int val = Utils.getDX();
			if (listen) {
				dX = val;
			}
			return val;
		}
		return dX;
	}

	public static int getDY() {
		if (!hack) {
			int val = Utils.getDY();
			if (listen) {
				dY = val;
			}
			return val;
		}
		return dY;
	}
	
}
