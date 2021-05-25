package net.tasmod;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.tasmod.recorder.Recorder;
import net.tasmod.replayer.Replayer;
import net.tasmod.virtual.VirtualKeyboard.VirtualKeyEvent;
import net.tasmod.virtual.VirtualMouse.VirtualMouseEvent;

/**
 * 
 * Heart of the TASmod, all stuff that needs to be accessed from different Parts of MC Code
 * 
 * @author Pancake
 */
public final class TASmod {

	/** Currently running Recording */
	private static volatile Recorder recording;
	
	/** Currently running Playback */
	private static volatile Replayer playback;
	
	/** Hacky boolean, to run Code once when MC starts, without requiring more Code Edits */
	private static boolean hasBeenTransformed;

	/** Minecraft Instance obtained via Reflection */
	public static Minecraft mc;
	
	/**
	 * Join a World and start the Recording
	 * 
	 * @param seed Create a world with this seed
	 * @return Returns whether the action was successful
	 * @throws IOException This Exception cannot be thrown, unless something is terribly wrong.
	 */
	public static final boolean startRecording(long worldseed, int worldtype, boolean hardcore, boolean mapFeatures, String folderName, String worldName) throws IOException {
		if (recording != null) return false;
		recording = new Recorder(worldseed, worldtype, hardcore, mapFeatures, folderName, worldName);
		recording.startRecording();
		return true;
	}

	/**
	 * Ticks all kinds of things
	 * @throws IOException Unexpected File End
	 */
	public static final void tick() throws IOException {
		/* Tick Recording/Playback if needed */
		if (recording != null) recording.tick();
		if (playback != null) playback.tick();
		/* Run Code when MC Ticks the First Time */
		if (!hasBeenTransformed) {
			hasBeenTransformed = true;
			try {
				TASmod.mc = Utils.obtainMinecraftInstance();
				Utils.transformStringTranslate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/* End the Recording when 'K' is pressed */
        try {
			if(net.tasmod.virtual.VirtualKeyboard.isKeyDown(37) && mc.theWorld != null) net.tasmod.TASmod.endRecording();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Whenever a new Keyboard Event gets called
	 * @param event The Keyboard Event
	 */
	public static final void keyboardTick(final VirtualKeyEvent event) {
		if (recording != null) recording.keyboardTick(event);
	}
	
	/**
	 * Whenever a new Mouse Event gets called
	 * @param event The Mouse Event
	 */
	public static final void mouseTick(final VirtualMouseEvent event) {
		if (recording != null) recording.mouseTick(event);
	}
	
	/**
	 * Ends the current recording
	 */
	public static final boolean endRecording() throws IOException {
		if (recording == null) return false;
		recording.endRecording();
		recording = null;
		return true;
	}
	
	/**
	 * @return Returns whether a Playback is running	
	 */
	public static boolean isPlayback() {
		return playback != null;
	}
	
	/**
	 * @return Returns whether a Recording is running	
	 */
	public static boolean isRecording() {
		return recording != null;
	}
	
	/**
	 * Joins the World of the Playback and starts the Playback
	 * 
	 * @param seed Create a world with this seed
	 * @return Returns whether the action was successful
	 * @throws IOException Thrown if the File doesn't exists
	 */
	public static final boolean startPlayback(String fileName) {
		if (playback != null) return false;
		try {
			playback = new Replayer(fileName);
		} catch (IOException e) {
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

	public static Recorder getRecording() {
		return recording;
	}

	public static Replayer getPlayback() {
		return playback;
	}
	
}
