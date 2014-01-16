package com.dmillerw.remoteIO.core.handler;

import com.dmillerw.remoteIO.block.tile.TileIOCore;
import com.dmillerw.remoteIO.inventory.ContainerDocumentation;
import com.dmillerw.remoteIO.inventory.ContainerUpgrade;
import com.dmillerw.remoteIO.inventory.gui.GuiDocumentation;
import com.dmillerw.remoteIO.inventory.gui.GuiUpgrade;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileIOCore tile = (TileIOCore) world.getBlockTileEntity(x, y, z);
        
        switch(ID) {
		case 0: {
		    return new ContainerUpgrade(player, tile, 0);
		}
		case 1: {
		    return new ContainerUpgrade(player, tile, 1);
		}
        case 2: return new ContainerDocumentation(player);
        case 3: {
            return new ContainerUpgrade(player, tile, 2);
        }
        }
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileIOCore tile = (TileIOCore) world.getBlockTileEntity(x, y, z);

		switch(ID) {
	    case 0: {
            return new GuiUpgrade(player, tile, 0, "gui.upgrade.io");
        }
        case 1: {
            return new GuiUpgrade(player, tile, 1, "gui.upgrade.remote");
        }
        case 2: return new GuiDocumentation(player);
        case 3: {
            return new GuiUpgrade(player, tile, 2, "gui.upgrade.turtle");
        }
        }
		
		return null;
	}

}
