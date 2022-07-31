package net.tasmod;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.tasmod.infogui.InfoHud;
import net.tasmod.main.EmulatorFrame;
import net.tasmod.main.Start;
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
	public static volatile Recorder recording;

	/** Currently running Playback */
	public static volatile Replayer playback;

	/** Hacky boolean, to run Code once when MC starts, without requiring more Code Edits */
	private static boolean hasBeenTransformed;

	/** Minecraft Instance obtained via Reflection */
	public static Minecraft mc;

	/** The only Info Hud Instance :CsGun: */
	public static InfoHud infoHud = new InfoHud();
	
	/** Temporary variable to avoid pressing a key twice */
	private static boolean _was66pressed;
	
	/** Temporary variable to avoid pressing a key twice */
	private static boolean _was67pressed;
	
	/** Temporary variable to avoid pressing a key twice */
	private static boolean _was51pressed;
	
	/** Temporary variable to avoid pressing a key twice */
	private static boolean _was52pressed;
	
	/** Temporary variable to avoid pressing a key twice */
	private static boolean _was62pressed;
	
	/** Temporary variable for tickrate zero to work */
	private static boolean _undoTickrate;
	
	/** Whether a recording should start */
	public static boolean startRecording;
	
	/** Whether a playback should start */
	public static boolean startPlayback;
	
	/** Whether the current TAS is being rerecorded */
	public static File rerecord;
	
	/** The Thread that minecraft runs on */
	public static Thread mcThread;
	
	/** Tick to pause playback at */
	public static int pauseAt = -1;
	
	/** Synchronize */
	public static boolean wait;
	
	/**
	 * Ticks all kinds of things
	 * @throws IOException Unexpected File End
	 */
	public static void tick() {
		/* During a playback, ask the user if TAS should be rerecorded from here */
		if (playback != null && Keyboard.isKeyDown(Keyboard.KEY_F3))
			pauseAt = playback.currentTick + 1; // pause on this tick
		/* During a recording, ask the user if TAS should be saved here */
		if (recording != null && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
			GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
			EmulatorFrame.save.doClick();
		}
		/* Tick Recording/Playback if needed */
		if (recording != null) recording.tick();
		if (playback != null) playback.tick();
		/* Run Code when MC Ticks the First Time */
		if (!hasBeenTransformed) {
			hasBeenTransformed = true;
			try {
				TASmod.mc = Utils.obtainMinecraftInstance();
			} catch (final Exception e) {
				e.printStackTrace();
			}
			if (startRecording) {
				recording = new Recorder(0); // Start a new Recording
				startRecording = false;
			} else if (startPlayback) {
				playback.startReplay();
				startPlayback = false;
			}
		}
		/* Handle keybinds and tick advance */
		try {
			if (Keyboard.isKeyDown(64)) TASmod.mc.displayGuiScreen(infoHud);
			if (_undoTickrate ? !(_undoTickrate = !_undoTickrate) : false)
				TickrateChanger.updateTickrate(0f);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if (TASmod.wait) {
			TASmod.wait = false;
			try {
				if (!TickrateChanger.isTickAdvance) TickrateChanger.toggleTickadvance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			new Thread(() -> {
				File outFile = TASmod.rerecord;
				if (outFile == null) {
					GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
					final String out = JOptionPane.showInputDialog("Enter a name for the TAS", "");
					if (out == null) return;
					outFile = new File(Start.tasDir, out);
					TASmod.recording.endRecording();
					try {
						TASmod.recording.saveTo(outFile);
					} catch (final Exception e1) {
						e1.printStackTrace();
					}
				} else {
					TASmod.recording.endRecording();
					try {
						TASmod.recording.saveTo(outFile, TASmod.recording.startingTick);
					} catch (final Exception e1) {
						e1.printStackTrace();
					}
				}
				EmulatorFrame.save.setEnabled(false);				
			}).start();
		}
		/* Update the Label */
		int currentTick = 0;
		if (playback != null) currentTick = playback.currentTick;
		else if (recording != null) currentTick = recording.currentTick;
		final String label = String.format("Resolution: %dx%d, Gamespeed: %.2f, Current Tick: %d, F4 to toggle the menu" + ((playback != null) ? ", F3 to rerecord from here." : "."), mc.displayWidth, mc.displayHeight, TickrateChanger.availableGamespeeds[TickrateChanger.selectedGamespeed], currentTick);
		EmulatorFrame.label.setText(label);
	}

	/**
	 * Ticks frame based stuff.
	 * @throws Exception Throws Exception whenever something bad happens
	 */
	public static void render() {
		if (playback != null) playback.render();
		try {
			if (!_was51pressed && Keyboard.isKeyDown(51)) TickrateChanger.slower();
			if (!_was52pressed && Keyboard.isKeyDown(52)) TickrateChanger.faster();
			_was51pressed = Keyboard.isKeyDown(51);
			_was52pressed = Keyboard.isKeyDown(52);
			if (Keyboard.isKeyDown(66) && !_was66pressed) {
				TickrateChanger.toggleTickadvance();
			}
			_was66pressed = Keyboard.isKeyDown(66);
			if (Keyboard.isKeyDown(62) && !_was62pressed) {
				EmulatorFrame.bar.setVisible(!EmulatorFrame.bar.isVisible());
				EmulatorFrame.label.setVisible(EmulatorFrame.bar.isVisible());
			}
			_was62pressed = Keyboard.isKeyDown(62);
			if (Keyboard.isKeyDown(67) && !_was67pressed && playback == null && TickrateChanger.isTickAdvance) {
				TickrateChanger.updateTickrate(TickrateChanger.availableGamespeeds[TickrateChanger.selectedGamespeed]);
				_undoTickrate = true;
			}
			_was67pressed = Keyboard.isKeyDown(67);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Whenever a new Keyboard Event gets called
	 * @param event The Keyboard Event
	 */
	public static void keyboardTick(final VirtualKeyEvent event) {
		if (recording != null) recording.keyboardTick(event);
	}

	/**
	 * Whenever a new Mouse Event gets called
	 * @param event The Mouse Event
	 */
	public static void mouseTick(final VirtualMouseEvent event) {
		if (recording != null) recording.mouseTick(event);
	}

}
