package de.pfannekuchen.tasmodoginstaller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.ImageIcon;

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
					Files.copy(new URL("https://mgnet.work/TASmod_OG-" + (dialog.fullRadioBox.isSelected() ? "full" : "light") + "-" + (dialog.earlyAccessRadioBox.isSelected() ? "earlyaccess" : "release") + ".jar").openStream(), new File(instance, "minecraft.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				if (new File(instance, "minecraft.jar").exists()) {
					
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
	
}
