package net.tasmod.main;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.swing.UIManager;

import org.apache.openjpa.enhance.InstrumentationFactory;
import org.apache.openjpa.lib.log.NoneLogFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import net.minecraft.client.Minecraft;
import net.tasmod.TASmod;
import net.tasmod.Utils;
import net.tasmod.asm.RandomnessVisitor;
import net.tasmod.asm.VirtualInputVisitor;
import net.tasmod.asm.WeightedRandomnessVisitor;

public class Start
{

	/**
	 * List of classes that need their RNG to be weighted/removed
	 */
	public static final List<String> deadlockablerng = Arrays.asList(
			"net/minecraft/src/GuiEnchantment",
			"net/minecraft/src/TileEntityEnchantmentTable"
			);

	/**
	 * List of classes that need their RNG to be removed
	 */
	public static final List<String> rng = Arrays.asList(
			"net/minecraft/src/BlockBrewingStand",
			"net/minecraft/src/BlockChest",
			"net/minecraft/src/BlockDispenser",
			"net/minecraft/src/BlockFurnace",
			"net/minecraft/src/ContainerEnchantment",
			"net/minecraft/src/EffectRenderer",
			"net/minecraft/src/EnchantmentHelper",
			"net/minecraft/src/EnchantmentNameParts",
			"net/minecraft/src/Entity",
			"net/minecraft/src/Explosion",
			"net/minecraft/src/FontRenderer",
			"net/minecraft/src/GuiMainMenu",
			"net/minecraft/src/Item",
			"net/minecraft/src/NetClientHandler",
			"net/minecraft/src/RenderEnderman",
			"net/minecraft/src/SoundManager",
			"net/minecraft/src/SoundPool",
			"net/minecraft/src/Teleporter",
			"net/minecraft/src/TileEntityDispenser",
			"net/minecraft/src/GuiCreateWorld",
			"net/minecraft/src/World" // World is only being replaced in the constructor!
			);

	/**
	 * List of classes that need their Keyboard/Mouse to be removed
	 */
	public static final List<String> input = Arrays.asList(
			"net/minecraft/client/Minecraft",
			"net/minecraft/src/GuiContainer",
			"net/minecraft/src/GuiScreen",
			"net/minecraft/src/GuiAchievements",
			"net/minecraft/src/GuiContainerCreative",
			"net/minecraft/src/GuiSlot",
			"net/minecraft/src/GuiSlotStats",
			"net/minecraft/src/MouseHelper",
			"net/minecraft/src/EntityRenderer"
			);

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
		final Instrumentation inst = InstrumentationFactory.getInstrumentation(new NoneLogFactory().getLog("loggers"));
		inst.addTransformer(new ClassFileTransformer() {

			@Override
			public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
				if (!className.toLowerCase().startsWith("net/minecraft")) return classfileBuffer;
				final ClassReader reader = new ClassReader(classfileBuffer);
				final ClassWriter writer = new ClassWriter(reader, 0);

				if (rng.contains(className))
					reader.accept(RandomnessVisitor.classVisitor(className, writer), 0);
				else if (input.contains(className))
					reader.accept(VirtualInputVisitor.classVisitor(className, writer), 0);
				else if (deadlockablerng.contains(className))
					reader.accept(WeightedRandomnessVisitor.classVisitor(className, writer), 0);
				else
					return classfileBuffer;

				return writer.toByteArray();
			}
		});
		Utils.transformRandom();

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
		EmulatorFrame.window.dispose();
	}

}
