package net.tasmod;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.client.Minecraft;
import net.tasmod.random.SimpleRandomMod;
import net.tasmod.virtual.VirtualKeyboard;
import net.tasmod.virtual.VirtualMouse;

/**
 * Class that contains useful stuff
 * @author Pancake
 */
public final class Utils {

	/**
	 * Small Utility that deletes a Directory recursively
	 */
	public static boolean deleteDirectory(final File directoryToBeDeleted) {
		final File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null)
			for (final File file : allContents)
				deleteDirectory(file);
		return directoryToBeDeleted.delete();
	}

	/**
	 * Transforms the Random Variable for Math.random()
	 */
	public static void transformRandom() throws Exception {
		/* Get Fields for the Random Value used in Math */
		final Field mathRandomField = Class.forName("java.lang.Math$RandomNumberGeneratorHolder").getDeclaredField("randomNumberGenerator");
		mathRandomField.setAccessible(true);
		/* Remove Final */
		final Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(mathRandomField, mathRandomField.getModifiers() & ~Modifier.FINAL);
		/* Replace Random of Math with Modded one */
		mathRandomField.set(null, new SimpleRandomMod());
	}

	/**
	 * Obtains the inaccessible Singleton from the Minecraft Class
	 */
	public static Minecraft obtainMinecraftInstance() throws Exception {
		/* Get Field in Obfuscated or Non-Obfuscated Environment */
		final Class<?> clazz = Class.forName("net.minecraft.client.Minecraft");
		Field theMinecraftField;
		try {
			theMinecraftField = clazz.getDeclaredField("theMinecraft");
		} catch (final Exception e) {
			theMinecraftField = clazz.getDeclaredField("a");
		}
		theMinecraftField.setAccessible(true);
		return (Minecraft) theMinecraftField.get(null);
	}

	public static void lazyMouse() {
		if (VirtualMouse.getEventButton() == 0) VirtualMouse.isButton0Down = VirtualMouse.getEventButtonState();
	}

	public static void lazyKeyboard() {
		if (VirtualKeyboard.getEventKey() == 37) VirtualKeyboard.isKey37Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 42) VirtualKeyboard.isKey42Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 54) VirtualKeyboard.isKey54Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 60) VirtualKeyboard.isKey60Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 61) VirtualKeyboard.isKey61Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 51) VirtualKeyboard.isKey51Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 52) VirtualKeyboard.isKey52Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 65) VirtualKeyboard.isKey65Down = VirtualKeyboard.getEventKeyState();
	}

}
