package net.tasmod;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.tasmod.infogui.InfoHud;
import net.tasmod.recorder.Recorder;
import net.tasmod.replayer.Replayer;
import net.tasmod.tools.TickrateChanger;
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
	
	/** The only Info Hud Instance :CsGun: */
	public static InfoHud infoHud = new InfoHud();
	
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
	public static final void tick() {
		/* Tick Recording/Playback if needed */
		if (recording != null) recording.tick();
		if (playback != null) playback.tick();
		/* Run Code when MC Ticks the First Time */
		if (!hasBeenTransformed) {
			hasBeenTransformed = true;
			try {
				TASmod.mc = Utils.obtainMinecraftInstance();
				Utils.transformStringTranslate();
				Utils.transformRandom();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/* Handle keybinds and tick advance */
        try {
			if (Keyboard.isKeyDown(64)) TASmod.mc.displayGuiScreen(infoHud);
			if ((_undoTickrate) ? !(_undoTickrate = !_undoTickrate) : false) {
				TickrateChanger.updateTickrate(0f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        infoHud.tick();
	}
	
	/** Temporary variable to avoid pressing a key twice */
	private static boolean _was66pressed;
	/** Temporary variable to avoid pressing a key twice */
	private static boolean _was67pressed;
	/** Temporary variable to avoid pressing a key twice */
	private static boolean _was51pressed;
	/** Temporary variable to avoid pressing a key twice */
	private static boolean _was52pressed;
	/** Temporary variable for tickrate zero to work */
	private static boolean _undoTickrate;
	
	/**
	 * Ticks frame based stuff.
	 * @throws Exception Throws Exception whenever something bad happens
	 */
	public static final void render() {
		try {
			if (!_was51pressed && Keyboard.isKeyDown(51)) TickrateChanger.slower();
			if (!_was52pressed && Keyboard.isKeyDown(52)) TickrateChanger.faster();
			_was51pressed = Keyboard.isKeyDown(51);
			_was52pressed = Keyboard.isKeyDown(52);
			if (Keyboard.isKeyDown(66) && !_was66pressed) TickrateChanger.toggleTickadvance();
			_was66pressed = Keyboard.isKeyDown(66);
			if (Keyboard.isKeyDown(67) && !_was67pressed && !isPlayback() && TickrateChanger.isTickAdvance) {
				TickrateChanger.updateTickrate(TickrateChanger.availableGamespeeds[TickrateChanger.selectedGamespeed]);
				_undoTickrate = true;
			}
			_was67pressed = Keyboard.isKeyDown(67);
		} catch (Exception e) {
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

	public static Recorder getRecording() {
		return recording;
	}

	public static Replayer getPlayback() {
		return playback;
	}
	
}
