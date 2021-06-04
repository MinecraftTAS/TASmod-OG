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
import net.minecraft.src.PlayerControllerCreative;
import net.minecraft.src.PlayerControllerSP;
import net.minecraft.src.WorldSettings;
import net.tasmod.TASmod;
import net.tasmod.Utils;
import net.tasmod.virtual.VirtualKeyboard;
import net.tasmod.virtual.VirtualKeyboard.VirtualKeyEvent;
import net.tasmod.virtual.VirtualMouse;
import net.tasmod.virtual.VirtualMouse.VirtualMouseEvent;

/**
 * Records a Speedrun, and saves it into a File.
 * @author Pancake
 */
public final class Replayer {
	
	private final long worldseed;
	private final int worldtype;
	private final boolean hardcore;
	private final boolean mapFeatures;
	private final double spawnX;
	private final double spawnY;
	private final double spawnZ;
	private final Minecraft mc;
	private final File file;
	private final BufferedReader reader;
	private final Queue<String> linesRead = new LinkedList<String>();
	
	private final Thread fileReader;
	
	/** Mouse for next tick */
	private String mouse;
	
	/**
	 * Loads a File and reads some ticks from it
	 * @throws IOException Cannot be thrown, unless something is terribly wrong.
	 */
	public Replayer(String fileName) throws IOException {
		this.mc = TASmod.mc;
		this.file = new File(this.mc.mcDataDir, fileName + ".tas");
		
		this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
		
		this.reader.readLine();
		this.reader.readLine();
		this.worldseed = Long.parseLong(this.reader.readLine().split(": ")[1].split(" ")[0]);
		this.hardcore = Boolean.parseBoolean(this.reader.readLine().split(": ")[1].split(" ")[0]);
		this.mapFeatures = Boolean.parseBoolean(this.reader.readLine().split(": ")[1].split(" ")[0]);
		this.worldtype = Integer.parseInt(this.reader.readLine().split(": ")[1].split(" ")[0]);
		this.reader.readLine();
		this.reader.readLine();
		
		final String spawnPos = this.reader.readLine();
		spawnX = Double.parseDouble(spawnPos.split(":")[0]);
		spawnY = Double.parseDouble(spawnPos.split(":")[1]);
		spawnZ = Double.parseDouble(spawnPos.split(":")[2]);
		this.fileReader = new Thread(new Runnable() {
			
			/**
			 * Thread that will read the file and store it in the Queue
			 */
			@Override
			public void run() {
				try {
					while (true) {
						// Only read up to 20 ticks
						if (linesRead.size() < 80) {
							final String line = reader.readLine();
							if (line == null || line.startsWith("#")) break;
							linesRead.add(line);
						} else {
							Thread.sleep(32);
						}
					}
					reader.close();
					System.out.println("Read Finished.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		fileReader.start();
	}
	
	/**
	 * Create a new World, and join it.
	 */
	public final void startReplay() {
		/* Delete World if it exists */
		final File worldFile = new File(this.mc.mcDataDir, "saves" + File.separator + "TAS-Playback");
		if (worldFile.exists()) Utils.deleteDirectory(worldFile);
		
		VirtualMouse.setCursorPosition(mc.displayWidth / 2, mc.displayHeight / 2);
		VirtualMouse.getDX();
		VirtualMouse.getDY();
		
		/* Join a new world */
		this.mc.playerController = this.worldtype == 0 ? new PlayerControllerSP(this.mc) : new PlayerControllerCreative(this.mc);
		this.mc.startWorld("TAS-Playback", "TAS-Playback", new WorldSettings(this.worldseed, this.worldtype, mapFeatures, hardcore));
		this.mc.displayGuiScreen(null);
		this.mc.thePlayer.rotationYaw = 0f;
		this.mc.thePlayer.rotationPitch = 0f;
	}
	
	/**
	 * Replay Read Ticks
	 */
	public final void tick() {
		tickKeyboad();
		tickMouse();
		Utils.checkDesync(mc, linesRead.poll());
		tickChunkloading();
	}
	
	private final void tickKeyboad() {
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
			TASmod.endPlayback();
		}
	}
	
	private final void tickMouse() {
		final String line = linesRead.poll();
		if (line != null) {
			if (line.isEmpty()) return;
			final Queue<VirtualMouseEvent> queue = VirtualMouse.mouseEventsForTick;
			queue.clear();
			Arrays.asList(line.split(":")).forEach(c -> {
				VirtualMouseEvent c2 = VirtualMouseEvent.fromString(c);
				queue.add(c2);
				VirtualMouse.dX = c2.dX;
				VirtualMouse.dY = c2.dY;
			});
			VirtualMouse.hack = true;
		} else {
			VirtualMouse.hack = false;
		}
	}
	
	private final void tickChunkloading() {
		final String line = linesRead.poll();
		if (line != null) {
			if (line.isEmpty()) return;
			Arrays.asList(line.split(":")).forEach(c -> {
				mc.theWorld.getIChunkProvider().loadChunk(Integer.parseInt(c.split(",")[0]), Integer.parseInt(c.split(",")[1]));
			});
			VirtualMouse.hack = true;
		} else {
			VirtualMouse.hack = false;
		}
	}
	
	/* Getters */
	
	public double getSpawnX() {
		return spawnX;
	}
	
	public double getSpawnY() {
		return spawnY;
	}
	
	public double getSpawnZ() {
		return spawnZ;
	}
	
}
