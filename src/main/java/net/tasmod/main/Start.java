package net.tasmod.main;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import net.minecraft.client.Minecraft;
import net.tasmod.TASmod;
import net.tasmod.Utils;
import net.tasmod.Utils.OS;

public class Start {
	
	public static void main(final String[] args) throws Exception {	
		// download natives
		File natives = new File("natives");
		if (!natives.exists()) {
			natives.mkdirs();
			
			OS os = Utils.getOS();
			String url = "https://data.mgnet.work/mcp4gradle/natives/";
			if (os == OS.WINDOWS) {
				Files.copy(new URL(url + "jinput-dx8_64.dll").openStream(), new File(natives, "jinput-dx8_64.dll").toPath());
				Files.copy(new URL(url + "jinput-raw_64.dll").openStream(), new File(natives, "jinput-raw_64.dll").toPath());
				Files.copy(new URL(url + "lwjgl64.dll").openStream(), new File(natives, "lwjgl64.dll").toPath());
				Files.copy(new URL(url + "OpenAL64.dll").openStream(), new File(natives, "OpenAL64.dll").toPath());
			} else if (os == OS.MACOS) {
				Files.copy(new URL(url + "libjinput-osx.dylib").openStream(), new File(natives, "libjinput-osx.dylib").toPath());
				Files.copy(new URL(url + "liblwjgl.dylib").openStream(), new File(natives, "liblwjgl.dylib").toPath());
				Files.copy(new URL(url + "openal.dylib").openStream(), new File(natives, "openal.dylib").toPath());
			} else if (os == OS.LINUX) {
				Files.copy(new URL(url + "libjinput-linux64.so").openStream(), new File(natives, "libjinput-linux64.so").toPath());
				Files.copy(new URL(url + "liblwjgl64.so").openStream(), new File(natives, "liblwjgl64.so").toPath());
				Files.copy(new URL(url + "libopenal64.so").openStream(), new File(natives, "libopenal64.so").toPath());
			}
		}
		
		// load natives
		System.setProperty("org.lwjgl.librarypath", natives.getAbsolutePath());
		System.setProperty("net.java.games.input.librarypath", natives.getAbsolutePath());
		
		// launch game
		Minecraft.main(new String[0]);

	}

	public static void startGame(boolean isTemp) throws Exception {
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
		
		// launch game
		TASmod.isRunning = true;
	}

}
