package net.tasmod;

import java.io.IOException;

import net.tasmod.recorder.Recorder;
import net.tasmod.replayer.Replayer;
import net.tasmod.rng.FakeRandom;
import net.tasmod.virtual.VirtualKeyboard.VirtualKeyEvent;
import net.tasmod.virtual.VirtualMouse.VirtualMouseEvent;

/**
 * 
 * All methods that should be accessible from the Minecraft Code
 * 
 * @author Pancake
 */
public final class TASmod {

	/** Currently running Recording */
	private static volatile Recorder recording;
	
	/** Currently running Playback */
	private static volatile Replayer playback;
	
	// TODO: Sync Playback/Recording
	// TODO: Implement a Tick Start thingy
	
	/**
	 * Join a World and start the Recording
	 * 
	 * @param seed Create a world with this seed
	 * @return Returns whether the action was successful
	 * @throws IOException This Exception cannot be thrown, unless something is terribly wrong.
	 */
	public static final boolean startRecording(long worldseed, int worldtype, boolean hardcore, boolean mapFeatures, String folderName, String worldName) throws IOException {
		if (recording != null) return false;
		Utils.changeField("java.lang.Math$RandomNumberGeneratorHolder", "randomNumberGenerator", new FakeRandom(), true);
		recording = new Recorder(worldseed, worldtype, hardcore, mapFeatures, folderName, worldName);
		recording.startRecording();
		return true;
	}

	/**
	 * Ticks all kinds of things
	 * @throws IOException Unexpected File End
	 */
	public static final void tick() throws IOException {
		if (recording != null) recording.tick();
		if (playback != null) playback.tick();
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
	public static final boolean startPlayback(String fileName) throws IOException {
		if (playback != null) return false;
		Utils.changeField("java.lang.Math$RandomNumberGeneratorHolder", "randomNumberGenerator", new FakeRandom(), true);
		playback = new Replayer(fileName);
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
