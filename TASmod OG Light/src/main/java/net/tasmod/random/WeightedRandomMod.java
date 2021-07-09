package net.tasmod.random;

import java.util.Random;

/**
 * Hijacked Random, that uses a global counter
 * @author Pancake
 */
public class WeightedRandomMod extends Random {
	
	private static final long serialVersionUID = -2110828922074304230L;
	public static int intCalls = 0;
	
	@Override public boolean nextBoolean() { throw new RuntimeException("Method does not exist"); }
	@Override public int nextInt() { throw new RuntimeException("Method does not exist"); }
	@Override public int nextInt(int bound) { return Math.floorMod(intCalls++, bound); }
	@Override public double nextDouble() { throw new RuntimeException("Method does not exist"); }
	@Override public float nextFloat() { throw new RuntimeException("Method does not exist"); }
	@Override public double nextGaussian() { throw new RuntimeException("Method does not exist"); }
	@Override public long nextLong() { throw new RuntimeException("Method does not exist"); }
	
}
