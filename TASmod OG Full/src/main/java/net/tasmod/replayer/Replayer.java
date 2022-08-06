package net.tasmod.replayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.tasmod.TASmod;
import net.tasmod.main.EmulatorFrame;
import net.tasmod.random.MathRandomMod;
import net.tasmod.random.SimpleRandomMod;
import net.tasmod.recorder.Recorder;
import net.tasmod.tools.TickrateChanger;
import net.tasmod.virtual.VirtualKeyboard;
import net.tasmod.virtual.VirtualKeyboard.VirtualKeyEvent;
import net.tasmod.virtual.VirtualMouse;
import net.tasmod.virtual.VirtualMouse.VirtualMouseEvent;

/**
 * Records a Speedrun, and saves it into a File.
 * @author Pancake
 */
public class Replayer {

	protected Minecraft mc;
	protected final BufferedReader reader;
	protected final Queue<String> linesRead = new LinkedList<>();
	protected final Thread fileReader;
	public final File file;
	public int currentTick;

	/**
	 * Loads a File and reads some ticks from it
	 * @throws IOException Cannot be thrown, unless something is terribly wrong.
	 */
	public Replayer(final File name) throws Exception {
		this.file = name;
		this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
		this.fileReader = new Thread(new Runnable() {

			/**
			 * Thread that will read the file and store it in the Queue
			 */
			@Override
			public void run() {
				try {
					while (true)
						// Only read up to 20 ticks
						if (linesRead.size() < 60) {
							final String line = reader.readLine();
							if (line == null) break;
							linesRead.add(line);
						} else
							Thread.sleep(32);
					reader.close();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
		fileReader.start();
	}

	/**
	 * Start the replay
	 */
	public void startReplay() {
		this.mc = TASmod.mc;
		MathRandomMod.updateSeed(0L);
		SimpleRandomMod.updateSeed(0L);
		
		VirtualMouse.setCursorPosition(mc.displayWidth / 2, mc.displayHeight / 2);
		VirtualMouse.getDX();
		VirtualMouse.getDY();
	}

	/**
	 * Replay Read Ticks
	 */
	public void tick() {
		tickKeyboad();
		tickMouse();
		MathRandomMod.updateSeed(currentTick);
		SimpleRandomMod.updateSeed(currentTick);
		this.currentTick++;
		if (TASmod.pauseAt == this.currentTick) {
			try {
				if (!TickrateChanger.isTickAdvance) TickrateChanger.toggleTickadvance();
				TASmod.rerecord = file;
				TASmod.playback = null;
				VirtualKeyboard.hack = false;
				VirtualMouse.hack = false;
				TASmod.recording = new Recorder(currentTick);
				EmulatorFrame.save.setEnabled(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Empty method for future implementations that run in the render loop
	 */
	public void render() {

	}
		 
	private void tickKeyboad() {
		final String line = linesRead.poll();
		if (line != null) {
			if (line.isEmpty()) return;
			final Queue<VirtualKeyEvent> queue = VirtualKeyboard.keyEventsForTick;
			queue.clear();
			Arrays.asList(line.split(":")).forEach(c -> {
				queue.add(VirtualKeyEvent.fromString(c));
			});
			VirtualKeyboard.hack = true;
		} else {
			VirtualKeyboard.hack = false;
			TASmod.playback = null;
		}
	}

	private void tickMouse() {
		final String line = linesRead.poll();
		if (line != null) {
			if (line.isEmpty()) return;
			final Queue<VirtualMouseEvent> queue = VirtualMouse.mouseEventsForTick;
			queue.clear();
			Arrays.asList(line.split(":")).forEach(c -> {
				final VirtualMouseEvent c2 = VirtualMouseEvent.fromString(c);
				queue.add(c2);
				VirtualMouse.dX = c2.dX;
				VirtualMouse.dY = c2.dY;
			});
			VirtualMouse.hack = true;
		} else
			VirtualMouse.hack = false;
	}
	
	/**
	 * Whether the hud should be visible
	 */
	public boolean isVisible() {
		return true;
	}

}
