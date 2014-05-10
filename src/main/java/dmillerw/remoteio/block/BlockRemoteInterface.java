package dmillerw.remoteio.block;

import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.api.IIOTool;
import dmillerw.remoteio.block.tile.TileRemoteInterface;
import dmillerw.remoteio.client.render.RenderBlockRemoteInterface;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.ModInfo;
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
public class BlockRemoteInterface extends BlockContainer {

	public IIcon[] icons;

	public BlockRemoteInterface() {
		super(Material.iron);

		setHardness(5F);
		setResistance(5F);
		setCreativeTab(TabRemoteIO.TAB);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
		if (!world.isRemote) {
			TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

			if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IIOTool) {
				player.openGui(RemoteIO.instance, GuiHandler.GUI_REMOTE_INTERFACE, world, x, y, z);
			} else {
				if (tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_ACCESS)) {
					DimensionalCoords coords = tile.remotePosition;
					coords.getBlock().onBlockActivated(coords.getWorld(), coords.x, coords.y, coords.z, player, side, fx, fy, fz);
				}
			}
		}
		return true;
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
	public int getRenderType() {
		return RenderBlockRemoteInterface.renderID;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

		if (tile != null) {
			if (tile.visualState != TileRemoteInterface.VisualState.REMOTE_CAMO) {
				return icons[tile.visualState.ordinal()];
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
		return new TileRemoteInterface();
	}

}
