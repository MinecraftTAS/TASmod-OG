package com.minecrafttas.tasmodog.main;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import javax.swing.UIManager;

import net.minecraft.client.Minecraft;

public class Main {
	
	/**
	 * Launch TASmod
	 * @param args Launch parameters
	 * @throws Exception Filesystem/Internet Exception
	 */
	public static void main(final String[] args) throws Exception {
		// download natives
		File natives = new File("natives");
		if (!natives.exists()) {
			natives.mkdirs();
			
			String os = System.getProperty("os.name").toLowerCase();
			String url = "https://data.mgnet.work/mcp4gradle/natives/";
			if (os.contains("win")) {
				Files.copy(new URL(url + "jinput-dx8_64.dll").openStream(), new File(natives, "jinput-dx8_64.dll").toPath());
				Files.copy(new URL(url + "jinput-raw_64.dll").openStream(), new File(natives, "jinput-raw_64.dll").toPath());
				Files.copy(new URL(url + "lwjgl64.dll").openStream(), new File(natives, "lwjgl64.dll").toPath());
				Files.copy(new URL(url + "OpenAL64.dll").openStream(), new File(natives, "OpenAL64.dll").toPath());
			} else if (os.contains("mac")) {
				Files.copy(new URL(url + "libjinput-osx.dylib").openStream(), new File(natives, "libjinput-osx.dylib").toPath());
				Files.copy(new URL(url + "liblwjgl.dylib").openStream(), new File(natives, "liblwjgl.dylib").toPath());
				Files.copy(new URL(url + "openal.dylib").openStream(), new File(natives, "openal.dylib").toPath());
			} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				Files.copy(new URL(url + "libjinput-linux64.so").openStream(), new File(natives, "libjinput-linux64.so").toPath());
				Files.copy(new URL(url + "liblwjgl64.so").openStream(), new File(natives, "liblwjgl64.so").toPath());
				Files.copy(new URL(url + "libopenal64.so").openStream(), new File(natives, "libopenal64.so").toPath());
			}
		}
		
		// load natives
		System.setProperty("org.lwjgl.librarypath", natives.getAbsolutePath());
		System.setProperty("net.java.games.input.librarypath", natives.getAbsolutePath());
		
		// load system look and feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		// launch game
		Minecraft.main(new String[0]);

	}

	/**
	 * Setup minecraft filestructure
	 * @param isTemp Should run in main .minecraft or temporary .minecraft folder
	 * @throws Exception Filesystem Exception
	 */
	public static void setupFilestructure(boolean isTemp) throws Exception {
		// setup file structure
		File mainMinecraft = new File(".minecraft");
		if (isTemp) {
			File tempMinecraft = new File(".instance-" + System.currentTimeMillis());
			File optionsTxt = new File(mainMinecraft, "options.txt");
			File infoGuiCfg = new File(mainMinecraft, "infogui.cfg");
			File newOptionsTxt = new File(tempMinecraft, "options.txt");
			File newInfoGuiCfg = new File(tempMinecraft, "infogui.cfg");
			
			tempMinecraft.mkdirs();
			if (optionsTxt.exists()) Files.copy(optionsTxt.toPath(), newOptionsTxt.toPath());
			if (infoGuiCfg.exists()) Files.copy(infoGuiCfg.toPath(), newInfoGuiCfg.toPath());
			Minecraft.minecraftDir = tempMinecraft;
		} else {
			Minecraft.minecraftDir = mainMinecraft;
			mainMinecraft.mkdirs();
		}
	}

}
