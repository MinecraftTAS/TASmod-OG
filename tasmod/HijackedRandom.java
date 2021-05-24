package net.tasmod;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This Random forces a Seed for all 'Random' Operations and logs them into a file.
 * @author Pancake
 */
public class HijackedRandom extends Random {
	
	public static final AtomicLong seed = new AtomicLong(0L);
	
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;
	
    /* Redirect internal seed to static seed */
    
    public HijackedRandom(long seed) {
    	System.err.println(Thread.currentThread().getStackTrace()[5].getFileName());
    }
    
	public HijackedRandom() {
		
	}

	@Override
    synchronized public void setSeed(long seed) {
		
    }
	
	@Override
	public synchronized double nextGaussian() {
		return nextDouble(); // Gaussian is nextDouble but biased
	}
	
    @Override
    protected int next(int bits) {
        long nextseed = (HijackedRandom.seed.get() * multiplier + addend) & mask;
        return (int)(nextseed >>> (48 - bits));
    }
    
}
