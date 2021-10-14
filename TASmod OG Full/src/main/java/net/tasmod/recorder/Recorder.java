package net.tasmod.recorder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.tasmod.TASmod;
import net.tasmod.main.Start;
import net.tasmod.random.SimpleRandomMod;
import net.tasmod.random.WeightedRandomMod;
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

	/**
	 * Prepares the Recording File
	 * @param name The TAS name
	 * @throws IOException Cannot be thrown, unless something is terribly wrong.
	 */
	public Recorder() {
		this.mc = TASmod.mc;
		SimpleRandomMod.updateSeed(0L);
		WeightedRandomMod.intCalls = 0;

		VirtualMouse.setCursorPosition(mc.displayWidth / 2, mc.displayHeight / 2);
		VirtualMouse.getDX();
		VirtualMouse.getDY();

		linesToPrint.add(Start.resolution + "\n");

		VirtualKeyboard.listen = true;
		VirtualMouse.listen = true;
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

	public void saveTo(final File file) throws Exception {
		file.createNewFile();
		final FileWriter f = new FileWriter(file);
		while (!linesToPrint.isEmpty())
			f.write(linesToPrint.poll());
		f.close();
	}

}
