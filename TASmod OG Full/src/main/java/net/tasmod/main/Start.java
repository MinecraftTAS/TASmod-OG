package net.tasmod.main;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

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
			"net/minecraft/src/World" // World is only being replaced in the contructor!
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
	
	
	public static void main(String[] args) throws Exception {
		Instrumentation inst = InstrumentationFactory.getInstrumentation(new NoneLogFactory().getLog("loggers"));
		inst.addTransformer(new ClassFileTransformer() {
			
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
				if (!className.toLowerCase().startsWith("net/minecraft")) return classfileBuffer;
				ClassReader reader = new ClassReader(classfileBuffer);
				ClassWriter writer = new ClassWriter(reader, 0);
				
				if (rng.contains(className)) {
					reader.accept(RandomnessVisitor.classVisitor(className, writer), 0);
				} else if (input.contains(className)) {
					reader.accept(VirtualInputVisitor.classVisitor(className, writer), 0);
				} else if (deadlockablerng.contains(className)) {
					reader.accept(WeightedRandomnessVisitor.classVisitor(className, writer), 0);
				} else {
					return classfileBuffer;
				}
				
				return writer.toByteArray();
			}
		});
		Utils.transformRandom();
		
		File mcfolder = new File("tas.minecraft");
		if (mcfolder.exists()) Utils.deleteDirectory(mcfolder);
		
		// Change MC Settings
		Field f = Minecraft.class.getDeclaredField("minecraftDir");
		Field.setAccessible(new Field[] { f }, true);
		f.set(null, mcfolder);
		
		launch();
	}
	
	/**
	 * Launches the Game with specific TAS Modes
	 */
	public static void launch() throws Exception {
		File settings = new File("tas.settings");
		if (settings.exists()) {
			// If settings are given, set the to be ran once the game first ticks
			List<String> lines = Files.readAllLines(settings.toPath());
			TASmod.shouldRecordOrPlayback = "R".equalsIgnoreCase(lines.get(0));
			TASmod.tasFile = lines.get(1);
			settings.delete();
		}
		
		// Run Minecraft
		Minecraft.main(new String[0]);
		
		// Stop the recording after the game is closed
		if (TASmod.isRecording()) TASmod.getRecording().endRecording();
	}
	
}
