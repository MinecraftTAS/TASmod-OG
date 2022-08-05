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

	/**
	 * Method that changes the Seed and updates all the next Calls
	 */
	public static void updateSeed(final long newSeed) {
		seed = newSeed;
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

	public static int INSN;
	
	static {
		updateSeed(0L);
	}

	@Override public boolean nextBoolean() { 
		System.out.println(INSN);
		return _nextBoolean(INSN); }
	
	private boolean _nextBoolean(int i) {
		INSN = 0;
		if (i % 2 == 0)
			return nextBoolean1;
		return nextBoolean;
	}
	
	@Override public int nextInt() { return _nextInt(INSN); }
	@Override public int nextInt(final int bound) { return Math.floorMod(_nextInt(INSN), bound); }
	
	private int _nextInt(int i) {
		INSN = 0;
		if (i % 2 == 0)
			return nextInt1;
		return nextInt;
	}
	
	@Override public double nextDouble() { return _nextDouble(INSN); }
	
	private double _nextDouble(int i) {
		INSN = 0;
		if (i % 2 == 0)
			return nextDouble1;
		return nextDouble;
	}
	
	@Override public float nextFloat() { return _nextFloat(INSN); }
	
	private float _nextFloat(int i) {
		INSN = 0;
		if (i % 2 == 0)
			return nextFloat1;
		return nextFloat;
	}
	
	@Override public double nextGaussian() { return _nextGaussian(INSN); }
	
	private double _nextGaussian(int i) {
		INSN = 0;
		if (i % 2 == 0)
			return nextGaussian1;
		return nextGaussian;
	}
	
	@Override public long nextLong() { return _nextLong(INSN); }
	
	private long _nextLong(int i) {
		INSN = 0;
		if (i % 2 == 0)
			return nextLong1;
		return nextLong;
	}

}