package net.tasmod.infogui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import net.minecraft.src.GuiScreen;
import net.tasmod.TASmod;
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
			int h = y + 25;
			
			if (mouseX >= x && mouseX <= w && mouseY >= y && mouseY <= h && label.visible) {
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
		System.out.println(mouseButton);
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
	 * Render the Info Hud Customization menu
	 */
	@Override public void drawScreen(int i, int j, float f) {
		super.drawScreen(i, j, f);
		drawHud();
		drawString(TASmod.mc.fontRenderer, "Right-Click to hide rect", 2, 2, 0xFFFFFF);
	}
	
	/**
	 * Updates every tick
	 */
	public void tick() {
		if (checkInit()) return;
		for (InfoLabel label : lists) label.tick();
	}
	
	public boolean checkInit() {
		if (configuration != null) return true;
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
				return "Tickrate: " + TickrateChanger.availableGamespeeds[TickrateChanger.selectedGamespeed];
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Render the Info Hud only
	 */
	public void drawHud() {
		for (InfoLabel label : lists) if (label.visible) drawRectWithText(label.renderText, label.x, label.y, label.renderRect);
	}
	
	/**
	 * Renders a Box with Text in it
	 */
	private void drawRectWithText(String text, int x, int y, boolean rect) {
		if (rect) drawRect(x, y, x + TASmod.mc.fontRenderer.getStringWidth(text) + 4, y + 14, -2147483648);
		TASmod.mc.fontRenderer.drawStringWithShadow(text, x + 2, y + 3, 0xFFFFFF);
	}
	
}
