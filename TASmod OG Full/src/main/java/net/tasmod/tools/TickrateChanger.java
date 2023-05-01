package net.tasmod.tools;

import net.tasmod.TASmod;

/**
 * The TickrateChanger is the part of the mod, that slows down the game to allow for more precise inputs.
 * @author Pancake
 */
public class TickrateChanger {



	/** Whether the game is currently in tick advance mode */
	public static boolean isTickAdvance;
	/** Selected index from array below */
	public static int selectedGamespeed = 6;
	/** Array of all available gamespeeds */
	public static float[] availableGamespeeds = new float[] {
			0.025f, // 0
			0.05f, // 1
			0.1f, // 2
			0.2f, // 3
			0.4f, // 4
			0.5f, // 5
			1f, // 6
			2.0f // 7
	};

	public static long timeOffset = 0L;
	public static long timeSinceZero = System.currentTimeMillis();
	public static long timeSinceTC = System.currentTimeMillis();
	public static long fakeTimeSinceTC = System.currentTimeMillis();
	public static boolean shouldOverrideRenderTime; // overrides the milliseconds
	public static long overrideRenderTime;
	
	public static long getMilliseconds() {
		if (shouldOverrideRenderTime) {
			return overrideRenderTime;
		}
		long time = System.currentTimeMillis() - timeSinceTC - timeOffset;
		time *= availableGamespeeds[selectedGamespeed] * 20.0f / 20F;
		return fakeTimeSinceTC + time;
	}

	/**
	 * Increases the gamespeed by one (see {@link #availableGamespeeds}).
	 */
	public static void faster() {
		if (selectedGamespeed != availableGamespeeds.length - 1) selectedGamespeed++;
		try {
			updateTickrate(availableGamespeeds[selectedGamespeed]);
		} catch (final Exception e) {
			System.err.println("Error changing gamespeed...");
			e.printStackTrace();
		}
	}

	/**
	 * Decreases the gamespeed by one (see {@link #availableGamespeeds}).
	 */
	public static void slower() {
		if (selectedGamespeed != 0) selectedGamespeed--;
		try {
			updateTickrate(availableGamespeeds[selectedGamespeed]);
		} catch (final Exception e) {
			System.err.println("Error changing gamespeed...");
			e.printStackTrace();
		}
	}

	/**
	 * Enters or leaves tick advance
	 */
	public static void toggleTickadvance() {
		if (!(isTickAdvance = !isTickAdvance))
			updateTickrate(availableGamespeeds[selectedGamespeed]);
		else
			updateTickrate(0f);
	}

	/**
	 * This method is used to slow down the game. It replaces the 'timerSpeed' field in the Timer class.
	 * @param gamespeed New Speed of game (1.0 = normal speed)
	 */
	public static void updateTickrate(final float gamespeed) {
		if (gamespeed <= 0.02F) timeSinceZero = System.currentTimeMillis() - timeOffset;

		final long time = System.currentTimeMillis() - timeSinceTC - timeOffset;
		fakeTimeSinceTC += (long) (time * (gamespeed * 20.0f / 20F));
		timeSinceTC = System.currentTimeMillis() - timeOffset;
		TASmod.mc.timer.timerSpeed = gamespeed;
	}

}
