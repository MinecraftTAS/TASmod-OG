package de.pfannekuchen.tasmodoginstaller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import javax.swing.JOptionPane;

import de.pfannekuchen.tasmodoginstaller.Utils.OS;

public class InstallerBackend {

	public static void download(boolean fullOrLight, boolean earlyAccessOrNot, boolean cacheOrNot) throws IOException, URISyntaxException {
		File instance = cacheOrNot ? new File("tasmodog") : Files.createTempDirectory("tasmodog").toFile();
		if (!instance.exists()) instance.mkdir();
		OS os = Utils.getOS();
		if (netIsAvailable()) {
			if (os == OS.WINDOWS) {
				if (!new File(instance, "jinput-dx8.dll").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/jinput-dx8.dll").openStream(), new File(instance, "jinput-dx8.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "jinput-dx8_64.dll").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/jinput-dx8_64.dll").openStream(), new File(instance, "jinput-dx8_64.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "jinput-raw.dll").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/jinput-raw.dll").openStream(), new File(instance, "jinput-raw.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "jinput-raw_64.dll").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/jinput-raw_64.dll").openStream(), new File(instance, "jinput-raw_64.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "lwjgl.dll").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/lwjgl.dll").openStream(), new File(instance, "lwjgl.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "lwjgl64.dll").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/lwjgl64.dll").openStream(), new File(instance, "lwjgl64.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "OpenAL32.dll").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/OpenAL32.dll").openStream(), new File(instance, "OpenAL32.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "OpenAL64.dll").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/OpenAL64.dll").openStream(), new File(instance, "OpenAL64.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else if (os == OS.MACOS) {
				if (!new File(instance, "libjinput-osx.dylib").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/libjinput-osx.dylib").openStream(), new File(instance, "libjinput-osx.dylib").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "liblwjgl.dylib").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/liblwjgl.dylib").openStream(), new File(instance, "liblwjgl.dylib").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "openal.dylib").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/openal.dylib").openStream(), new File(instance, "openal.dylib").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else if (os == OS.LINUX) {
				if (!new File(instance, "libjinput-linux.so").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/libjinput-linux.so").openStream(), new File(instance, "libjinput-linux.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "libjinput-linux64.so").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/libjinput-linux64.so").openStream(), new File(instance, "libjinput-linux64.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "liblwjgl.so").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/liblwjgl.so").openStream(), new File(instance, "liblwjgl.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "liblwjgl64.so").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/liblwjgl64.so").openStream(), new File(instance, "liblwjgl64.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "libopenal.so").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/libopenal.so").openStream(), new File(instance, "libopenal.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (!new File(instance, "libopenal64.so").exists()) Files.copy(new URL("https://data.mgnet.work/mcp4gradle/natives/libopenal64.so").openStream(), new File(instance, "libopenal64.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			Files.copy(new URL("https://data.mgnet.work/tasmodog/TASmod_OG-" + (fullOrLight ? "full" : "light") + "-" + (earlyAccessOrNot ? "earlyaccess" : "release") + ".jar").openStream(), new File(instance, "minecraft.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		if (new File(instance, "minecraft.jar").exists()) {
			findJRE8();
			ProcessBuilder builder = new ProcessBuilder(new String[] {javaexe.getAbsolutePath(), "-Djdk.attach.allowAttachSelf=true", "-Djava.library.path=.", "-jar", "minecraft.jar", Base64.getEncoder().encodeToString(new File("tasmodog").getParentFile().getAbsolutePath().getBytes(StandardCharsets.UTF_8))});
			builder.directory(instance);
			builder.inheritIO();
			builder.start();
		}
	}
	
	private static boolean netIsAvailable() {
	    try {
	        final URL url = new URL("https://mgnet.work/");
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        conn.getInputStream().close();
	        return true;
	    } catch (MalformedURLException e) {
	        throw new RuntimeException(e);
	    } catch (IOException e) {
	        return false;
	    }
	}
	
	/** Detected Java 1.8 java.exe from {@link #findJRE8()} */
	private static File javaexe = null;
	
	/**
	 * Go through known Java Installation Locations and try to find Java 1.8
	 */
	private static void findJRE8() throws IOException, URISyntaxException {
		File programFilesFolder = new File("C:\\Program Files\\");
		File programFilesx86Folder = new File("C:\\Program Files (x86)\\");
		
		if (programFilesFolder.exists()) {
			// Check all known Dirs
			checkDir(programFilesFolder, "AdoptOpenJDK", "jdk-8");
			checkDir(programFilesFolder, "Java", "jdk1.8");
			checkDir(programFilesFolder, "Java", "jdk8");
			checkDir(programFilesFolder, "Oracle\\Java", "jdk1.8");
			checkDir(programFilesFolder, "Oracle\\Java", "jdk8");
		}
		if (programFilesx86Folder.exists()) {
			// Check all known Dirs in the 32-bit Program Files Folder
			checkDir(programFilesx86Folder, "AdoptOpenJDK", "jdk-8");
			checkDir(programFilesx86Folder, "Java", "jdk1.8");
			checkDir(programFilesx86Folder, "Java", "jdk8");
			checkDir(programFilesx86Folder, "Oracle\\Java", "jdk1.8");
			checkDir(programFilesx86Folder, "Oracle\\Java", "jdk8");
		}
		
		if (javaexe == null) {
			javaexe = new File("/bin/java");
			if (javaexe.exists()) return;
			javaexe = new File("/usr/bin/java");
			if (javaexe.exists()) return;
			/* Display an Error Message that the Program didn't work with the unsupported Java version, and the Program couldn't find one installed on the PC */
			JOptionPane.showConfirmDialog(null, "The Program couldn't launch\nand we couldn't find Java 1.8 on your PC :(", "Couldn't attach to JVM.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Check a given Directory for Java 1.8 using a search term
	 */
	private static void checkDir(final File programFilesFolder, final String javaDir, final String searchTerm) {
		if (new File(programFilesFolder, javaDir).exists()) 
			new File(programFilesFolder, javaDir).listFiles((dir, name) -> { 										// Go through all Files and
				if (name.toLowerCase().startsWith(searchTerm)) javaexe = new File(dir, name + "\\bin\\java.exe");	// check if it starts with the searchTerm
				return false;
			});
	}
	
}
