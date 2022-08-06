package net.tasmod.recorder;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JOptionPane;

import net.minecraft.client.Minecraft;
import net.tasmod.TASmod;
import net.tasmod.main.EmulatorFrame;
import net.tasmod.main.Start;
import net.tasmod.random.MathRandomMod;
import net.tasmod.random.SimpleRandomMod;
import net.tasmod.virtual.VirtualKeyboard;
import net.tasmod.virtual.VirtualKeyboard.VirtualKeyEvent;
import net.tasmod.virtual.VirtualMouse;
import net.tasmod.virtual.VirtualMouse.VirtualMouseEvent;

/**
 * Records a Speedrun, and saves it into a File.
 * @author Pancake
 */
public final class Recorder {

	private final Minecraft mc;
	private final Queue<String> linesToPrint = new LinkedList<>();

	public int currentTick;
	public int startingTick;
	
	/**
	 * Prepares the Recording File
	 * @param name The TAS name
	 * @throws IOException Cannot be thrown, unless something is terribly wrong.
	 */
	public Recorder(int tick) {
		this.mc = TASmod.mc;

		VirtualMouse.setCursorPosition(mc.displayWidth / 2, mc.displayHeight / 2);
		VirtualMouse.getDX();
		VirtualMouse.getDY();

		VirtualKeyboard.listen = true;
		VirtualMouse.listen = true;
		currentTick = tick;
		startingTick = tick;
	}

	private volatile List<VirtualKeyEvent> keyEventsPerTick = new ArrayList<>();
	private volatile List<VirtualMouseEvent> mouseEventsPerTick = new ArrayList<>();

	/**
	 * Write all Keybindings into the File
	 */
	public void tick() {
		while (true) {
			String events = "";
			if (keyEventsPerTick.size() == 0) {
				linesToPrint.add("\n");
				break;
			}
			for (final VirtualKeyEvent virtualKeyEvent : keyEventsPerTick)
				events += virtualKeyEvent + ":";
			linesToPrint.add(events.substring(0, events.length() - 1) + "\n");
			keyEventsPerTick.clear();
			break;
		}
		while (true) {
			String events = "";
			if (mouseEventsPerTick.size() == 0) {
				linesToPrint.add("\n");
				break;
			}
			for (final VirtualMouseEvent virtualMouseEvent : mouseEventsPerTick) {
				virtualMouseEvent.dX = VirtualMouse.dX;
				virtualMouseEvent.dY = VirtualMouse.dY;
				events += virtualMouseEvent + ":";
			}
			linesToPrint.add(events.substring(0, events.length() - 1) + "\n");
			mouseEventsPerTick.clear();
			break;
		}
		this.currentTick++;
		MathRandomMod.updateSeed(currentTick);
		SimpleRandomMod.updateSeed(currentTick);
	}

	/**
	 * End the Recording
	 */
	public void endRecording() {
		VirtualKeyboard.listen = false;
		VirtualMouse.listen = false;
	}

	/**
	 * Adds Mouse Events to a List
	 */
	public void keyboardTick(final VirtualKeyEvent event) {
		if (event.key != -1) keyEventsPerTick.add(event);
	}

	/**
	 * Adds Mouse Events to a List
	 */
	public void mouseTick(final VirtualMouseEvent event) {
		if (event.posX != -1 || event.eventButton != -1) mouseEventsPerTick.add(event);
	}

	public void saveTo(final File file, int tick) throws Exception {
		List<String> lines = Files.readAllLines(file.toPath());
		lines = lines.subList(0, (tick*2)-1);
		final FileWriter f = new FileWriter(file);
		for (String line : lines)
			f.write(line + "\n");
		while (!linesToPrint.isEmpty())
			f.write(linesToPrint.poll());
		f.close();
	}

	
	public void saveTo(final File file) throws Exception {
		file.createNewFile();
		final FileWriter f = new FileWriter(file);
		while (!linesToPrint.isEmpty())
			f.write(linesToPrint.poll());
		f.close();
	}

	public static void saveTAS() {
		File outFile = TASmod.rerecord;
		if (outFile == null) {
			GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
			final String out = JOptionPane.showInputDialog("Enter a name for the TAS", "");
			if (out == null) return;
			outFile = new File(Start.tasDir, out + ".tas");
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
	}

}
