package dmillerw.remoteio.core.handler;

import cpw.mods.fml.common.network.IGuiHandler;
import dmillerw.remoteio.client.gui.GuiRemoteInterface;
import dmillerw.remoteio.client.gui.GuiRemoteInventory;
import dmillerw.remoteio.inventory.container.ContainerRemoteInterface;
import dmillerw.remoteio.inventory.container.ContainerRemoteInventory;
import dmillerw.remoteio.inventory.container.core.ContainerIO;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.client.gui.GuiSimpleCamo;
import dmillerw.remoteio.inventory.InventoryItem;
import dmillerw.remoteio.inventory.container.ContainerSimpleCamo;
import dmillerw.remoteio.tile.TileRemoteInventory;
import dmillerw.remoteio.tile.core.TileIOCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class GuiHandler implements IGuiHandler {

	public static final int GUI_REMOTE_INTERFACE = 0;
	public static final int GUI_SIMPLE_CAMO = 1;
	public static final int GUI_REMOTE_INVENTORY = 2;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch(id) {
			case GUI_REMOTE_INTERFACE:
				return new ContainerRemoteInterface(player.inventory, (TileRemoteInterface) world.getTileEntity(x, y, z));

			case GUI_SIMPLE_CAMO:
				return new ContainerSimpleCamo(player, new InventoryItem(player.getCurrentEquippedItem(), 1));

			case GUI_REMOTE_INVENTORY:
				return new ContainerRemoteInventory(player.inventory, (TileRemoteInventory) world.getTileEntity(x, y, z));
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch(id) {
			case GUI_REMOTE_INTERFACE:
				return new GuiRemoteInterface(player.inventory, (TileRemoteInterface) world.getTileEntity(x, y, z));

			case GUI_SIMPLE_CAMO:
				return new GuiSimpleCamo(player, new InventoryItem(player.getCurrentEquippedItem(), 1));

			case GUI_REMOTE_INVENTORY:
				return new GuiRemoteInventory(player.inventory, (TileRemoteInventory) world.getTileEntity(x, y, z));
		}

		return null;
	}

}
