package com.minecrafttas.tasmodog.tools;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;

/**
 * Tickrate Changer mod
 * @author Pancake
 */
public class TickrateChanger {
	
	private static final float[] GAMESPEEDS = { 0.025f, 0.05f, 0.1f, 0.2f, 0.4f, 0.5f, 1f, 2.0f };

	private int gamespeedIndex = 6;
	private boolean advanceTick;
	
	private long systemTimeSinceTC = System.currentTimeMillis(); // system time passed since last tickrate change
	private long gameTime = System.currentTimeMillis(); // game time passed (since last tickrate update)
	
	private Minecraft mc;
	
	/**
	 * Initialize Tickrate Changer
	 */
	public TickrateChanger() {
		
	}
	
	/**
	 * Post Initialize Tickrate Changer with Minecraft instance
	 * @param mc Minecraft instance
	 */
	public void init(Minecraft mc) {
		this.mc = mc;
		this.toggleTickadvance();
	}
	
	/**
	 * Tick Tickrate Changer
	 */
	public void tick() {
		if (this.advanceTick) {
			this.advanceTick = false;
			this.toggleTickadvance();
		}
	}
	
	/**
	 * Render loop Tickrate Changer
	 */
	public void render() {
		// don't take inputs during tick advance
		if (this.advanceTick)
			return;
		
		// increase gamespeed keybind
		if (KeyboardHelper.isKeyPress(Keyboard.KEY_PERIOD))
			this.increaseGamespeed();
		
		// decrease gamespeed keybind
		if (KeyboardHelper.isKeyPress(Keyboard.KEY_COMMA))
			this.decreaseGamespeed();
		
		// toggle tick advance keybind
		if (KeyboardHelper.isKeyPress(Keyboard.KEY_F8))
			this.toggleTickadvance();
		
		// advance tick keybind
		if (KeyboardHelper.isKeyPress(Keyboard.KEY_F9) && this.mc.timer.timerSpeed <= 0.02f)
			this.advanceTick();
	}
	
	/**
	 * Advances a tick during tickadvance
	 */
	public void advanceTick() {
		this.toggleTickadvance();
		this.advanceTick = true;
	}

	/**
	 * Returns the amount of milliseconds passed without including the tickrate changing
	 * @return Milliseconds
	 */
	public long currentTimeMillis() {
		return (long) (this.gameTime + ((System.currentTimeMillis() - this.systemTimeSinceTC) * this.mc.timer.timerSpeed));
	}

	/**
	 * Increases the game speed to the next available gamespeed
	 */
	public void increaseGamespeed() {
		if (this.gamespeedIndex != GAMESPEEDS.length - 1) 
			this.gamespeedIndex++;

		this.updateGamespeed(GAMESPEEDS[this.gamespeedIndex]);
	}

	/**
	 * Decreases the game speed to the next available gamespeed
	 */
	public void decreaseGamespeed() {
		if (this.gamespeedIndex != 0) 
			this.gamespeedIndex--;
		
		this.updateGamespeed(GAMESPEEDS[gamespeedIndex]);
	}

	/**
	 * Toggle tick advance state
	 */
	public void toggleTickadvance() {
		if (this.mc.timer.timerSpeed <= 0.02f)
			this.updateGamespeed(GAMESPEEDS[this.gamespeedIndex]);
		else
			this.updateGamespeed(0f);
	}

	/**
	 * Update the game speed
	 * @param gamespeed Game speed
	 */
	public void updateGamespeed(float gamespeed) {
		long millis = System.currentTimeMillis();

		// calculate time passed without tickrate changing
		long timePassed = millis - this.systemTimeSinceTC;
		this.gameTime += (long) (timePassed * this.mc.timer.timerSpeed);
		this.systemTimeSinceTC = millis;
		
		// update game speed
		this.mc.timer.timerSpeed = gamespeed;
	}

}
