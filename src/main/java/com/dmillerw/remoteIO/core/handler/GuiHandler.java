package com.dmillerw.remoteIO.core.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.dmillerw.remoteIO.block.tile.TileIO;
import com.dmillerw.remoteIO.block.tile.TileRemoteInventory;
import com.dmillerw.remoteIO.block.tile.TileTurtleBridge;
import com.dmillerw.remoteIO.inventory.ContainerDocumentation;
import com.dmillerw.remoteIO.inventory.ContainerUpgrade;
import com.dmillerw.remoteIO.inventory.gui.GuiDocumentation;
import com.dmillerw.remoteIO.inventory.gui.GuiUpgrade;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		case 0: {
		    TileIO tile = (TileIO) world.getBlockTileEntity(x, y, z);
		    return new ContainerUpgrade(player, tile.upgrades, tile.camo, 0);
		}
		case 1: {
		    TileRemoteInventory tile = (TileRemoteInventory) world.getBlockTileEntity(x, y, z);
		    return new ContainerUpgrade(player, tile.upgrades, tile.camo, 1);
		}
        case 2: return new ContainerDocumentation(player);
        case 3: {
            TileTurtleBridge tile = (TileTurtleBridge) world.getBlockTileEntity(x, y, z);
            return new ContainerUpgrade(player, tile.upgrades, tile.camo, 2);
        }
        }
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
	    case 0: {
            TileIO tile = (TileIO) world.getBlockTileEntity(x, y, z);
            return new GuiUpgrade(player, tile.upgrades, tile.camo, 0, "gui.upgrade.io");
        }
        case 1: {
            TileRemoteInventory tile = (TileRemoteInventory) world.getBlockTileEntity(x, y, z);
            return new GuiUpgrade(player, tile.upgrades, tile.camo, 1, "gui.upgrade.remote");
        }
        case 2: return new GuiDocumentation(player);
        case 3: {
            TileTurtleBridge tile = (TileTurtleBridge) world.getBlockTileEntity(x, y, z);
            return new GuiUpgrade(player, tile.upgrades, tile.camo, 2, "gui.upgrade.turtle");
        }
        }
		
		return null;
	}

}
