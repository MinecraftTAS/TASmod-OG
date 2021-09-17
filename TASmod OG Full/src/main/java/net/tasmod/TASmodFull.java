package net.tasmod;

import org.lwjgl.input.Mouse;

import net.minecraft.src.GuiSlot;

/**
 * This Class contains features that are only used in the Full Version of TASmod that contains all tools to create TASes
 * @author Pancake
 */
public class TASmodFull {

	/**
	 * This method bypasses ASM and checks the actual Mouse for a press, this is used in {@link GuiSlot} because otherwise one would have to mash the button to enter a world
	 * @param i Button to check
	 * @return Whether the button is checked
	 */
	public static boolean checkRealMouseButtonDown(int i) {
		return Mouse.isButtonDown(i);
	}
	
}
