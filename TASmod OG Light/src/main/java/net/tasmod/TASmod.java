package net.tasmod;

import java.io.File;
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
	private static volatile Replayer playback;

	/** Hacky boolean, to run Code once when MC starts, without requiring more Code Edits */
	private static boolean hasBeenTransformed;

	/** Minecraft Instance obtained via Reflection */
	public static Minecraft mc;

	/** TAS File to play back if set */
	public static File tasFile;

	/**
	 * Ticks all kinds of things
	 * @throws IOException Unexpected File End
	 */
	public static final void tick() {
		/* Tick Playback if needed */
		if (playback != null) playback.tick();
		/* Run Code when MC Ticks the First Time */
		if (!hasBeenTransformed) {
			hasBeenTransformed = true;
			try {
				TASmod.mc = Utils.obtainMinecraftInstance();
				// start a playback or recording if needed
				if (tasFile != null) {
					playback = new Replayer(tasFile);
					playback.startReplay();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return Returns whether a Playback is running
	 */
	public static boolean isPlayback() {
		return playback != null;
	}

	/**
	 * Joins the World of the Playback and starts the Playback
	 *
	 * @param seed Create a world with this seed
	 * @return Returns whether the action was successful
	 * @throws IOException Thrown if the File doesn't exists
	 */
	public static final boolean startPlayback(final File tasFile) {
		if (playback != null) return false;
		try {
			playback = new Replayer(tasFile);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		playback.startReplay();
		return true;
	}

	/**
	 * End the current Playback
	 */
	public static void endPlayback() {
		playback = null;
	}

	public static Replayer getPlayback() {
		return playback;
	}

}
