package com.minecrafttas.tasmodog.container;

import com.minecrafttas.tasmodog.TASmod;

public interface Container {

	public void init(TASmod tasmod);
	public void tick() throws Exception;
	
}
