package net.tasmod.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import javax.swing.UIManager;

import net.minecraft.client.Minecraft;
import net.tasmod.TASmod;
import net.tasmod.Utils;

public class Start
{
	
	/** Whether the options.txt and infoGui.data should be saved or not */
	public static boolean isNormalLaunch;
	/** Whether the game should start already */
	public static boolean shouldStart;
	/** Directory for all TAS files */
	public static File tasDir;
	
	public static void main(final String[] args) throws Exception {
		if (args.length == 1) {
			tasDir = new File(new String(Base64.getDecoder().decode(args[0]), StandardCharsets.UTF_8));
			System.out.println("Using given TAS directory: " + tasDir.getAbsolutePath());
		} else {
			tasDir = new File(".").getAbsoluteFile();
		}

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		final File mcfolder = Files.createTempDirectory(".minecraft").toFile();
		if (!mcfolder.exists()) mcfolder.mkdir();

		// Change MC Settings
		final Field f = Minecraft.class.getDeclaredField("minecraftDir");
		AccessibleObject.setAccessible(new Field[] { f }, true);
		f.set(null, mcfolder);

		// Copy some basic minecraft files
		final File optionsTxt = new File(mcfolder, "options.txt");
		final File infoGuiCfg = new File(mcfolder, "infogui.cfg");
		final File originalOptionsTxt = new File("options.txt");
		final File originalInfoGuiCfg = new File("infogui.cfg");
		if (originalOptionsTxt.exists()) Files.copy(originalOptionsTxt.toPath(), optionsTxt.toPath(), StandardCopyOption.REPLACE_EXISTING);
		if (originalInfoGuiCfg.exists()) Files.copy(originalInfoGuiCfg.toPath(), infoGuiCfg.toPath(), StandardCopyOption.REPLACE_EXISTING);

		System.out.println("Running .minecraft in: " + mcfolder.getAbsolutePath());

		// Add a shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				// Save some files
				if (isNormalLaunch) {
					Files.copy(optionsTxt.toPath(), originalOptionsTxt.toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(infoGuiCfg.toPath(), originalInfoGuiCfg.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}

				Utils.deleteDirectory(mcfolder);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}));

		// Run Minecraft
		Minecraft.main(new String[0]);
		TASmod.mcThread.join();
//		EmulatorFrame.window.dispose();
	}

}
