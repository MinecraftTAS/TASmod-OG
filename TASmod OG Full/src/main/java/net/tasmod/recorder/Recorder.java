package net.tasmod.recorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.tasmod.TASmod;
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
	private final File file;
	private final FileOutputStream writer;
	private final Queue<String> linesToPrint = new LinkedList<String>();
	
	private int currentTick;
	private final Thread fileWriter;
	
	/**
	 * Prepares the Recording File
	 * @param name The TAS name
	 * @throws IOException Cannot be thrown, unless something is terribly wrong.
	 */
	public Recorder(File name) throws IOException {
		this.mc = TASmod.mc;
		this.file = name;
		
		/** Create a new File for the Recorder */
		if (!this.file.exists()) this.file.createNewFile();
		this.writer = new FileOutputStream(this.file, false);
		fileWriter = new Thread(new Runnable() {
			
			/**
			 * Thread that will take stuff from a queue and write to the File
			 */
			@Override
			public void run() {
				try {
					/* Print the Header of the TAS into the File */
					while (!Thread.currentThread().isInterrupted()) {
						if (!linesToPrint.isEmpty()) writer.write(linesToPrint.poll().getBytes(StandardCharsets.UTF_8));
						else
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								break;
							}
					}
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		fileWriter.start();
	}
	
	/**
	 * Start the recording after preparing the player mouse
	 */
	public final void startRecording() {
		SimpleRandomMod.updateSeed(0L);
		WeightedRandomMod.intCalls = 0;
		
		VirtualMouse.setCursorPosition(mc.displayWidth / 2, mc.displayHeight / 2);
		VirtualMouse.getDX();
		VirtualMouse.getDY();
		
		VirtualKeyboard.listen = true;
		VirtualMouse.listen = true;
	}
	
	private volatile List<VirtualKeyEvent> keyEventsPerTick = new ArrayList<>();
	private volatile List<VirtualMouseEvent> mouseEventsPerTick = new ArrayList<>();
	
	/**
	 * Write all Keybindings into the File
	 */
	public final void tick() {
		while (true) {
			String events = "";
			if (keyEventsPerTick.size() == 0) {
				linesToPrint.add("\n");
				break;
			}
			for (final VirtualKeyEvent virtualKeyEvent : keyEventsPerTick) {
				events += virtualKeyEvent + ":";
			}
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
	public final void endRecording() {
		fileWriter.interrupt();
		VirtualKeyboard.listen = false;
		VirtualMouse.listen = false;
	}
	
	/**
	 * Adds Mouse Events to a List 
	 */
	public final void keyboardTick(final VirtualKeyEvent event) {
		if (event.key != -1) keyEventsPerTick.add(event);
	}

	/**
	 * Adds Mouse Events to a List 
	 */
	public final void mouseTick(final VirtualMouseEvent event) {
		if (event.posX != -1 || event.eventButton != -1) mouseEventsPerTick.add(event);
	}
	
}
