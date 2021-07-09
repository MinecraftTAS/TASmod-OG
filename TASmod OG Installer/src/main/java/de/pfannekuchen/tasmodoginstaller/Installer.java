package de.pfannekuchen.tasmodoginstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;

import jadretro.Main;
import mcinjector.MCInjectorImpl;
import net.lingala.zip4j.ZipFile;
import retrogradle.NameProvider;
import retrogradle.obf.RetroGuardImpl;

public class Installer {

	public static String channel = "early-access";
	public static boolean isLight = false;
	
	public static void main(String[] args) throws Exception {
		/* Update Data */
		File instance = new File("instance");
		File updateFile = new File("update");
		if (updateFile.exists()) {
			List<String> lines = Files.readAllLines(updateFile.toPath());
			channel = lines.get(0);
			isLight = Boolean.parseBoolean(lines.get(1));
		}
		/* Create the Window */
		File potionFile = File.createTempFile("potion", ".png");
		Files.copy(new URL("http://mgnet.work/potion.png").openStream(), potionFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		InstallerWindow dialog = new InstallerWindow();
		dialog.setResizable(false);
		dialog.setSize(400, 400);
		dialog.setIconImage(new ImageIcon(Files.readAllBytes(potionFile.toPath())).getImage());
		dialog.setTitle("TASmod OG Launcher");
		dialog.launchButton.setEnabled(updateFile.exists());
		if (!updateFile.exists()) {
			dialog.updateButton.setText("Install");
		} else {
			dialog.updateButton.setText("Update");
		}
		dialog.setVisible(true);
		
		dialog.updateButton.addActionListener((l) -> {
			String channelToInstall = dialog.devRadioBox.isSelected() ? "dev" : (dialog.earlyAccessRadioBox.isSelected() ? "early-access" : "release");
			File cloneDir = new File(".clone");
			/* Uninstall */
			instance.delete();
			updateFile.delete();
			/* Install */
			new Thread(() -> {
				try {
					FileUtils.deleteDirectory(cloneDir);
					dialog.progressBar.setValue(0);
					Git.cloneRepository().setURI("https://github.com/MCPfannkuchenYT/TASmod-1.0-OG").setDirectory(cloneDir).call();
					Git.shutdown();
					dialog.progressBar.setValue(20);
					install(new File(cloneDir, (dialog.lightRadioBox.isSelected() ? "TASmod OG Light" : "TASmod OG Full") + "/src/main/java/"), new File(cloneDir, (dialog.lightRadioBox.isSelected() ? "TASmod OG Light" : "TASmod OG Full") + "/change.patch"));
					dialog.progressBar.setValue(100);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}).start();
			/* Update File */
			try {
				Files.write(updateFile.toPath(), Arrays.asList(channelToInstall, dialog.lightRadioBox.isSelected() + ""), StandardOpenOption.CREATE);
			} catch (IOException e) {
				e.printStackTrace();
			}
			/* Update Btns */
			dialog.updateButton.setText("Update");
			dialog.launchButton.setEnabled(true);
		});
	}

	/**
	 * Installs the actual instance
	 */
	private static void install(File srcDir, File patchFile) throws Exception {
		/* RetroGuard */
		System.out.println("Running Retroguard [De]obfuscator");
		File minecraft_rg_jar = Utils.tempFile();
		File client_rg_cfg = Utils.tempFile();
		// create new config file with above paths
		Files.write(client_rg_cfg.toPath(), Arrays.asList(
				"startindex = 0",
				"input = " + Utils.obtainTempFile(new URL("https://mgnet.work/repo/com/mojang/minecraft/1.0/minecraft-1.0.jar")).getAbsolutePath(),
				"output = " + minecraft_rg_jar.getAbsolutePath(),
				"reobinput = " + Utils.tempFile().getAbsolutePath(),
				"reoboutput = " + Utils.tempFile().getAbsolutePath(),
				"script = " + Utils.obtainTempFile(new URL("https://mgnet.work/cfg/retroguard1.0.cfg")).getAbsolutePath(),
				"log = " + Utils.tempFile().getAbsolutePath(),
				"deob = " + Utils.obtainTempFile(new URL("https://mgnet.work/cfg/client_rg1.0.srg")).getAbsolutePath(),
				"protectedpackage = paulscode",	
				"protectedpackage = com/jcraft",
				"protectedpackage = isom",
				"protectedpackage = ibxm",
				"protectedpackage = de/matthiasmann/twl",
				"protectedpackage = org/xmlpull",
				"protectedpackage = javax/xml"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		// run retroguard the bad way
		String[] args = NameProvider.parseCommandLine(new String[] {"-searge", client_rg_cfg.getAbsolutePath()});
		RetroGuardImpl.obfuscate((args.length < 1 ? null : args[0]), (args.length < 2 ? null : args[1]), (args.length < 3 ? null : args[2]), (args.length < 4 ? null : args[3]));
		System.out.println("Running MCInjector...");
		/* MCInjector */
		File minecraft_exc_jar = Utils.tempFile();
		// run mcinjector the actual right way
		MCInjectorImpl.process(minecraft_rg_jar.getAbsolutePath(), minecraft_exc_jar.getAbsolutePath(), Utils.obtainTempFile(new URL("https://mgnet.work/cfg/client1.0.exc")).getAbsolutePath(), Utils.tempFile().getAbsolutePath(), null, 0);
		System.out.println("Applying Jad Retro to sources...");
		/* Jadretro */
        new File(new File("build"), "src/minecraft").mkdirs(); // dumb.. create the build folder first
        System.out.println("Unzipping Sources..");
        // unzip files
        ZipFile f = new ZipFile(minecraft_exc_jar); f.extractAll(new File(new File("build"), "bin/minecraft").getAbsolutePath()); f.close();
        // run jadretro the cli way
        Main.main(new String[] { new File(new File("build"), "bin/minecraft/net/minecraft/client").getAbsolutePath()});
        Main.main(new String[] { new File(new File("build"), "bin/minecraft/net/minecraft/src").getAbsolutePath()});
        System.out.println("Decompiling using Jad..");
        /* Jad */
        File jad_exe = Utils.obtainTempFile(new URL("https://mgnet.work/cfg/jad1.0.exe"));
        // jad is not a java program (???) so we run it via cli
        Utils.run(Arrays.asList(jad_exe.getAbsolutePath(), "-b", "-d", "src/minecraft", "-dead", "-o", "-r", "-s", ".java", "-stat", "-v", "-ff", "bin/minecraft\\net\\minecraft\\client\\*.class"), new File("build"), false);
        Utils.run(Arrays.asList(jad_exe.getAbsolutePath(), "-b", "-d", "src/minecraft", "-dead", "-o", "-r", "-s", ".java", "-stat", "-v", "-ff", "bin/minecraft\\net\\minecraft\\src\\*.class"), new File("build"), false);
        System.out.println("Fixing up Sources..");
        /* Applydiff - few diffs so mcp works. */
        Utils.run(Arrays.asList(Utils.obtainTempFile(new URL("https://mgnet.work/cfg/applydiff1.0.exe")).getAbsolutePath(), "--binary", "-p1", "-u", "-i", Utils.obtainTempFile(new URL("https://mgnet.work/cfg/temp1.0.patch")).getAbsolutePath(), "-d", "src/minecraft"), new File("build"), false);
        System.out.println("Renaming Sources..");
        /* Rename Sources */
        // read csv files
        CSVParser functionParser = CSVFormat.DEFAULT.parse(new FileReader(Utils.obtainTempFile(new URL("https://mgnet.work/cfg/methods1.0.csv"))));
        CSVParser methodParser = CSVFormat.DEFAULT.parse(new FileReader(Utils.obtainTempFile(new URL("https://mgnet.work/cfg/fields1.0.csv"))));
        HashMap<String, String> functionRefmap = new HashMap<>();
        for (CSVRecord csvRecord : functionParser) {
        	if (csvRecord.get(8).equals("0")) functionRefmap.put(csvRecord.get(0), csvRecord.get(1));
		}
        HashMap<String, String> methodRefmap = new HashMap<>();
        for (CSVRecord csvRecord : methodParser) {
        	if (csvRecord.get(8).equals("0")) methodRefmap.put(csvRecord.get(0), csvRecord.get(1));
		}
        // modify all files
        new File(new File("build"), "src").listFiles(new FilenameFilter() { @Override public boolean accept(File dir, String name) {
        	try {
				File theFile = new File(dir, name);
				if (theFile.isDirectory()) theFile.listFiles(this);
				else if (theFile.getName().toLowerCase().endsWith(".java")) {
					// read file
					FileReader reader = new FileReader(theFile);
					String fileContent = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
					String orig = fileContent + "";
					reader.close();
					// replace funcs
					Matcher func = Pattern.compile("func_[0-9]+_[a-zA-Z]+_?").matcher(fileContent);
					while (func.find()) {
						String match = func.group();
						if (functionRefmap.containsKey(match)) fileContent = fileContent.replaceAll(match, functionRefmap.get(match));
					}
					// replace methods
					Matcher method = Pattern.compile("field_[0-9]+_[a-zA-Z]+_?").matcher(fileContent);
					while (method.find()) {
						String match = method.group();
						if (methodRefmap.containsKey(match)) fileContent = fileContent.replaceAll(match, methodRefmap.get(match));
					}
					// replace opengl
					if (fileContent.contains("import org.lwjgl.opengl.")) {
						Matcher opengl = Pattern.compile("(?<![.\\w])\\d+(?![.\\w])(?! /\\*GL_)").matcher(fileContent);
						while (opengl.find()) {
							String match = opengl.group(0);
							if (Utils.map.containsKey(Integer.parseInt(match))) fileContent = fileContent.replaceAll(match + "\\)", match + " /*" + Utils.map.get(Integer.parseInt(match)) + "*/)").replaceAll(match + ",", match + " /*" + Utils.map.get(Integer.parseInt(match)) + "*/,");
							
						}
					}
					// write file again
					if (!orig.equals(fileContent)) {
						FileWriter writer = new FileWriter(theFile, false);
						writer.write(fileContent + "\n");
						writer.flush();
						writer.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return true; } });
        System.out.println("Linuxifying Sources..");
        /* Convert to unix format forever.. */
        Utils.run(Arrays.asList("C:\\Windows\\System32\\wsl.exe", "find", "./src", "-type", "f", "-exec", "dos2unix", "{}", "\\;"), new File("build"), false); // linuxify
        System.out.println("Garbage Collecting...");
        System.gc();
        /* Copy back source */
		System.out.println("Deleting sources...");
        new File(new File("build2"), "src/main/java").mkdirs();
		new File(new File("build2"), "src/main/java/net/minecraft").delete();
		System.out.println("Copying sources...");
		System.gc();
		Utils.copyFolder(new File(new File("build"), "src/minecraft").toPath(), new File(new File("build2"), "src/main/java").toPath(), StandardCopyOption.REPLACE_EXISTING);
        /* Patch source */
		// TODO
		System.err.println("Repacking...");
		File jarFile = new File(new File("build"), "libs/").listFiles()[0];
		// download resources
		File resDir = new File(new File("build"), "resources1.0");
		resDir.mkdirs();
		ZipFile res = new ZipFile(Utils.obtainTempFile(new URL("https://mgnet.work/cfg/resources1.0.zip")));
		res.extractAll(resDir.getAbsolutePath());
		ZipFile orig = new ZipFile(jarFile);
		orig.removeFile("me.class");
		resDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				try {
					File fileOrFolder = new File(dir, name);
					if (fileOrFolder.isDirectory()) orig.addFolder(fileOrFolder);
					else orig.addFile(fileOrFolder);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		});
		orig.close();
		System.err.println("Reobfuscating...");
		File deobfFile = new File(new File("build"), "minecraft.jar");
		File config = Utils.tempFile();
		Files.write(config.toPath(), Arrays.asList(
				"startindex = 0",
				"reobinput = " + jarFile.getAbsolutePath(),
				"reoboutput = " + deobfFile.getAbsolutePath(),
				"script = " + Utils.obtainTempFile(new URL("https://mgnet.work/cfg/retroguard_ro1.0.cfg")).getAbsolutePath(),
				"reob = " + Utils.obtainTempFile(new URL("https://mgnet.work/cfg/client_ro1.0.srg")).getAbsolutePath(),
				"log = " + Utils.tempFile().getAbsolutePath(),
				"rolog = " + Utils.tempFile().getAbsolutePath(),
				"protectedpackage = paulscode",	
				"protectedpackage = com/jcraft",
				"protectedpackage = isom",
				"protectedpackage = ibxm",
				"protectedpackage = de/matthiasmann/twl",
				"protectedpackage = org/xmlpull",
				"protectedpackage = javax/xml"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		// run retroguard the bad way
		String[] args2 = NameProvider.parseCommandLine(new String[] {"-notch", config.getAbsolutePath()});
		RetroGuardImpl.obfuscate((args2.length < 1 ? null : args2[0]), (args2.length < 2 ? null : args2[1]), (args2.length < 3 ? null : args2[2]), (args2.length < 4 ? null : args2[3]));
	}
}
