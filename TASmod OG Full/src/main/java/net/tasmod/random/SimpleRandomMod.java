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

	private static final long serialVersionUID = -7004011273350186876L;
	public static long seed = 0L;
	public static SimpleRandomMod singleton = new SimpleRandomMod();
	
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
		
		nextBoolean1 = new Random(seed+1).nextBoolean();
		nextInt1 = new Random(seed+1).nextInt();
		nextDouble1 = new Random(seed+1).nextDouble();
		nextFloat1 = new Random(seed+1).nextFloat();
		nextGaussian1 = new Random(seed+1).nextGaussian();
		nextLong1 = new Random(seed+1).nextLong();
	}

	/* Override all Random Calls used by Minecraft Code to custom ones */

	public static Random finalRandom = new Random(seed);
	public static boolean nextBoolean;
	public static int nextInt;
	public static double nextDouble;
	public static float nextFloat;
	public static double nextGaussian;
	public static long nextLong;
	public static boolean nextBoolean1;
	public static int nextInt1;
	public static double nextDouble1;
	public static float nextFloat1;
	public static double nextGaussian1;
	public static long nextLong1;
	
	static {
		updateSeed(0L);
	}
	
	public static int lastBound;
	public static int INSN;
	
	@Override public boolean nextBoolean() { return nextBoolean; }
	@Override public int nextInt() { return nextInt; }
	@Override public int nextInt(final int bound) { return Math.floorMod(nextInt, lastBound = bound); }
	@Override public double nextDouble() { INSN++; return _nextDouble(); }
	@Override public float nextFloat() { return nextFloat; }
	@Override public double nextGaussian() { return nextGaussian; }
	@Override public long nextLong() { return nextLong; }
	
	public static int _nextInt0() {
		return Math.floorMod(_nextInt(INSN), lastBound);
	}
	
	public static boolean _nextBoolean() { 
		return _nextBoolean(INSN);
	}
	
	public static int _nextInt() { 
		return _nextInt(INSN);
	}
	
	public static double _nextDouble() {
		return _nextDouble(INSN);
	}
	
	public static float _nextFloat() {
		return _nextFloat(INSN);
	}
	
	public static double _nextGaussian() {
		return _nextGaussian(INSN); 
	}
	
	public static long _nextLong() {
		return _nextLong(INSN); 
	}
	
	private static boolean _nextBoolean(int i) {
		if (i % 2 == 0)
			return nextBoolean1;
		return nextBoolean;
	}
	
	private static int _nextInt(int i) {
		if (i % 2 == 0)
			return nextInt1;
		return nextInt;
	}
	
	private static double _nextDouble(int i) {
		if (i % 2 == 0)
			return nextDouble1;
		return nextDouble;
	}
	
	private static float _nextFloat(int i) {
		if (i % 2 == 0)
			return nextFloat1;
		return nextFloat;
	}
	
	private static double _nextGaussian(int i) {
		if (i % 2 == 0)
			return nextGaussian1;
		return nextGaussian;
	}
	
	private static long _nextLong(int i) {
		if (i % 2 == 0)
			return nextLong1;
		return nextLong;
	}
	
}
