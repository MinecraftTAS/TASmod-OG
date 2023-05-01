package com.minecrafttas.tasmodog.virtual;

public class VirtualCamera {

	// pitch,yaw of fake camera
	public static float rotationPitch, rotationYaw;
	// prev pitch,yaw of fake camera
	public static float prevRotationPitch, prevRotationYaw;
	// tick based x,y mouse coordinates
	public static int lastX, lastY;

	/**
	 * Update virtual camera rotation
	 * @author Mojang Studios
	 */
	public static void setFakeAngles(final float f, final float f1) {
		final float f2 = rotationPitch;
		final float f3 = rotationYaw;
		rotationYaw += f * 0.14999999999999999D;
		rotationPitch -= f1 * 0.14999999999999999D;
		if(rotationPitch < -90F)
			rotationPitch = -90F;
		if(rotationPitch > 90F)
			rotationPitch = 90F;
		prevRotationPitch += rotationPitch - f2;
		prevRotationYaw += rotationYaw - f3;
	}
	
}
