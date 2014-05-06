package dmillerw.remoteio.core.handler;

import cpw.mods.fml.common.network.IGuiHandler;
import dmillerw.remoteio.block.tile.TileRemoteInterface;
import dmillerw.remoteio.client.gui.GuiRemoteInterface;
import dmillerw.remoteio.inventory.ContainerRemoteInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class GuiHandler implements IGuiHandler {

	public static final int GUI_REMOTE_INTERFACE = 0;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch(id) {
			case GUI_REMOTE_INTERFACE:
				return new ContainerRemoteInterface(player.inventory, (TileRemoteInterface) world.getTileEntity(x, y, z));
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch(id) {
			case GUI_REMOTE_INTERFACE:
				return new GuiRemoteInterface(player.inventory, (TileRemoteInterface) world.getTileEntity(x, y, z));
		}

		return null;
	}

}
