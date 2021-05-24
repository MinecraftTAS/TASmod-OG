package net.tasmod.rng;

import java.util.Random;

public class FakeRandom extends Random {

	public static long seed = 0L;
	
	@Override
	public synchronized void setSeed(long seed) {
		System.out.println("Successfully messed up!");
		throw new RuntimeException();
	}
	
	@Override
	public boolean nextBoolean() { return new Random(seed).nextBoolean(); }
	
	@Override
	public double nextDouble() { return new Random(seed).nextDouble(); }
	
	@Override
	public float nextFloat() { return new Random(seed).nextFloat(); }
	
	@Override
	public int nextInt() { return new Random(seed).nextInt(); }
	
	@Override
	public int nextInt(int bound) { return new Random(seed).nextInt(bound); }
	
	@Override
	public long nextLong() { return new Random(seed).nextLong(); }
	
	@Override
	public synchronized double nextGaussian() { return new Random(seed).nextGaussian(); } 
	
	
	
	/* List of all RNG Sources, that have to be changed */
	
	/*
	BlockBrewingStand.field_40213_a, BlockChest.random, BlockDispenser.random, BlockFurnace.furnaceRand
	Used in: onBlockRemoval():
		- Position of all Items inside of the Container
		- Amount of Items that should be dropped at once
		- Motion of dropped Items, inside of the Container
	*/
	
	/*
	ContainerEnchantment.field_40237_l
	Used in onCraftMatrixChanged():
		- Generates the Level of Enchants in the Enchanting Table
	Used in func_40233_a():
		- Enchants an Item with the Enchants
	*/
	
	/*
	EnchantmentHelper.enchantmentRand
	Used in getEnchantmentModifierDamage() and getEnchantmentModifierLiving():
		- Increases Damage by a random value (?)
	*/
	
	/*
	Entity.rand
	Used everywhere. 
	The good thing is, it is not being setSeed'ed, which means that this can be changed per tick
	*/
	
	/*
	Explosion.ExplosionRNG
	Used in doExplosionB():
		- Rolls, whether fire should be placed on a block
	*/

	/*
	Item.itemRand
	Used in ItemDye.onItemUse():
		- Rolls random World Update (?)
	*/
	
	/*
	Teleporter.random
	Used in createPortal():
		- Direction of the Portal (?)
	*/
	
	/*
	TileEntityDispenser.dispenserRandom
		- Rolls Item Slot to throw out
	*/
	
	/*
	WorldGenBigTree.rand
		- Helps out the Big Tree Generation
	 */
	
	/*
	World.rand
	Used in a lot of places, it is not being setSeed'ed either!
	*/
	
	/*
	GuiEnchantment.field_40230_x
	    - I don't know ._.
	*/
	
	/*
	World.randomDisplayUpdates.random
		- I don't know ._:
	 */
	
}
