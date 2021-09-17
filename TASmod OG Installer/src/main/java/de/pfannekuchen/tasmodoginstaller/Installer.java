package de.pfannekuchen.tasmodoginstaller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Installer {
	
	public static void main(String[] args) throws Exception {
		/* Create the Window */
		File potionFile = File.createTempFile("potion", ".png");
		Files.copy(new URL("http://mgnet.work/potion.png").openStream(), potionFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		InstallerWindow dialog = new InstallerWindow();
		dialog.setResizable(false);
		dialog.setSize(400, 400);
		dialog.setIconImage(new ImageIcon(Files.readAllBytes(potionFile.toPath())).getImage());
		dialog.setTitle("TASmod OG Launcher");
		dialog.setVisible(true);
		
		dialog.launchButton.addActionListener((l) -> {
			dialog.dispose();
			File instance = new File("tasmod_data");
			instance.mkdir();
			try {
				if (netIsAvailable()) {
					System.out.println("Downloading Game Files..");
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/jinput-dx8.dll").openStream(), new File(instance, "jinput-dx8.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/jinput-dx8_64.dll").openStream(), new File(instance, "jinput-dx8_64.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/jinput-raw.dll").openStream(), new File(instance, "jinput-raw.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/jinput-raw_64.dll").openStream(), new File(instance, "jinput-raw_64.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/lwjgl.dll").openStream(), new File(instance, "lwjgl.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/lwjgl64.dll").openStream(), new File(instance, "lwjgl64.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/OpenAL32.dll").openStream(), new File(instance, "OpenAL32.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/OpenAL64.dll").openStream(), new File(instance, "OpenAL64.dll").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/libjinput-osx.dylib").openStream(), new File(instance, "libjinput-osx.dylib").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/liblwjgl.dylib").openStream(), new File(instance, "liblwjgl.dylib").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/openal.dylib").openStream(), new File(instance, "openal.dylib").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/libjinput-linux.so").openStream(), new File(instance, "libjinput-linux.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/libjinput-linux64.so").openStream(), new File(instance, "libjinput-linux64.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/liblwjgl.so").openStream(), new File(instance, "liblwjgl.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/liblwjgl64.so").openStream(), new File(instance, "liblwjgl64.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/libopenal.so").openStream(), new File(instance, "libopenal.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/cfg/1.0natives/libopenal64.so").openStream(), new File(instance, "libopenal64.so").toPath(), StandardCopyOption.REPLACE_EXISTING);
					Files.copy(new URL("https://mgnet.work/TASmod_OG-" + (dialog.fullRadioBox.isSelected() ? "full" : "light") + "-" + (dialog.earlyAccessRadioBox.isSelected() ? "earlyaccess" : "release") + ".jar").openStream(), new File(instance, "minecraft.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				if (new File(instance, "minecraft.jar").exists()) {
					findJRE8();
					ProcessBuilder builder = new ProcessBuilder(new String[] {javaexe.getAbsolutePath(), "-Djdk.attach.allowAttachSelf=true", "-Djava.library.path=.", "-jar", "minecraft.jar"});
					builder.directory(instance);
					builder.inheritIO();
					builder.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
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