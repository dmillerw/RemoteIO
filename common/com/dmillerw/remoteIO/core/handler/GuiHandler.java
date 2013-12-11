package com.dmillerw.remoteIO.core.handler;

import com.dmillerw.remoteIO.block.tile.TileIO;
import com.dmillerw.remoteIO.inventory.ContainerIOUpgrade;
import com.dmillerw.remoteIO.inventory.gui.GuiIOUpgrade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		case 0: return new ContainerIOUpgrade(player, (TileIO) world.getBlockTileEntity(x, y, z));
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		case 0: return new GuiIOUpgrade(player, (TileIO) world.getBlockTileEntity(x, y, z));
		}
		
		return null;
	}

}
