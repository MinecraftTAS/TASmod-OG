package net.tasmod;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.tasmod.replayer.Replayer;

/**
 *
 * Heart of the TASmod, all stuff that needs to be accessed from different Parts of MC Code
 *
 * @author Pancake
 */
public final class TASmod {

	/** Currently running Playback */
	public static volatile Replayer playback;

	/** Hacky boolean, to run Code once when MC starts, without requiring more Code Edits */
	private static boolean hasBeenTransformed;

	/** Minecraft Instance obtained via Reflection */
	public static Minecraft mc;
	
	/** Whether a playback should start */
	public static boolean startPlayback;

	/**
	 * Ticks all kinds of things
	 * @throws IOException Unexpected File End
	 */
	public static void tick() {
		/* Tick Playback if needed */
		if (playback != null) playback.tick();
		/* Run Code when MC Ticks the First Time */
		if (!hasBeenTransformed) {
			hasBeenTransformed = true;
			try {
				TASmod.mc = Utils.obtainMinecraftInstance();
			} catch (final Exception e) {
				e.printStackTrace();
			}
			if (startPlayback) {
				playback.startReplay();
				startPlayback = false;
			}
		}
	}

	/**
	 * Ticks frame based stuff.
	 * @throws Exception Throws Exception whenever something bad happens
	 */
	public static void render() {
		
	}

	/** The Thread that minecraft runs on */
	public static Thread mcThread;

}
