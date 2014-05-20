package dmillerw.remoteio.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.api.IIOTool;
import dmillerw.remoteio.block.core.BlockIOCore;
import dmillerw.remoteio.client.render.RenderBlockRemoteInterface;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.core.helper.RotationHelper;
import dmillerw.remoteio.item.HandlerItem;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.VisualState;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.tile.core.TileIOCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class BlockRemoteInterface extends BlockIOCore {

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
		super.onBlockActivated(world, x, y, z, player, side, fx, fy, fz);

		if (!world.isRemote) {
			TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

			if (tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_ACCESS)) {
				int adjustedSide = RotationHelper.getRotatedSide(0, tile.rotationY, 0, side);
				DimensionalCoords there = tile.remotePosition;
				Block remote = there.getBlock();

				remote.onBlockActivated(there.getWorld(), there.x, there.y, there.z, player, adjustedSide, fx, fy, fz);

				return true;
			}
		}

		return false;
	}

	@Override
	public int getGuiID() {
		return GuiHandler.GUI_REMOTE_INTERFACE;
	}

	/* BEGIN COLLISION HANDLING */

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

		if (tile != null && tile.visualState == VisualState.CAMOUFLAGE_REMOTE && tile.remotePosition != null) {
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

		if (tile != null && tile.visualState == VisualState.CAMOUFLAGE_REMOTE && tile.remotePosition != null) {
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

		if (tile != null && tile.visualState == VisualState.CAMOUFLAGE_REMOTE && tile.remotePosition != null) {
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
	public int getRenderType() {
		return RenderBlockRemoteInterface.renderID;
	}

	@Override
	public TileIOCore getTileEntity() {
		return new TileRemoteInterface();
	}

}
