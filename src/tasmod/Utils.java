package net.tasmod;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.client.Minecraft;
import net.minecraft.src.StringTranslate;
import net.tasmod.random.SimpleRandomMod;
import net.tasmod.virtual.VirtualKeyboard;
import net.tasmod.virtual.VirtualMouse;

/**
 * Class that contains useful stuff
 * @author Pancake
 */
public final class Utils {

	/** X Position that the mouse moved in-between Ticks */
	public static int dX;
	/** Y Position that the mouse moved in-between Ticks */
	public static int dY;
	/* X Position that the mouse is currently per Tick */
	public static int lastX;
	/* Y Position that the mouse is currently per Tick */
	public static int lastY;
	
	/** Pitch of the Fake Camera */
	public static float rotationPitch = 0f;
	/** Yaw of the Fake Camera */
	public static float rotationYaw = 0f;
	/** Previous Pitch of the Fake Camera */
	public static float prevRotationPitch = 0f;
	/** Previous Yaw of the Fake Camera */
	public static float prevRotationYaw = 0f;
	
	/**
	 * Method used to update Yaw and Pitch using Mouse Coordinates.
	 * Used for Fake Camera
	 * @author Mojang Studios
	 */
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
	
    /**
     * Small Utility that deletes a Directory recursively
     */
	public static final boolean deleteDirectory(final File directoryToBeDeleted) {
		final File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (final File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	/**
	 * Utility to see if a float is near zero
	 */
	public static boolean isZeroFloat(float value, float threshold){
	    return value >= -threshold && value <= threshold;
	}
	
	/**
	 * Utility to see if a double is near zero
	 */
	public static boolean isZero(double value, double threshold){
	    return value >= -threshold && value <= threshold;
	}

	/**
	 * Used by VirtualMouse, to get the dX for one tick.
	 * The Value dX is being added over frames
	 */
	public static int getDX() {
		int value = dX;
		dX = 0;
		return value;
	}

	/**
	 * Used by VirtualMouse, to get the dY for one tick.
	 * The Value dY is being added over frames
	 */
	public static int getDY() {
		int value = dY;
		dY = 0;
		return value;
	}

	/**
	 * Changes the Name of a few Buttons to match their use without editing Minecraft Code
	 */
	public static void transformStringTranslate() throws Exception {
		/* Get Field in Obfuscated or Non-Obfuscated Environment */
		Field translateTableField;
		try {
			/* Non-Obfuscated net.minecraft.src.StringTranslate.translateTable */
			System.err.println("Non-Obfuscated Environment detected!");
			translateTableField = Class.forName("net.minecraft.src.StringTranslate").getDeclaredField("translateTable");
		} catch (Exception e) {
			/* Obfuscated: qp.b */
			System.err.println("Obfuscated Environment detected!");
			translateTableField = Class.forName("qp").getDeclaredField("b");
		}
		translateTableField.setAccessible(true);
		/* Replace Translations for custom stuff */
		final Properties translations = (Properties) translateTableField.get(StringTranslate.getInstance());
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
	

	/**
	 * Transforms the Random Variable for Math.random()
	 */
	public static void transformRandom() throws Exception {
		/* Get Fields for the Random Value used in Math */
		final Field mathRandomField = Class.forName("java.lang.Math$RandomNumberGeneratorHolder").getDeclaredField("randomNumberGenerator");
		mathRandomField.setAccessible(true);
		/* Remove Final */
		Field modifiersField = Field.class.getDeclaredField("modifiers");
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
		} catch (Exception e) {
			theMinecraftField = clazz.getDeclaredField("a");
		}
		theMinecraftField.setAccessible(true);
		return (Minecraft) theMinecraftField.get(null);
	}
	
	/**
	 * Tool that double checks Position and Rotation of the Player, to see if the Playback has desynced
	 */
	public static void checkDesync(Minecraft mc, String line) {
		double desyncPosX = mc.thePlayer.posX - posX;
    	double desyncPosY = mc.thePlayer.posY - posY;
    	double desyncPosZ = mc.thePlayer.posZ - posZ;
    	double desyncMotionX = mc.thePlayer.motionX - motionX;
    	double desyncMotionY = mc.thePlayer.motionY - motionY;
    	double desyncMotionZ = mc.thePlayer.motionZ - motionZ;
    	float desyncYaw = (mc.thePlayer.rotationYaw % 360) - yaw;
    	float desyncPitch = (mc.thePlayer.rotationPitch % 360) - pitch;
    	desync = "";
    	desync_2 = "";
    	desync_3 = "";
    	if (!Utils.isZero(desyncPosX, 0.00001D)) desync += "X: " + String.format("%.5f", desyncPosX) + " ";
    	if (!Utils.isZero(desyncPosY, 0.00001D)) desync += "Y: " + String.format("%.5f", desyncPosY) + " ";
    	if (!Utils.isZero(desyncPosZ, 0.00001D)) desync += "Z: " + String.format("%.5f", desyncPosZ) + " ";
    	if (!Utils.isZero(desyncMotionX, 0.00001D)) desync_2 += "mX: " + String.format("%.5f", desyncMotionX) + " ";
    	if (!Utils.isZero(desyncMotionY, 0.00001D)) desync_2 += "mY: " + String.format("%.5f", desyncMotionY) + " ";
    	if (!Utils.isZero(desyncMotionZ, 0.00001D)) desync_2 += "mZ: " + String.format("%.5f", desyncMotionZ) + " ";
    	if (!Utils.isZeroFloat(desyncYaw, 0.00001F)) desync_3 += "Yaw: " + String.format("%.5f", desyncYaw) + " ";
    	if (!Utils.isZeroFloat(desyncPitch, 0.00001F)) desync_3 += "Pitch: " + String.format("%.5f", desyncPitch) + " ";
		if (line == null) return;
		final String[] strings = line.split(":");
		if (!strings[0].isEmpty()) posX = Double.parseDouble(strings[0]);
		else posX = 0.0D;
		if (!strings[1].isEmpty()) posY = Double.parseDouble(strings[1]);
		else posY = 0.0D;
		if (!strings[2].isEmpty()) posZ = Double.parseDouble(strings[2]);
		else posZ = 0.0D;
		if (!strings[3].isEmpty()) motionX = Double.parseDouble(strings[3]);
		else motionX = 0.0D;
		if (!strings[4].isEmpty()) motionY = Double.parseDouble(strings[4]);
		else motionY = 0.0D;
		if (!strings[5].isEmpty()) motionZ = Double.parseDouble(strings[5]);
		else motionZ = 0.0D;
		if (!strings[6].isEmpty()) pitch = Float.parseFloat(strings[6]);
		else pitch = 0.0F;
		if (!strings[7].isEmpty()) yaw = Float.parseFloat(strings[7]);
		else yaw = 0.0F;
		
		if (!desync.isEmpty()) mc.ingameGUI.addChatMessage(desync);
		if (!desync_2.isEmpty()) mc.ingameGUI.addChatMessage(desync_2);
		if (!desync_3.isEmpty()) mc.ingameGUI.addChatMessage(desync_3);
	}
	
	private static double posX;
	private static double posY;
	private static double posZ;
	private static double motionX;
	private static double motionY;
	private static double motionZ;
	private static float yaw;
	private static float pitch;
	private static String desync;
	private static String desync_2;
	private static String desync_3;

	public static void lazyMouse() {
		if (VirtualMouse.getEventButton() == 0) VirtualMouse.isButton0Down = VirtualMouse.getEventButtonState();
	}

	public static void lazyKeyboard() {
		if (VirtualKeyboard.getEventKey() == 37) VirtualKeyboard.isKey37Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 42) VirtualKeyboard.isKey42Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 54) VirtualKeyboard.isKey54Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 60) VirtualKeyboard.isKey60Down = VirtualKeyboard.getEventKeyState();
		if (VirtualKeyboard.getEventKey() == 61) VirtualKeyboard.isKey61Down = VirtualKeyboard.getEventKeyState();
	}
    
}
