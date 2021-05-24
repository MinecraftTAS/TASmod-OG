package net.tasmod.rng;

import java.util.Random;

public class DecoratorRng extends Random {

	@Override
	public int nextInt() {
		System.err.println(Thread.currentThread().getName());
		return super.nextInt();
	}
	
}
