package net.tasmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.src.StringTranslate;

public final class Utils {

	public static int dX;
	public static int dY;
	
	public static float rotationPitch = 0f;
	public static float rotationYaw = 0f;
	public static float prevRotationPitch = 0f;
	public static float prevRotationYaw = 0f;
	
    public static void setAngles(float f, float f1) {
        float f2 = rotationPitch;
        float f3 = rotationYaw;
        rotationYaw += (double)f * 0.14999999999999999D;
        rotationPitch -= (double)f1 * 0.14999999999999999D;
        if(rotationPitch < -90F)
        {
            rotationPitch = -90F;
        }
        if(rotationPitch > 90F)
        {
            rotationPitch = 90F;
        }
        prevRotationPitch += rotationPitch - f2;
        prevRotationYaw += rotationYaw - f3;
    }
	
	public static final boolean deleteDirectory(final File directoryToBeDeleted) {
		final File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (final File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	public static boolean isZeroFloat(float value, float threshold){
	    return value >= -threshold && value <= threshold;
	}
	
	public static boolean isZero(double value, double threshold){
	    return value >= -threshold && value <= threshold;
	}

	public static int getDX() {
		int value = dX;
		dX = 0;
		return value;
	}

	public static int getDY() {
		int value = dY;
		dY = 0;
		return value;
	}

	public static void transformStringTranslate() throws Exception {
		/* Get Field in Obfuscated or Non-Obfuscated Environment */
		Field translateTableField;
		try {
			Class<?> clazz = Class.forName("net.minecraft.src.StringTranslate");
			/* Non-Obfuscated net.minecraft.src.StringTranslate.translateTable */
			System.err.println("Non-Obfuscated Environment detected!");
			translateTableField = clazz.getDeclaredField("translateTable");
		} catch (Exception e) {
			/* Obfuscated: qp.b */
			Class<?> clazz = Class.forName("qp");
			System.err.println("Obfuscated Environment detected!");
			translateTableField = clazz.getDeclaredField("b");
		}
		translateTableField.setAccessible(true);
		/* Replace Translations for custom stuff */
		Properties translations = (Properties) translateTableField.get(StringTranslate.getInstance());
		translations.setProperty("selectWorld.create", "Create and record World");
		translations.setProperty("menu.singleplayer", "Record TAS");
		translations.setProperty("menu.multiplayer", "Playback TAS");
		translations.setProperty("multiplayer.title", "Playback or Edit TAS");
		translations.setProperty("selectServer.select", "Play");
		translations.setProperty("selectServer.direct", "Connect");
		translations.setProperty("selectServer.add", "Download");
		translations.setProperty("selectServer.select", "Play");
		/* Reopen Gui Screen with new Translations */
		TASmod.mc.displayGuiScreen(TASmod.mc.currentScreen);
	}

	public static Minecraft obtainMinecraftInstance() throws Exception {
		/* Get Field in Obfuscated or Non-Obfuscated Environment */
		final Class<?> clazz = Class.forName("net.minecraft.client.Minecraft");
		Field theMinecraftField;
		try {
			theMinecraftField = clazz.getDeclaredField("theMinecraft");
		} catch (Exception e) {
			theMinecraftField = clazz.getDeclaredField("a");
		}
		theMinecraftField.setAccessible(true);
		return (Minecraft) theMinecraftField.get(null);
	}
	
}
