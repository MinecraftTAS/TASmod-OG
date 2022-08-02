package net.tasmod.renderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.jogamp.openal.ALCdevice;
import com.jogamp.openal.ALExt;

import net.minecraft.src.Timer;
import net.tasmod.TASmod;
import net.tasmod.Utils;
import net.tasmod.replayer.Replayer;
import net.tasmod.tools.TickrateChanger;

public final class Renderer extends Replayer {

	public static ALCdevice dev;
	public static ALExt ext;
	
	private OutputStream stream;
	private int framesPerTick;
	private ByteBuffer b;
	private byte[] ba;
	private ByteBuffer aa = ByteBuffer.allocate(2 * 2205 * 4);
	private Timer timer;
	
	public String path;
	public String resolution = "1920x1080";
	public int framerate = 60;
	public int crf = 18;
	public String codec = "libx264";
	public FileOutputStream out;
	
	public Renderer(File name) throws Exception {
		super(name);
	}
	
	/**
	 * Starts ffmpeg after the replay
	 */
	@Override
	public void startReplay() {
		super.startReplay();
		
		String ffmpeg = '"' + path + '"' + " -y -f rawvideo -c:v rawvideo -s 1920x1080 -pix_fmt rgb24 -r " + framerate + " -i - -vf vflip -pix_fmt yuv420p -c:v " + codec + " -s " + resolution + ("libx264".equals(codec) ? " -preset veryslow -tune film -profile high -rc-lookahead 120" : " -tier high -preset 4 -la_depth 120 -sc_detection true -hielevel 4level") + " -qp " + crf + " -crf " + crf + " \"" + file.getName() + ".mp4.video\"";
		System.out.println(ffmpeg);
		ProcessBuilder pb = new ProcessBuilder(ffmpeg);
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectErrorStream(true);
		pb.redirectError(Redirect.INHERIT);
		this.framesPerTick = 0;
		try {
			Process p = pb.start();
			this.stream = p.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			out = new FileOutputStream(new File(file.getName() + ".snd"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			this.timer = Utils.obtainTimerInstance();
			this.timer.renderPartialTicks = 0f;
			TickrateChanger.updateTickrate(500.0f);
			TickrateChanger.shouldOverrideRenderTime = true;
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
				if (this.framesPerTick == (framerate / 20)) {
					this.timer.elapsedTicks = 1;
					this.framesPerTick = 0;
				} else {
					this.timer.elapsedTicks = 0;
				}
				this.timer.renderPartialTicks = this.framesPerTick * (1 / (framerate / 20.0f));
				TickrateChanger.overrideRenderTime += ((1.0f / framerate) * 1000);
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
	 * Stops ffmpeg if tas is done and renders audio
	 */
	@Override
	public void tick() {
		super.tick();
		
		if (out != null && dev != null) {
			try {
        		ext.alcRenderSamplesSOFT(dev, aa, 2205);
				out.write(aa.array());
				aa.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (TASmod.playback == null) { // playback ended
			try {
				stream.flush();
				stream.close();
				out.flush();
				out.close();
				out = null;
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
