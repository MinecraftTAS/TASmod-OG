package com.minecrafttas.tasmodog.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.minecrafttas.tasmodog.TASmod;
import com.minecrafttas.tasmodog.tools.KeyboardHelper;

/**
 * Input container containing inputs and other tasmod data for all ticks
 * @author Pancake
 */
public class InputContainer {
	public enum State { NONE, PLAYBACK, RECORDING }
	
	private TASmod tasmod;
	private List<Tick> ticks;
	private Tick currentTick;
	private int nextTick;
	private State state;
	private boolean shouldStartRecording;
	
	/**
	 * Initialize Input container
	 */
	public InputContainer() {
		this.ticks = new ArrayList<>();
		this.currentTick = new Tick();
		this.nextTick = 0;
		this.state = State.NONE;
	}
	
	/**
	 * Post Initialize Input container with TASmod instance
	 * @param tasmod TASmod instance
	 */
	public void init(TASmod tasmod) {
		this.tasmod = tasmod;
	}
	
	/**
	 * Tick Input container
	 */
	public void tick() {
		// ignore when not active
		if (this.state == State.NONE) {
			this.currentTick = new Tick();
			return;
		}
		
		// add tick if recording
		if (this.state == State.RECORDING)
			if (this.nextTick >= this.ticks.size())
				this.ticks.add(this.currentTick);
			else
				this.ticks.set(this.nextTick, this.currentTick);

		// start recording if requested
		if (this.shouldStartRecording) {
			this.shouldStartRecording = false;
			
			this.tasmod.getTickrateChanger().updateGamespeed(0f);
			this.tasmod.getMinecraftWindow().enableSaveButton();
			
			this.state = State.RECORDING;
			
			System.out.println("Re-recording from tick " + this.nextTick);
		}
		
		// load next tick
		this.currentTick = (this.nextTick >= this.ticks.size() || this.state == State.RECORDING) ? new Tick() : this.ticks.get(this.nextTick).clone();
		this.nextTick++;
	}
	
	public void render() {
		// ignore when not active
		if (this.state == State.NONE)
			return;
		
		// switch to recording keybind
		if (KeyboardHelper.isKeyPress(Keyboard.KEY_F12) && this.state == State.PLAYBACK)
			this.shouldStartRecording = true;
	}
	
	/**
	 * Save tick data to file
	 * @param file File
	 * @throws Exception Filesystem exception
	 */
	public void save(File file) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(this.ticks);
		oos.close();
		
		System.out.println(String.format("Saved %d ticks", this.ticks.size()));
	}
	
	/**
	 * Load tick data from file
	 * @param file File
	 * @throws Exception Filesystem exception
	 */
	@SuppressWarnings("unchecked")
	public void load(File file) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		this.ticks = (List<Tick>) ois.readObject();
		ois.close();
		
		System.out.println(String.format("Loaded %d ticks", this.ticks.size()));
	}
	
	/**
	 * Updates container state
	 * @param newState State
	 */
	public void updateState(State newState) {
		this.state = newState;
	}
	
	/**
	 * Return current state
	 * @return Current state
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * Return current tick data
	 * @return Current tick data
	 */
	public Tick getCurrentTickData() {
		return this.currentTick;
	}

}
