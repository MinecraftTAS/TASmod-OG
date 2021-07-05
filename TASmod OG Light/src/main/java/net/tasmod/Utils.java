package net.tasmod;

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

	public static boolean isObfuscated;

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
			isObfuscated = true;
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
	}
    
}
