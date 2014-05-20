package dmillerw.remoteio.block;

import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.api.IIOTool;
import dmillerw.remoteio.block.core.BlockIOCore;
import dmillerw.remoteio.client.render.RenderBlockRemoteInterface;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.core.helper.InventoryHelper;
import dmillerw.remoteio.core.helper.RotationHelper;
import dmillerw.remoteio.item.HandlerItem;
import dmillerw.remoteio.item.ItemWirelessTransmitter;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.tile.TileRemoteInventory;
import dmillerw.remoteio.tile.TileRemoteInventory;
import dmillerw.remoteio.tile.core.TileIOCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class BlockRemoteInventory extends BlockIOCore {

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
		super.onBlockActivated(world, x, y, z, player, side, fx, fy, fz);

		TileRemoteInventory tile = (TileRemoteInventory) world.getTileEntity(x, y, z);

		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == HandlerItem.wirelessTransmitter) {
			if (!world.isRemote) {
				EntityPlayer player1 = ItemWirelessTransmitter.getPlayer(player.getCurrentEquippedItem());

				if (player1 != null) {
					tile.setPlayer(player1);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int getGuiID() {
		return GuiHandler.GUI_REMOTE_INVENTORY;
	}

	@Override
	public TileIOCore getTileEntity() {
		return new TileRemoteInventory();
	}
}
