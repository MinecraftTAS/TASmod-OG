package net.tasmod;

import java.awt.FileDialog;
import java.awt.Frame;
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

	/** Task to run next tick */
	public static Runnable runMe;
	
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
				Utils.transformRandom();
				System.setProperty("java.awt.headless", "false"); // Believe it or not, this works
				openTASPicker();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (runMe != null) {
			runMe.run();
			runMe = null;
		}
	}
	
	/** Time when the last TAS Picker was opened */
	private static long timeLastOpen;
	
	/**
	 * Opens a File Picker, where you can start a TAS
	 */
	public static void openTASPicker() {
		if ((System.currentTimeMillis() - timeLastOpen) < 1000) return; // Avoid double clicking
		timeLastOpen = System.currentTimeMillis();
		new Thread(() -> {
			FileDialog taspicker = new FileDialog((Frame) null, "Pick a TAS to play", FileDialog.LOAD);
			taspicker.setMultipleMode(false);
			try {
				taspicker.setDirectory(System.getenv("AppData") + "\\.minecraft");
			} catch (Exception e) {
				// not on win
			}
			taspicker.setVisible(true);
			File tasFile = taspicker.getFiles()[0];
			if (tasFile != null)
				runMe = () -> {
					startPlayback(tasFile);
				};
		}).start();
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
	public static final boolean startPlayback(File tasFile) {
		if (playback != null) return false;
		try {
			playback = new Replayer(tasFile);
		} catch (Exception e) {
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
