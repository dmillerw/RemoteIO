package dmillerw.remoteio.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.api.IIOTool;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.client.render.RenderBlockRemoteInterface;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.core.helper.InventoryHelper;
import dmillerw.remoteio.core.helper.RotationHelper;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

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
			int adjustedSide = RotationHelper.getRotatedSide(0, tile.rotationY, 0, side);

			if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IIOTool) {
				if (!player.isSneaking()) {
					player.openGui(RemoteIO.instance, GuiHandler.GUI_REMOTE_INTERFACE, world, x, y, z);
				}
			} else {
				if (tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_ACCESS)) {
					DimensionalCoords there = tile.remotePosition;
					Block remote = there.getBlock();

					remote.onBlockActivated(there.getWorld(), there.x, there.y, there.z, player, adjustedSide, fx, fy, fz);
				}
			}
		}
		return true;
	}

	/* BEGIN COLLISION HANDLING */

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

		if (tile != null && tile.visualState == TileRemoteInterface.VisualState.CAMOUFLAGE_REMOTE && tile.remotePosition != null) {
			DimensionalCoords there = tile.remotePosition;
			Block remote = there.getBlock(world);

			int offsetX = there.x - x;
			int offsetY = there.y - y;
			int offsetZ = there.z - z;

			Vec3 offsetStart = Vec3.createVectorHelper(start.xCoord + offsetX, start.yCoord + offsetY, start.zCoord + offsetZ);
			Vec3 offsetEnd = Vec3.createVectorHelper(end.xCoord + offsetX, end.yCoord + offsetY, end.zCoord + offsetZ);

			MovingObjectPosition mob = remote.collisionRayTrace(world, there.x, there.y, there.z, offsetStart, offsetEnd);

			if (mob != null) {
				mob.blockX -= offsetX;
				mob.blockY -= offsetY;
				mob.blockZ -= offsetZ;
				mob.hitVec.xCoord -= offsetX;
				mob.hitVec.yCoord -= offsetY;
				mob.hitVec.zCoord -= offsetZ;
			}

			return mob;
		}

		return super.collisionRayTrace(world, x, y, z, start, end);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

		if (tile != null && tile.visualState == TileRemoteInterface.VisualState.CAMOUFLAGE_REMOTE && tile.remotePosition != null) {
			DimensionalCoords there = tile.remotePosition;
			Block remote = there.getBlock(world);

			int offsetX = there.x - x;
			int offsetY = there.y - y;
			int offsetZ = there.z - z;

			EntityPlayer player = Minecraft.getMinecraft().thePlayer;

			//TODO: Rotate the player based on the block rotation to get accurate hit results

			// We're about to descend into madness here...
			player.prevPosX += offsetX;
			player.prevPosY += offsetY;
			player.prevPosZ += offsetZ;
			player.posX += offsetX;
			player.posY += offsetY;
			player.posZ += offsetZ;

			AxisAlignedBB aabb = remote.getSelectedBoundingBoxFromPool(world, there.x, there.y, there.z);

			// Ending the madness
			player.prevPosX -= offsetX;
			player.prevPosY -= offsetY;
			player.prevPosZ -= offsetZ;
			player.posX -= offsetX;
			player.posY -= offsetY;
			player.posZ -= offsetZ;

			if (aabb != null) {
				aabb.offset(-offsetX, -offsetY, -offsetZ);
			}

			return aabb;
		}

		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
		TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

		if (tile != null && tile.visualState == TileRemoteInterface.VisualState.CAMOUFLAGE_REMOTE && tile.remotePosition != null) {
			DimensionalCoords there = tile.remotePosition;
			Block remote = there.getBlock(world);

			int offsetX = there.x - x;
			int offsetY = there.y - y;
			int offsetZ = there.z - z;

			AxisAlignedBB newAABB = AxisAlignedBB.getBoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ).offset(offsetX, offsetY, offsetZ);
			List newList = new ArrayList();

			remote.addCollisionBoxesToList(world, there.x, there.y, there.z, newAABB, newList, entity);

			for (Object o : newList) {
				AxisAlignedBB aabb1 = (AxisAlignedBB) o;
				aabb1.offset(-offsetX, -offsetY, -offsetZ);

				if (aabb.intersectsWith(aabb1)) {
					list.add(aabb1);
				}
			}

			return;
		}

		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
	}

	/* END COLLISION HANDLING */

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (!world.isRemote) {
			TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

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
	public int getRenderType() {
		return RenderBlockRemoteInterface.renderID;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

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
		return new TileRemoteInterface();
	}

}
