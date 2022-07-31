package net.tasmod.replayer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import net.tasmod.TASmod;
import net.tasmod.tools.TickrateChanger;

public final class Renderer extends Replayer {

	private static final String ffmpeg = "\"C:\\Program Files (x86)\\VMware\\VMware Workstation\\bin\\ffmpeg.exe\" -y -hwaccel vulkan -hwaccel_output_format cuda -f rawvideo -c:v rawvideo -s %WIDTH%x%HEIGHT% -pix_fmt rgb24 -r 120 -i - -vf vflip -b:v 32M -pix_fmt yuv420p -c:v h264_nvenc %OUTPUT%";
	
	private OutputStream stream;
	private int framesPerTick;
	private ByteBuffer b;
	private byte[] ba;
	
	public Renderer(File name) throws Exception {
		super(name);
	}
	
	/**
	 * Starts ffmpeg after the replay
	 */
	@Override
	public void startReplay() {
		super.startReplay();
		
		ProcessBuilder pb = new ProcessBuilder(ffmpeg.replace("%WIDTH%", TASmod.mc.displayWidth + "").replace("%HEIGHT%", TASmod.mc.displayHeight + "").replace("%OUTPUT%", "\"" + this.file + ".mp4\"").split(" "));
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectErrorStream(true);
		pb.redirectError(Redirect.INHERIT);
		this.framesPerTick = 0;
		this.mc.timer.renderPartialTicks = 0f;
		try {
			Process p = pb.start();
			this.stream = p.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			TickrateChanger.updateTickrate(500.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renders a frame to ffmpeg every frame
	 */
	@Override
	public void render() {
		super.render();
		
		try {
			if (this.mc != null) {
				this.framesPerTick++;
				if (this.framesPerTick == 6) {
					TASmod.mc.timer.elapsedTicks = 1;
					this.framesPerTick = 0;
				} else {
					TASmod.mc.timer.elapsedTicks = 0;
				}
				TASmod.mc.timer.renderPartialTicks = this.framesPerTick * 0.166666667f;
				if (b == null) {
					this.b = ByteBuffer.allocateDirect(this.mc.displayWidth*this.mc.displayHeight*3);
					this.ba = new byte[this.mc.displayWidth*this.mc.displayHeight*3];
				} else {
					this.b.clear();
					GL11.glReadPixels(0, 0, this.mc.displayWidth, this.mc.displayHeight, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, this.b);
					this.b.get(this.ba);
					try {
						this.stream.write(this.ba);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops ffmpeg if tas is done
	 */
	@Override
	public void tick() {
		super.tick();
		
		if (TASmod.playback == null) { // playback ended
			try {
				stream.flush();
				stream.close();
				TickrateChanger.toggleTickadvance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean isVisible() {
		return false; // don't render ui and disable hotkeys
	}
	
}
