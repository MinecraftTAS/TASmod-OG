package com.minecrafttas.tasmodog.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;
import net.minecraft.src.WorldSettings;

/**
 * Save and loadstating utils
 */
public class StateUtils {

	/**
	 * Create state
	 * @param mc
	 * @return
	 * @throws Exception
	 */
	public static byte[] savestate(Minecraft mc) throws Exception {
		
		// save world
		mc.theWorld.saveWorldIndirectly(null);
		
		// recursively save 'saves' folder to byte array		
		Path savesDir = new File(Minecraft.minecraftDir, "saves").toPath().toAbsolutePath();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		recursiveSave(savesDir, savesDir.relativize(savesDir), new DataOutputStream(stream));
		stream.close();
		return stream.toByteArray();
	}

	/**
	 * Recursively write folder to data stream
	 * @param root Root directory
	 * @param currentDir Current directory
	 * @param out Output stream
	 * @throws Exception
	 */
	private static void recursiveSave(Path root, Path currentDir, DataOutputStream out) throws Exception {
		// recurse through directories
		if (Files.isDirectory(currentDir)) {
			for (Path f : Files.list(root.resolve(currentDir)).toArray(Path[]::new))
				recursiveSave(root, root.resolve(f), out);
			
			return;
		}
		
		// write path
		byte[] pathBytes = root.relativize(currentDir).toString().getBytes();
		out.write(1);
		out.writeInt(pathBytes.length);
		out.write(pathBytes);
		
		// write file content
		byte[] data = Files.readAllBytes(currentDir);
		out.writeInt(data.length);
		out.write(data);
		
		System.out.println("Encoded " + new String(pathBytes) + " (" + data.length + " bytes)");
	}

	/**
	 * Load state
	 * @param mc Minecraft instance
	 * @param state State data
	 * @throws Exception Filesystem exception
	 */
	public static void loadstate(Minecraft mc, byte[] state) throws Exception {
		// get world info
		ISaveHandler handler = mc.theWorld.saveHandler;
		WorldInfo worldInfo = mc.theWorld.getWorldInfo();
		String name = worldInfo.getWorldName();
		WorldSettings settings = new WorldSettings(worldInfo.getRandomSeed(), worldInfo.getGameType(), worldInfo.isMapFeaturesEnabled(), worldInfo.isHardcoreModeEnabled());
		
		// exit world
		mc.changeWorld1(null);

		// load files
		Path savesDir = new File(Minecraft.minecraftDir, "saves").toPath().toAbsolutePath();
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(state));
		while (stream.read() != -1) {
			// read path
			byte[] pathBytes = new byte[stream.readInt()];
			stream.read(pathBytes);

			// read content
			byte[] data = new byte[stream.readInt()];
			stream.read(data);

			// write file
			String pathString = new String(pathBytes);
			Path path = savesDir.resolve(pathString);
			Files.createDirectories(path.getParent());
			Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			System.out.println("Decoded " + pathString + " (" + data.length + " bytes)");
		}
		
		// reenter world
		mc.changeWorld1(new World(handler, name, settings));
	}
	
}
