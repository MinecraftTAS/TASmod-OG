package com.minecrafttas.tasmodog.tools;

/**
 * Virtual frame based camera
 */
public class FakeCamera {

	// pitch,yaw of fake camera
	public static float yaw, pitch;
	// prev pitch,yaw of fake camera
	public static float prevPitch, prevYaw;

	/**
	 * Update virtual camera rotation
	 * @param dx Mouse delta x with sensitivity applied
	 * @param dy Mouse delta y with sensitivity applied
	 */
	public static void setFakeAngles(float dx, float dy) {
		float newPrevYaw = yaw;
		float newPrevPitch = pitch;
		
		yaw += dx * 0.15;
		pitch -= Math.max(-90.0f, Math.min(90.0f, dy * 0.15));
		
		prevYaw += yaw - newPrevYaw;
		prevPitch += pitch - newPrevPitch;
	}
	
}
