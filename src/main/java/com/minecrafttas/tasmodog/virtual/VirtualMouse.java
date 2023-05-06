package com.minecrafttas.tasmodog.virtual;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.minecrafttas.tasmodog.TASmod;
import com.minecrafttas.tasmodog.container.InputContainer;
import com.minecrafttas.tasmodog.virtual.structs.MouseEvent;

public class VirtualMouse {

	private static MouseEvent mouseEvent = new MouseEvent(-1, false, 0, 0, 0);
	private static List<Integer> buttonsPressed = new ArrayList<>(16);
	
	public static boolean next() {
		InputContainer inputContainer = TASmod.instance.getInputContainer();
		MouseEvent nextMouseEvent = inputContainer.getCurrentTickData().pollMouseEvent();
		boolean next = nextMouseEvent != null;
		
		// update from real mouse if recording
		if (inputContainer.isRecording() || !inputContainer.isActive()) {
			// fetch event
			next = Mouse.next();
			nextMouseEvent = new MouseEvent(Mouse.getEventButton(), Mouse.getEventButtonState(), Mouse.getEventDWheel(), Mouse.getEventX(), Mouse.getEventY());
			
			// push event
			if (next && inputContainer.isActive())
				inputContainer.getCurrentTickData().addMouseEvent(nextMouseEvent);
		}
		
		// update mouse if new event
		if (next) {
			mouseEvent = nextMouseEvent;
			if (nextMouseEvent.getState())
				buttonsPressed.add(nextMouseEvent.getButton());
			else
				buttonsPressed.remove((Integer) nextMouseEvent.getButton());
		}
		
		return next;
	}
	
	public static boolean isButtonDown(int i) {
		return buttonsPressed.contains((Integer) i);
	}
	
	public static boolean getEventButtonState() {
		return mouseEvent.getState();
	}
	
	public static int getEventButton() {
		return mouseEvent.getButton();
	}
	
	public static int getEventDWheel() {
		return mouseEvent.getWheel();
	}
	
	public static int getEventX() {
		return mouseEvent.getX();
	}
	
	public static int getEventY() {
		return mouseEvent.getY();
	}
	
	public static int getX() {
		return mouseEvent.getX();
	}

	public static int getY() {
		return mouseEvent.getY();
	}
	
	public static int getDX() {
		int dx = Mouse.getDX();
		TASmod.instance.getInputContainer().getCurrentTickData().dx += dx;
		return dx;
	}

	public static int getDY() {
		int dy = Mouse.getDY();
		TASmod.instance.getInputContainer().getCurrentTickData().dy += dy;
		return dy;
	}
	
	public static int getVirtualDX() {
		return TASmod.instance.getInputContainer().getCurrentTickData().dx;
	}

	public static int getVirtualDY() {
		return TASmod.instance.getInputContainer().getCurrentTickData().dy;
	}
	
}
