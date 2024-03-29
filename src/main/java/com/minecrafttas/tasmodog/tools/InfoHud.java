package com.minecrafttas.tasmodog.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.minecrafttas.tasmodog.TASmod;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.KeyBinding;

/**
 * Customizable info hud/overlay
 * @author Pancake
 */
public class InfoHud extends GuiScreen {

	/**
	 * Renderable Widget
	 */
	public abstract class Widget {
		
		private String displayName;
		private String shortName;
		private int x, y;
		private boolean visible;
		private boolean shouldRenderBackground;

		/**
		 * Initialize widget
		 * @param displayName Display name
		 * @param shortName Config name
		 */
		public Widget(String displayName, String shortName) {
			this.displayName = displayName;
			this.shortName = shortName;
			this.visible = Boolean.parseBoolean(configuration.getProperty(shortName + "_visible", "false"));
			this.x = Integer.parseInt(configuration.getProperty(shortName + "_x", "0"));
			this.y = Integer.parseInt(configuration.getProperty(shortName + "_y", "0"));
			this.shouldRenderBackground = Boolean.parseBoolean(configuration.getProperty(shortName + "_rect", "true"));
		}
		
		public abstract String text();
		
	}
	
	private Properties configuration;
//	private TASmod tasmod;
	private List<Widget> widgets;
	private Widget currentlyDragging;
	private int offsetX, offsetY; // mouse offset between cursor and corner of item below
	
	/**
	 * Initialize Info Hud
	 */
	public InfoHud() {
		this.configuration = new Properties();
		this.widgets = new ArrayList<>();
	}

	/**
	 * Post Initialize Info Hud with Minecraft instance
	 * @param mc Minecraft instance
	 * @param tasmod TASmod instance
	 */
	public void init(Minecraft mc, TASmod tasmod) {
		this.mc = mc;
//		this.tasmod = tasmod;
		
		// try to load configuration
		try {
			this.loadConfig();
		} catch (Exception e) {
			System.err.println("Unable to load info hud configuration");
			e.printStackTrace();
		}
		
		// initialize widgets
		this.initWidgets();
	}
	
	/**
	 * Identifies the widget below the cursor
	 * @param mouseX Mouse X
	 * @param mouseY Mouse Y
	 * @return Widget below cursor
	 */
	public Widget identifyCursor(int mouseX, int mouseY) {
		for (Widget widget : this.widgets) {
			if (mouseX >= widget.x && mouseX <= (widget.x + Math.max(this.mc.fontRenderer.getStringWidth(widget.displayName), this.mc.fontRenderer.getStringWidth(widget.text()))) && mouseY >= widget.y && mouseY <= (widget.y + 15)) {
				this.offsetX = mouseX - widget.x;
				this.offsetY = mouseY - widget.y;
				return widget;
			}
		}
		this.offsetX = this.offsetY = -1;
		return null;
	}
	

	/**
	 * Mouse click event
	 */
	@Override 
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		if (this.currentlyDragging != null)
			return;
		
		Widget widget = this.identifyCursor(mouseX, mouseY);
		if (widget == null)
			return;
		
		if (mouseButton == 1) {
			widget.shouldRenderBackground = !widget.shouldRenderBackground;
			this.configuration.setProperty(widget.shortName + "_rect", widget.shouldRenderBackground + "");
		} else if (mouseButton == 2) {
			widget.visible = !widget.visible;
			this.configuration.setProperty(widget.shortName + "_visible", widget.visible + "");
		} else {
			this.currentlyDragging = widget;
		}
		
		try {
			this.saveConfig();
		} catch (Exception e) {
			System.err.println("Unable to save info hud configuration");
			e.printStackTrace();
		}
	}

	/**
	 * Mouse drag event
	 */
	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int k) {
		super.mouseMovedOrUp(mouseX, mouseY, k);
		if (this.currentlyDragging == null)
			return;
		
		if (k == -1) {
			this.configuration.setProperty(this.currentlyDragging.shortName + "_x", (this.currentlyDragging.x = mouseX - this.offsetX) + "");
			this.configuration.setProperty(this.currentlyDragging.shortName + "_y", (this.currentlyDragging.y = mouseY - this.offsetY) + "");
		} else if (k == 0) {
			this.currentlyDragging = null;
			try {
				this.saveConfig();
			} catch (Exception e) {
				System.err.println("Unable to save info hud configuration");
				e.printStackTrace();
			}
		}
	}



	/**
	 * Render info hud
	 */
	public void drawHud() {
		// don't draw if not requested by playback
//		TODO: reimplement
//		if (this.tasmod.getInputContainer() instanceof Playback && !((Playback) this.tasmod.getInputContainer()).isVisible())
//			return;

		// don't draw over f3 screen
		if (Minecraft.isDebugInfoEnabled()) 
			return;
		
		// render widgets
		for (Widget widget : this.widgets)
			if (this.mc.currentScreen instanceof InfoHud)
				this.drawText(widget.displayName, widget.x, widget.y, widget.shouldRenderBackground, widget.visible);
			else if (widget.visible)
				this.drawText(widget.text(), widget.x, widget.y, widget.shouldRenderBackground, true);
	}

	/**
	 * Load configuration
	 * @throws Exception Filesystem exception
	 */
	private void loadConfig() throws Exception {
		final File configFile = new File("infogui.cfg");
		if (!configFile.exists())
			configFile.createNewFile();
		
		this.configuration.load(new FileReader(configFile));
	}
	
	/**
	 * Save configuration
	 * @throws Exception Filesystem exception
	 */
	private void saveConfig() throws Exception {
		File configFile = new File("infogui.cfg");
		if (!configFile.exists())
			configFile.createNewFile();
		
		this.configuration.store(new FileOutputStream(configFile, false), "Info Hud Configuration File");
	}
	
	/**
	 * Draw text with rect
	 * @param text Text
	 * @param x X-Position
	 * @param y Y-Position
	 * @param rect Rect visibility
	 * @param fullVisibility Full Visibility
	 */
	private void drawText(String text, int x, int y, boolean rect, boolean fullVisibility) {
		if (rect)
			this.drawRect(x, y, x + this.mc.fontRenderer.getStringWidth(text) + 4, y + 14, fullVisibility ? 0x80000000 : 0x40000000);

		GL11.glEnable(GL11.GL_BLEND);
		this.mc.fontRenderer.drawStringWithShadow(text, x + 2, y + 3, fullVisibility ? 0xFFFFFF : 0x40FFFFFF);
	}

	/**
	 * Initialize widgets
	 */
	private void initWidgets() {
		// Gamespeed widget
		this.widgets.add(new Widget("Gamespeed", "gamespeed") {

			@Override
			public String text() {
				return String.format("Gamespeed: %.2f", mc.timer.timerSpeed);
			}

		});

		// Position widget
		this.widgets.add(new Widget("Position", "xyz") {

			@Override
			public String text() {
				return String.format("XYZ: %.2f %.2f %.2f", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
			}

		});

		// Precise position widget
		this.widgets.add(new Widget("Exact Position", "precise_xyz") {

			@Override
			public String text() {
				return String.format("Exact XYZ: %f %f %f", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
			}

		});

		// Chunk widget
		this.widgets.add(new Widget("Chunk Position", "chunk_xz") {

			@Override
			public String text() {
				return String.format("Chunk: %d %d", mc.thePlayer.chunkCoordX, mc.thePlayer.chunkCoordZ);
			}

		});

		// Worldseed widget
		this.widgets.add(new Widget("Worldseed", "worldseed") {

			@Override
			public String text() {
				return String.format("Worldseed: %d", mc.theWorld.getWorldSeed());
			}

		});

		// Velocity widget
		this.widgets.add(new Widget("Velocity", "velocity") {

			@Override
			public String text() {
				return String.format("Velocity: %.2f %.2f %.2f", mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
			}

		});

		// Sprinting hint widget
		this.widgets.add(new Widget("Sprinting Hint", "sprinting") {

			@Override
			public String text() {
				return "Sprinting Hint: " + (!mc.thePlayer.isSprinting() ? mc.thePlayer.sprintToggleTimer > 5 ? "Release" : "Press" : "Hold");
			}

		});

		// Rotation widget
		this.widgets.add(new Widget("Rotation", "facing") {

			@Override
			public String text() {
				return String.format("Rotation: %.2f %.2f", FakeCamera.yaw, FakeCamera.pitch);
			}

		});

		// Keystrokes widget
		this.widgets.add(new Widget("Keystrokes", "keystrokes") {

			@Override
			public String text() {
				String text = "";
				String key;

				for (KeyBinding binds : mc.gameSettings.keyBindings)
					if (binds.pressed && (key = ((binds.keyCode >= 0 && binds.keyCode < 256) ? Keyboard.getKeyName(binds.keyCode) : null)) != null) 
						text += key + " ";

				if (mc.gameSettings.keyBindAttack.pressed)
					text += "LC ";

				if (mc.gameSettings.keyBindUseItem.pressed)
					text += "RC ";
				
				return text;
			}

		});
	}
	
}
