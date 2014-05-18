package dmillerw.remoteio.block;

import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.api.IIOTool;
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
public class BlockRemoteInventory extends BlockContainer {

	private IIcon[] icons;

	public BlockRemoteInventory() {
		super(Material.iron);

		setHardness(5F);
		setResistance(5F);
		setCreativeTab(TabRemoteIO.TAB);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
		if (!world.isRemote) {
			TileRemoteInventory tile = (TileRemoteInventory) world.getTileEntity(x, y, z);

			if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == HandlerItem.wirelessTransmitter) {
				EntityPlayer player1 = ItemWirelessTransmitter.getPlayer(player.getCurrentEquippedItem());

				if (player1 != null) {
					tile.setPlayer(player1);
				}
			}
		}
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (!world.isRemote) {
			TileRemoteInventory tile = (TileRemoteInventory) world.getTileEntity(x, y, z);

			if (tile != null) {
				InventoryHelper.dropContents(tile.upgradeChips, world, x, y, z);
				InventoryHelper.dropContents(tile.transferChips, world, x, y, z);
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return icons[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileRemoteInventory tile = (TileRemoteInventory) world.getTileEntity(x, y, z);

		if (tile != null) {
			if (!tile.visualState.isCamouflage()) {
				return icons[tile.visualState.ordinal()];
			} else if (tile.simpleCamo != null) {
				return Block.getBlockFromItem(tile.simpleCamo.getItem()).getIcon(side, tile.simpleCamo.getItemDamage());
			}
		}

		return icons[0];
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		icons = new IIcon[4];
		icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "inactive");
		icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "inactive_blink");
		icons[2] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "active");
		icons[3] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "active_blink");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileRemoteInventory();
	}

}
