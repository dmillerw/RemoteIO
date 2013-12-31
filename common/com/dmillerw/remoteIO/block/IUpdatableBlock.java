package com.dmillerw.remoteIO.block;

import net.minecraft.world.World;

public interface IUpdatableBlock {

	public void onBlockUpdate(World world, int x, int y, int z, int causeID, int causeMeta);

}
