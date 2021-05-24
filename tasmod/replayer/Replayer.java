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
import net.tasmod.recorder.Recorder;
import net.tasmod.rng.FakeRandom;
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
		
		this.fileReader = new Thread(new Runnable() {
			
			/**
			 * Thread that will read the file and store it in the Queue
			 */
			@Override
			public void run() {
				try {
					while (true) {
						// Only read up to 20 ticks
						if (linesRead.size() < 60) {
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
		//#DEV: Add Desync Tool
		// Update
    	double desyncPosX = mc.thePlayer.posX - posX;
    	double desyncPosY = mc.thePlayer.posY - posY;
    	double desyncPosZ = mc.thePlayer.posZ - posZ;
    	double desyncMotionX = mc.thePlayer.motionX - motionX;
    	double desyncMotionY = mc.thePlayer.motionY - motionY;
    	double desyncMotionZ = mc.thePlayer.motionZ - motionZ;
    	float desyncYaw = (mc.thePlayer.rotationYaw % 360) - yaw;
    	float desyncPitch = (mc.thePlayer.rotationPitch % 360) - pitch;
    	desync = "";
    	desync_2 = "";
    	desync_3 = "";
    	if (!Utils.isZero(desyncPosX, 0.000009D)) desync += "X: " + String.format("%.5f", desyncPosX) + " ";
    	if (!Utils.isZero(desyncPosY, 0.000009D)) desync += "Y: " + String.format("%.5f", desyncPosY) + " ";
    	if (!Utils.isZero(desyncPosZ, 0.000009D)) desync += "Z: " + String.format("%.5f", desyncPosZ) + " ";
    	if (!Utils.isZero(desyncMotionX, 0.000009D)) desync_2 += "mX: " + String.format("%.5f", desyncMotionX) + " ";
    	if (!Utils.isZero(desyncMotionY, 0.000009D)) desync_2 += "mY: " + String.format("%.5f", desyncMotionY) + " ";
    	if (!Utils.isZero(desyncMotionZ, 0.000009D)) desync_2 += "mZ: " + String.format("%.5f", desyncMotionZ) + " ";
    	if (!Utils.isZeroFloat(desyncYaw, 0.000009F)) desync_3 += "Yaw: " + String.format("%.5f", desyncYaw) + " ";
    	if (!Utils.isZeroFloat(desyncPitch, 0.000009F)) desync_3 += "Pitch: " + String.format("%.5f", desyncPitch) + " ";
    	// Reset
		final String line = linesRead.poll();
		if (line == null) return;
		final String[] strings = line.split(":");
		if (!strings[0].isEmpty()) posX = Double.parseDouble(strings[0]);
		else posX = 0.0D;
		if (!strings[1].isEmpty()) posY = Double.parseDouble(strings[1]);
		else posY = 0.0D;
		if (!strings[2].isEmpty()) posZ = Double.parseDouble(strings[2]);
		else posZ = 0.0D;
		if (!strings[3].isEmpty()) motionX = Double.parseDouble(strings[3]);
		else motionX = 0.0D;
		if (!strings[4].isEmpty()) motionY = Double.parseDouble(strings[4]);
		else motionY = 0.0D;
		if (!strings[5].isEmpty()) motionZ = Double.parseDouble(strings[5]);
		else motionZ = 0.0D;
		if (!strings[6].isEmpty()) pitch = Float.parseFloat(strings[6]);
		else pitch = 0.0F;
		if (!strings[7].isEmpty()) yaw = Float.parseFloat(strings[7]);
		else yaw = 0.0F;
		//#ENDDEV
	}
	
	//#DEV: Add Desync Tool
	public double posX;
	public double posY;
	public double posZ;
	public double motionX;
	public double motionY;
	public double motionZ;
	public float yaw;
	public float pitch;
	public String desync;
	public String desync_2;
	public String desync_3;
	//#ENDDEV
	
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
	
}
