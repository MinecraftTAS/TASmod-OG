package com.minecrafttas.tasmodog.tools;

import java.util.Random;

public class KillTheRng {
	
	private Random rng;
	
	/**
	 * Initialize kill the rng
	 */
	public KillTheRng() {
		this.rng = new Random();
	}
	
	/**
	 * Regenerate random values of game
	 * @param seed Seed to use
	 */
	public void regenerateRandom(long seed) {
		this.rng.setSeed(seed);
		
		for (int i = 0; i < 200; i++)
			KillTheRngOccurences.nextInt[i] = Math.abs(this.rng.nextInt());
		
		for (int i = 0; i < 91; i++)
			KillTheRngOccurences.nextFloat[i] = this.rng.nextFloat();
		
		for (int i = 0; i < 38; i++)
			KillTheRngOccurences.nextGaussian[i] = this.rng.nextGaussian();
		
		for (int i = 0; i < 15; i++)
			KillTheRngOccurences.nextDouble[i] = this.rng.nextDouble();
		
		for (int i = 0; i < 1; i++)
			KillTheRngOccurences.nextLong[i] = this.rng.nextLong();
		
		for (int i = 0; i < 2; i++)
			KillTheRngOccurences.nextBoolean[i] = this.rng.nextBoolean();
		
		for (int i = 0; i < 14; i++)
			KillTheRngOccurences.random[i] = this.rng.nextDouble();
	}
	
}
