package net.tasmod.infogui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.KeyBinding;
import net.tasmod.TASmod;
import net.tasmod.random.SimpleRandomMod;
import net.tasmod.tools.TickrateChanger;

/**
 * The info hud is a hud that is always being rendered ontop of the screen, it can show some stuff such as coordinates, etc.,
 * any everything can be customized
 * @author Pancake
 */
public class InfoHud extends GuiScreen {
	
	public static class InfoLabel {
		public String displayName;
		public int x;
		public int y;
		public boolean visible;
		public boolean renderRect;
		public String renderText;
		private Callable<String> text;
		
		public InfoLabel(String displayName, int x, int y, boolean visible, boolean renderRect, Callable<String> text) {
			this.displayName = displayName;
			this.visible = visible;
			this.x = x;
			this.y = y;
			this.renderRect = renderRect;
			this.text = text;
		}
		
		public void tick() {
			try {
				renderText = text.call();
			} catch (Exception e) {
				e.printStackTrace();
				// Lots of NPEs
			}
		}
	}
	
	/** -1, or the current index in {@link InfoHud#lists} that is being dragged by the mouse */
	private int currentlyDraggedIndex = -1;
	private int xOffset; // drag offsets
	private int yOffset;
	
	public Properties configuration;
	public static List<InfoLabel> lists = new ArrayList<>();
	
	private void setDefaults(String string) {
		configuration.setProperty(string + "_x", "0");
		configuration.setProperty(string + "_y", "0");
		configuration.setProperty(string + "_visible", "false");
		configuration.setProperty(string + "_rect", "false");
		saveConfig();
	}
	
	/**
	 * Returns the object below the mouse
	 */
	public void identify(int mouseX, int mouseY) {
		int index = 0;
		for (InfoLabel label : lists) {
			int x=0;
			int y=0;
			try {
				x = Integer.parseInt(configuration.getProperty(label.displayName + "_x"));
				y = Integer.parseInt(configuration.getProperty(label.displayName + "_y"));
			} catch (NumberFormatException e) {
				configuration.setProperty(label.displayName + "_x", "0");
				configuration.setProperty(label.displayName + "_y", "0");
				saveConfig();
			}
			int w = x + TASmod.mc.fontRenderer.getStringWidth(label.renderText);
			int h = y + 15;
			
			if (mouseX >= x && mouseX <= w && mouseY >= y && mouseY <= h) {
				currentlyDraggedIndex = index;
				xOffset = mouseX - x;
				yOffset = mouseY - y;
				return;
			}
			index++;
		}
		currentlyDraggedIndex = -1;
		xOffset = -1;
		yOffset = -1;
	}
	
	@Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 1) {
			identify(mouseX, mouseY);
			if (currentlyDraggedIndex != -1) {
				String id = lists.get(currentlyDraggedIndex).displayName;
				lists.get(currentlyDraggedIndex).renderRect = !lists.get(currentlyDraggedIndex).renderRect;
				configuration.setProperty(id + "_rect", configuration.getProperty(id + "_rect").equalsIgnoreCase("true") ? "false" : "true");
				saveConfig();
				currentlyDraggedIndex = -1;
			}
			return;
		} else if (mouseButton == 2) {
			identify(mouseX, mouseY);
			if (currentlyDraggedIndex != -1) {
				String id = lists.get(currentlyDraggedIndex).displayName;
				lists.get(currentlyDraggedIndex).visible = !lists.get(currentlyDraggedIndex).visible;
				configuration.setProperty(id + "_visible", configuration.getProperty(id + "_visible").equalsIgnoreCase("true") ? "false" : "true");
				saveConfig();
				currentlyDraggedIndex = -1;
			}
			return;
		}
		identify(mouseX, mouseY);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override protected void mouseMovedOrUp(int mouseX, int mouseY, int k) {
		if (currentlyDraggedIndex != -1 && k == -1) {
			String dragging = lists.get(currentlyDraggedIndex).displayName;
			lists.get(currentlyDraggedIndex).x = mouseX - xOffset;
			lists.get(currentlyDraggedIndex).y = mouseY - yOffset;
			configuration.setProperty(dragging + "_x", lists.get(currentlyDraggedIndex).x + "");
			configuration.setProperty(dragging + "_y", lists.get(currentlyDraggedIndex).y + "");
			saveConfig();
		}
		if (k == 0) {
			currentlyDraggedIndex = -1;
		}
		super.mouseMovedOrUp(mouseX, mouseY, k);
	}
	
	/**
	 * Saves the Configuration
	 */
	private void saveConfig() {
		try {
			File tasmodDir = new File(TASmod.mc.mcDataDir, "tasmodog");
			tasmodDir.mkdir();
			File configFile = new File(tasmodDir, "infogui.cfg");
			if (!configFile.exists()) configFile.createNewFile();
			configuration.store(new FileOutputStream(configFile, false), "DO NOT EDIT MANUALLY");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates every tick
	 */
	public void tick() {
		if (checkInit()) return;
		for (InfoLabel label : lists) label.tick();
	}
	
	public boolean checkInit() {
		if (configuration != null) return false;
		/* Check whether already rendered before */
		try {
			configuration = new Properties();
			File tasmodDir = new File(TASmod.mc.mcDataDir, "tasmodog");
			tasmodDir.mkdir();
			File configFile = new File(tasmodDir, "infogui.cfg");
			if (!configFile.exists()) configFile.createNewFile();
			configuration.load(new FileReader(configFile));
			lists = new ArrayList<InfoLabel>();
			/* ====================== */
			if (configuration.getProperty("tickrate_x", "err").equals("err")) setDefaults("tickrate");
			lists.add(new InfoLabel("tickrate", Integer.parseInt(configuration.getProperty("tickrate_x")), Integer.parseInt(configuration.getProperty("tickrate_y")), Boolean.parseBoolean(configuration.getProperty("tickrate_visible")), Boolean.parseBoolean(configuration.getProperty("tickrate_rect")), () -> {
				return "Gamespeed: " + TickrateChanger.availableGamespeeds[TickrateChanger.selectedGamespeed];
			}));
			if (configuration.getProperty("xyz_x", "err").equals("err")) setDefaults("xyz");
			lists.add(new InfoLabel("xyz", Integer.parseInt(configuration.getProperty("xyz_x")), Integer.parseInt(configuration.getProperty("xyz_y")), Boolean.parseBoolean(configuration.getProperty("xyz_visible")), Boolean.parseBoolean(configuration.getProperty("xyz_rect")), () -> {
				if (TASmod.mc == null) return "";
				if (TASmod.mc.thePlayer == null) return "";
				return String.format("XYZ: %.2f %.2f %.2f", TASmod.mc.thePlayer.posX, TASmod.mc.thePlayer.posY, TASmod.mc.thePlayer.posZ);
			}));
			if (configuration.getProperty("precise_xyz_x", "err").equals("err")) setDefaults("precise_xyz");
			lists.add(new InfoLabel("precise_xyz", Integer.parseInt(configuration.getProperty("precise_xyz_x")), Integer.parseInt(configuration.getProperty("precise_xyz_y")), Boolean.parseBoolean(configuration.getProperty("precise_xyz_visible")), Boolean.parseBoolean(configuration.getProperty("precise_xyz_rect")), () -> {
				if (TASmod.mc == null) return "";
				if (TASmod.mc.thePlayer == null) return "";
				return String.format("Precise XYZ: %f %f %f", TASmod.mc.thePlayer.posX, TASmod.mc.thePlayer.posY, TASmod.mc.thePlayer.posZ);
			}));
			if (configuration.getProperty("chunk_xz_x", "err").equals("err")) setDefaults("chunk_xz");
			lists.add(new InfoLabel("chunk_xz", Integer.parseInt(configuration.getProperty("chunk_xz_x")), Integer.parseInt(configuration.getProperty("chunk_xz_y")), Boolean.parseBoolean(configuration.getProperty("chunk_xz_visible")), Boolean.parseBoolean(configuration.getProperty("chunk_xz_rect")), () -> {
				if (TASmod.mc == null) return "";
				if (TASmod.mc.thePlayer == null) return "";
				return String.format("Chunk: %d %d", TASmod.mc.thePlayer.chunkCoordX, TASmod.mc.thePlayer.chunkCoordZ);
			}));
			if (configuration.getProperty("worldseed_x", "err").equals("err")) setDefaults("worldseed");
			lists.add(new InfoLabel("worldseed", Integer.parseInt(configuration.getProperty("worldseed_x")), Integer.parseInt(configuration.getProperty("worldseed_y")), Boolean.parseBoolean(configuration.getProperty("worldseed_visible")), Boolean.parseBoolean(configuration.getProperty("worldseed_rect")), () -> {
				if (TASmod.mc == null) return "";
				if (TASmod.mc.theWorld == null) return "";
				return String.format("Worldseed: " + TASmod.mc.theWorld.getWorldSeed());
			}));
			if (configuration.getProperty("rngseed_x", "err").equals("err")) setDefaults("rngseed");
			lists.add(new InfoLabel("rngseed", Integer.parseInt(configuration.getProperty("rngseed_x")), Integer.parseInt(configuration.getProperty("rngseed_y")), Boolean.parseBoolean(configuration.getProperty("rngseed_visible")), Boolean.parseBoolean(configuration.getProperty("rngseed_rect")), () -> {
				return String.format("Randomness: " + SimpleRandomMod.seed);
			}));
			if (configuration.getProperty("velocity_x", "err").equals("err")) setDefaults("velocity");
			lists.add(new InfoLabel("velocity", Integer.parseInt(configuration.getProperty("velocity_x")), Integer.parseInt(configuration.getProperty("velocity_y")), Boolean.parseBoolean(configuration.getProperty("velocity_visible")), Boolean.parseBoolean(configuration.getProperty("velocity_rect")), () -> {
				if (TASmod.mc == null) return "";
				if (TASmod.mc.thePlayer == null) return "";
				return String.format("Velocity: %.2f %.2f %.2f", TASmod.mc.thePlayer.motionX, TASmod.mc.thePlayer.motionY, TASmod.mc.thePlayer.motionZ);
			}));
			if (configuration.getProperty("sprinting_x", "err").equals("err")) setDefaults("sprinting");
			lists.add(new InfoLabel("sprinting", Integer.parseInt(configuration.getProperty("sprinting_x")), Integer.parseInt(configuration.getProperty("sprinting_y")), Boolean.parseBoolean(configuration.getProperty("sprinting_visible")), Boolean.parseBoolean(configuration.getProperty("sprinting_rect")), () -> {
				if (TASmod.mc == null) return "";
				if (TASmod.mc.thePlayer == null) return "";
				return "Sprinting Hint: " + (!TASmod.mc.thePlayer.isSprinting() ? ((TASmod.mc.thePlayer.sprintToggleTimer > 5) ? "Unpress" : "Press") : "Hold");
			}));
			if (configuration.getProperty("keystrokes_x", "err").equals("err")) setDefaults("keystrokes");
			lists.add(new InfoLabel("keystrokes", Integer.parseInt(configuration.getProperty("keystrokes_x")), Integer.parseInt(configuration.getProperty("keystrokes_y")), Boolean.parseBoolean(configuration.getProperty("keystrokes_visible")), Boolean.parseBoolean(configuration.getProperty("keystrokes_rect")), () -> {
				if (TASmod.mc == null) return "";
				String out1 = TASmod.mc.currentScreen == TASmod.infoHud ? "Keybinds " : "";
				for (KeyBinding binds : TASmod.mc.gameSettings.keyBindings) {
					try {
						if (binds.pressed) out1 += org.lwjgl.input.Keyboard.getKeyName(binds.keyCode) + " ";
					} catch (Exception e3) {
						
					}
				}
				// Add left and right-click to the string if pressed.
				if (TASmod.mc.gameSettings.keyBindAttack.pressed) out1 += "LC ";
				if (TASmod.mc.gameSettings.keyBindUseItem.pressed) out1 += "RC ";
				return out1;
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Render the Info Hud only
	 */
	public void drawHud() {
		if (TASmod.mc != null) if (!Minecraft.isDebugInfoEnabled()) for (InfoLabel label : lists) {
			if (label.visible) {
				drawRectWithText(label.renderText, label.x, label.y, label.renderRect);
			} else if (TASmod.mc.currentScreen != null) {
				if (TASmod.mc.currentScreen.getClass().getSimpleName().contains("InfoHud")) {
					GL11.glPushMatrix();
		         	GL11.glEnable(GL11.GL_BLEND);
		         	GL11.glBlendFunc(770, 771);
		         	TASmod.mc.fontRenderer.drawStringWithShadow(label.renderText, label.x + 2, label.y + 3, 0x40FFFFFF);
		    		GL11.glDisable(GL11.GL_BLEND);
		         	GL11.glPopMatrix();
				}
			}
		}
	}
	
	/**
	 * Renders a Box with Text in it
	 */
	private void drawRectWithText(String text, int x, int y, boolean rect) {
		if (rect) drawRect(x, y, x + TASmod.mc.fontRenderer.getStringWidth(text) + 4, y + 14, 0x80000000);
		TASmod.mc.fontRenderer.drawStringWithShadow(text, x + 2, y + 3, 0xFFFFFF);
		GL11.glEnable(3042 /*GL_BLEND*/);
	}
	
}
