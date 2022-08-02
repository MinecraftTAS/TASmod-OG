package net.tasmod.renderer;

import java.io.File;
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
	private OutputStream stream2;
	private int framesPerTick;
	private ByteBuffer b;
	private byte[] ba;
	private ByteBuffer aa = ByteBuffer.allocate(2 * 2205 * 4);
	private Timer timer;
	private Process videoProcess;
	private Process audioProcess;
	
	public String path;
	public String resolution = "1920x1080";
	public int framerate = 60;
	public int crf = 18;
	public String codec = "libx264";
	public boolean startRender;
	public String acodec;
	public int abitrate;
	
	public Renderer(File name) throws Exception {
		super(name);
	}
	
	/**
	 * Starts ffmpeg after the replay
	 */
	@Override
	public void startReplay() {
		super.startReplay();
		
		// start video ffmpeg
		String ffmpeg = '"' + path + '"' + " -y -f rawvideo -c:v rawvideo -s 1920x1080 -pix_fmt rgb24 -r " + framerate + " -i - -vf vflip -pix_fmt yuv420p -c:v " + codec + " -s " + resolution + ("libx264".equals(codec) ? " -preset veryslow -tune film -profile high -rc-lookahead 120" : " -tier high -preset 4 -la_depth 120 -sc_detection true -hielevel 4level") + " -qp " + crf + " -an -sn -crf " + crf + " \"" + file.getName() + ".mp4\"";
		System.out.println(ffmpeg);
		ProcessBuilder pb = new ProcessBuilder(ffmpeg);
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectErrorStream(true);
		pb.redirectError(Redirect.INHERIT);
		this.framesPerTick = 0;
		try {
			videoProcess = pb.start();
			this.stream = videoProcess.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// start audio ffmpeg
		ffmpeg = '"' + path + '"' + " -y -f f32le -ar 44100 -ac 2 -i - -c:a " + acodec + " -b:a " + abitrate + "K -vn -sn \"" + file.getName() + ((acodec == "aac") ? ".aac" : ((acodec == "libvorbis") ? ".ogg" : ".opus")) + "\"";
		System.out.println(ffmpeg);
		pb = new ProcessBuilder(ffmpeg);
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectErrorStream(true);
		pb.redirectError(Redirect.INHERIT);
		try {
			audioProcess = pb.start();
			this.stream2 = audioProcess.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.timer = Utils.obtainTimerInstance();
			this.timer.renderPartialTicks = 0f;
			this.timer.elapsedTicks = 1;
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
				this.startRender = true;
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
				}
				this.b.clear();
				GL11.glReadPixels(0, 0, this.mc.displayWidth, this.mc.displayHeight, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, this.b);
				this.b.get(this.ba);
				try {
					this.stream.write(this.ba);
				} catch (Exception e1) {
					e1.printStackTrace();
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
		
		try {
			if (this.stream2 != null && this.mc != null && this.startRender) {
				ext.alcRenderSamplesSOFT(dev, this.aa, 2205);
				this.stream2.write(this.aa.array());
				this.aa.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (TASmod.playback == null) { // playback ended
			try {
				this.stream.flush();
				this.stream.close();
				this.stream2.flush();
				this.stream2.close();
				TickrateChanger.toggleTickadvance();
				if (videoProcess.isAlive())
					videoProcess.waitFor();
				if (audioProcess.isAlive())
					audioProcess.waitFor();
				String ffmpeg = '"' + path + '"' + " -y -i \"" + file.getName() + ".mp4\" -i \"" + file.getName() + ((acodec == "aac") ? ".aac" : ((acodec == "libvorbis") ? ".ogg" : ".opus")) + "\" -c copy " + file.getName() + ".mkv\"";
				ProcessBuilder pb = new ProcessBuilder(ffmpeg);
				pb.redirectOutput(Redirect.INHERIT);
				pb.redirectErrorStream(true);
				pb.redirectError(Redirect.INHERIT);
				try {
					Process p = pb.start();
					p.waitFor();
					new File(file.getName() + ".mp4").delete();
					new File(file.getName() + ".opus").delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
