package net.tasmod.random;

import java.util.Random;

/**
 * Hijacked Random, that uses a global seed
 * 
 * THIS FILE IS NOT IN THE FINAL VERSION AND IS JUST FOR TESTING
 * TODO, RECORD THE SEED AND MAKE IT RANDOM PER ENTITY
 * @author Pancake
 */
public class SimpleRandomMod extends Random {

	public static long seed = 0L;
	
	/* Override all Random Calls used by Minecraft Code to custom ones */
	
	@Override public boolean nextBoolean() { return new Random(seed).nextBoolean(); }
	@Override public int nextInt() { return new Random(seed).nextInt(); }
	@Override public int nextInt(int bound) { return new Random(seed).nextInt(bound); }
	@Override public double nextDouble() { return new Random(seed).nextDouble(); }
	@Override public float nextFloat() { return new Random(seed).nextFloat(); }
	@Override public double nextGaussian() { return new Random(seed).nextGaussian(); }
	@Override public long nextLong() { return new Random(seed).nextLong(); }
	
}
