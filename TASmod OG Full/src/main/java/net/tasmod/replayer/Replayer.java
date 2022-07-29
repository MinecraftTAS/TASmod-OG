package net.tasmod.replayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.tasmod.TASmod;
import net.tasmod.main.EmulatorFrame;
import net.tasmod.main.Start;
import net.tasmod.random.SimpleRandomMod;
import net.tasmod.random.WeightedRandomMod;
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
public final class Replayer {

	private static final String ffmpeg = "\"C:\\Program Files (x86)\\VMware\\VMware Workstation\\bin\\ffmpeg.exe\" -y -hwaccel vulkan -hwaccel_output_format cuda -f rawvideo -c:v rawvideo -s %WIDTH%x%HEIGHT% -pix_fmt rgb24 -r 20 -i - -vf vflip -b:v 32M -pix_fmt yuv420p -c:v h264_nvenc %OUTPUT%";
	
	private Minecraft mc;
	private final File file;
	private final BufferedReader reader;
	private final Queue<String> linesRead = new LinkedList<>();
	private final Thread fileReader;
	public int currentTick;
	private OutputStream stream;
	
	/**
	 * Loads a File and reads some ticks from it
	 * @throws IOException Cannot be thrown, unless something is terribly wrong.
	 */
	public Replayer(final File name) throws Exception {
		this.file = name;
		this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
		Start.resolution = reader.readLine();
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
		SimpleRandomMod.updateSeed(0L);
		WeightedRandomMod.intCalls = 0;

		VirtualMouse.setCursorPosition(mc.displayWidth / 2, mc.displayHeight / 2);
		VirtualMouse.getDX();
		VirtualMouse.getDY();
		
		TASmod.mc.gameSettings.limitFramerate = 20;
		
		ProcessBuilder pb = new ProcessBuilder(ffmpeg.replace("%WIDTH%", TASmod.mc.displayWidth + "").replace("%HEIGHT%", TASmod.mc.displayHeight + "").replace("%OUTPUT%", "\"" + this.file + ".mp4\"").split(" "));
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectErrorStream(true);
		pb.redirectError(Redirect.INHERIT);
		try {
			Process p = pb.start();
			stream = p.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			TickrateChanger.updateTickrate(50.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ByteBuffer b;
	byte[] ba;
	
	/**
	 * Replay Read Ticks
	 */
	public void tick() {	
		tickKeyboad();
		tickMouse();
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

	public void render() {
		try {
			if (mc != null) {
				TASmod.mc.timer.elapsedTicks = 1;
				if (b == null) {
					b = ByteBuffer.allocateDirect(mc.displayWidth*mc.displayHeight*3);
					ba = new byte[mc.displayWidth*mc.displayHeight*3];
				} else {
					b.clear();
					GL11.glReadPixels(0, 0, mc.displayWidth, mc.displayHeight, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, b);
					b.get(ba);
					try {
						stream.write(ba);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			try {
				stream.flush();
				stream.close();
				TickrateChanger.toggleTickadvance();
			} catch (Exception e) {
				e.printStackTrace();
			}
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

}
