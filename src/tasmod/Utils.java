package net.tasmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

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

	public static void emptyFile(File file) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(file);
		writer.println("");
		writer.close();
	}
	
	public static void changeField(String clazz, String name, Random rng, boolean isFinal) {
		try {
			Field field = Class.forName(clazz).getDeclaredField(name);			
			field.setAccessible(true);
			if (isFinal) {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			}
			field.set(null, rng);
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException
				| IllegalAccessException e) {
			e.printStackTrace();
			System.err.println("\n\nCouldn't hack " + clazz + ":" + name + " #7\n\n");
			System.exit(0);
		}
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
	
}
