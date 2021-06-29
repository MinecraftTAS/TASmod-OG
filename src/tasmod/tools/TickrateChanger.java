package net.tasmod.tools;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Timer;
import net.tasmod.TASmod;
import net.tasmod.Utils;

/**
 * The TickrateChanger is the part of the mod, that slows down the game to allow for more precise inputs.
 * @author Pancake
 */
public class TickrateChanger {
 
	/** Whether the game is currently in tick advance mode */
	public static boolean isTickAdvance;
	/** Selected index from array below */
	public static int selectedGamespeed = 6;
	/** Array of all available gamespeeds */
	public static float[] availableGamespeeds = new float[] {
		0.025f, // 0
		0.05f, // 1
		0.1f, // 2
		0.2f, // 3
		0.4f, // 4
		0.5f, // 5
		1f, // 6
		2.0f // 7
	};
	
	/**
	 * Increases the gamespeed by one (see {@link #availableGamespeeds}).
	 */
	public static void faster() {
		if (selectedGamespeed != (availableGamespeeds.length - 1)) selectedGamespeed++;
		try {
			updateTickrate(availableGamespeeds[selectedGamespeed]);
		} catch (Exception e) {
			System.err.println("Error changing gamespeed...");
			e.printStackTrace();
		}
	}
	
	/**
	 * Decreases the gamespeed by one (see {@link #availableGamespeeds}).
	 */
	public static void slower() {
		if (selectedGamespeed != 0) selectedGamespeed--;
		try {
			updateTickrate(availableGamespeeds[selectedGamespeed]);
		} catch (Exception e) {
			System.err.println("Error changing gamespeed...");
			e.printStackTrace();
		}
	}
	
	/**
	 * Enters or leaves tick advance
	 */
	public static void toggleTickadvance() throws Exception {
		if (!(isTickAdvance = !isTickAdvance)) {
			updateTickrate(availableGamespeeds[selectedGamespeed]);
		} else {
			updateTickrate(0f);
		}
	}
	
	/**
	 * This method is used to slow down the game. It replaces the 'timerSpeed' field in the Timer class.
	 * @param gamespeed New Speed of game (1.0 = normal speed)
	 * @throws Exception Throws exception whenever invalid reflection target is set
	 */
	public static void updateTickrate(float gamespeed) throws Exception {
		/* Get Field in Obfuscated or Non-Obfuscated Environment */
		Field translateTableField;
		try {
			/* Non-Obfuscated net.minecraft.src.Timer.timerSpeed */
			translateTableField = Class.forName("net.minecraft.src.Timer").getDeclaredField("timerSpeed");
		} catch (Exception e) {
			/* Obfuscated: aij.d */
			translateTableField = Class.forName("aij").getDeclaredField("d");
		}
		/* Update Game Speed */
		translateTableField.setAccessible(true);
		translateTableField.setFloat(Utils.obtainTimerInstance(), gamespeed);
	}
	
}
