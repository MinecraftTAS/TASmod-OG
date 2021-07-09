package net.tasmod.recorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;
import net.minecraft.src.PlayerControllerCreative;
import net.minecraft.src.PlayerControllerSP;
import net.minecraft.src.WorldSettings;
import net.tasmod.TASmod;
import net.tasmod.Utils;
import net.tasmod.asm.WeightedRandomnessVisitor;
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
	
	private final long worldseed;
	private final int worldtype;
	private final boolean hardcore;
	private final boolean mapFeatures;
	private final String folderName;
	private final String worldName;
	private final Minecraft mc;
	private final File file;
	private final FileOutputStream writer;
	private final String author;
	private int savestates;
	private int loadstates;
	private final Queue<String> linesToPrint = new LinkedList<String>();
	
	private int currentTick;
	private final Thread fileWriter;
	
	/**
	 * Creates a new File and prints the Header into it
	 * @param worldseed World Seed of the TAS.
	 * @param worldtype Gamemode of the TAS
	 * @param worldName 
	 * @param folderName 
	 * @throws IOException Cannot be thrown, unless something is terribly wrong.
	 */
	public Recorder(long worldseed, int worldtype, boolean hardcore, boolean mapFeatures, String folderName, String worldName) throws IOException {
		this.worldseed = worldseed;
		this.mc = TASmod.mc;
		this.worldtype = worldtype;
		this.hardcore = hardcore;
		this.folderName = folderName;
		this.worldName = worldName;
		this.mapFeatures = mapFeatures;
		this.author = TASmod.mc.session.username;
		this.file = new File(this.mc.mcDataDir, folderName + ".tas");
		this.mc.gameSettings.saveOptions();
		
		SimpleRandomMod.updateSeed(0L);
		WeightedRandomMod.intCalls = 0;
		
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
					writer.write(("###############################################\n").getBytes(StandardCharsets.UTF_8));
					writer.write(("#                 THIS IS A TAS               #\n").getBytes(StandardCharsets.UTF_8));
					writer.write(("#    Seed: " + String.format("%020d", Recorder.this.worldseed) + "               #\n").getBytes(StandardCharsets.UTF_8));
					writer.write(("#    Hardcore: " + Recorder.this.hardcore + "                          #\n").getBytes(StandardCharsets.UTF_8));
					writer.write(("#    Map Features: " + Recorder.this.mapFeatures + "                       #\n").getBytes(StandardCharsets.UTF_8));
					writer.write(("#    Gametype: " + Recorder.this.worldtype +  "                              #\n").getBytes(StandardCharsets.UTF_8));
					writer.write(("#                 PLAYBACK FILE               #\n").getBytes(StandardCharsets.UTF_8));
					writer.write(("###############################################\n").getBytes(StandardCharsets.UTF_8));
					writer.write((Files.readAllLines(new File(mc.mcDataDir, "options.txt").toPath()).stream().collect(Collectors.joining("/r/n")) + "\n").getBytes(StandardCharsets.UTF_8));
					writer.write((Display.isFullscreen() + "\n").getBytes(StandardCharsets.UTF_8));
					writer.write(("Author: " + author + "\n").getBytes(StandardCharsets.UTF_8));
					writer.write(("Date: " + Calendar.getInstance().getTimeInMillis() + "\n").getBytes(StandardCharsets.UTF_8));
					
					while (!Thread.currentThread().isInterrupted()) {
						if (!linesToPrint.isEmpty()) writer.write(linesToPrint.poll().getBytes(StandardCharsets.UTF_8));
						else
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								break;
							}
					}
					writer.write("###############################################\n".getBytes(StandardCharsets.UTF_8));
					writer.write((Calendar.getInstance().getTimeInMillis() + "\n").getBytes(StandardCharsets.UTF_8));
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		fileWriter.start();
	}
	
	/**
	 * Create a new World, and join it.
	 */
	public final void startRecording() {
		/* Delete World if it exists */
		final File worldFile = new File(this.mc.mcDataDir, "saves" + File.separator + "TAS-Playback");
		if (worldFile.exists()) Utils.deleteDirectory(worldFile);
		
		VirtualMouse.setCursorPosition(mc.displayWidth / 2, mc.displayHeight / 2);
		VirtualMouse.getDX();
		VirtualMouse.getDY();
		
		VirtualKeyboard.listen = true;
		VirtualMouse.listen = true;
		
		/* Join a new world */
		this.mc.playerController = this.worldtype == 0 ? new PlayerControllerSP(this.mc) : new PlayerControllerCreative(this.mc);
		this.mc.startWorld(folderName, worldName, new WorldSettings(this.worldseed, this.worldtype, mapFeatures, hardcore));
		this.mc.displayGuiScreen(null);
		this.mc.thePlayer.rotationYaw = 0f;
		this.mc.thePlayer.rotationPitch = 0f;
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
		linesToPrint.add(String.format(Locale.US, "%.5f", mc.thePlayer.posX) + ":" + String.format(Locale.US, "%.5f", mc.thePlayer.posY) + ":" + String.format(Locale.US, "%.5f", mc.thePlayer.posZ) + ":" + String.format(Locale.US, "%.5f", mc.thePlayer.motionX) + ":" + String.format(Locale.US, "%.5f", mc.thePlayer.motionY) + ":" + String.format(Locale.US, "%.5f", mc.thePlayer.motionZ) + ":" + String.format(Locale.US, "%.5f", mc.thePlayer.rotationPitch % 360) + ":" + String.format(Locale.US, "%.5f", mc.thePlayer.rotationYaw % 360) + "\n");
		this.currentTick++;
		SimpleRandomMod.updateSeed(currentTick);
	}
	
	/**
	 * End the Recording by writing '#' to the File
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
