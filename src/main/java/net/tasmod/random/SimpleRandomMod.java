package net.tasmod.random;

import java.util.Random;

import net.minecraft.src.MathHelper;
import net.tasmod.Utils;

/**
 * Hijacked Random, that uses a global seed
 * 
 * THIS FILE IS NOT IN THE FINAL VERSION AND IS JUST FOR TESTING
 * TODO, RECORD THE SEED AND MAKE IT RANDOM PER ENTITY
 * @author Pancake
 */
public class SimpleRandomMod extends Random {
	
	public static long seed = 0L;
	
	/**
	 * Method that changes the Seed and updates all the next Calls
	 */
	public static final void updateSeed(final long newSeed) {
		seed = newSeed;
		finalRandom.setSeed(newSeed);
		nextBoolean = new Random(seed).nextBoolean();
		nextInt = new Random(seed).nextInt();
		nextDouble = new Random(seed).nextDouble();
		nextFloat = new Random(seed).nextFloat();
		nextGaussian = new Random(seed).nextGaussian();
		nextLong = new Random(seed).nextLong();
	}
	
	/* Override all Random Calls used by Minecraft Code to custom ones */
	
	public static Random finalRandom = new Random(seed);
	public static boolean nextBoolean;
	public static int nextInt;
	public static double nextDouble;
	public static float nextFloat;
	public static double nextGaussian;
	public static long nextLong;
	
	static {
		updateSeed(0L);
	}
	
	@Override public boolean nextBoolean() { return nextBoolean; }
	@Override public int nextInt() { return nextInt; }
	@Override public int nextInt(int bound) { return Math.floorMod(nextInt, bound); }
	@Override public double nextDouble() { return nextDouble; }
	@Override public float nextFloat() { return nextFloat; }
	@Override public double nextGaussian() { return nextGaussian; }
	@Override public long nextLong() { return nextLong; }
	
}
