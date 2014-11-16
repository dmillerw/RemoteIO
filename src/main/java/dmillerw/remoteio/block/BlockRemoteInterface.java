package dmillerw.remoteio.block;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.block.core.BlockIOCore;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.core.helper.RotationHelper;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.VisualState;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.tile.core.TileIOCore;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class BlockRemoteInterface extends BlockIOCore {

    public static int renderID;

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        boolean result = super.onBlockActivated(world, x, y, z, player, side, fx, fy, fz);

        if (result) {
            return true;
        }

        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

        if (tile.remotePosition != null && !player.isSneaking() && tile.hasUpgradeChip(UpgradeType.REMOTE_ACCESS)) {
            DimensionalCoords there = tile.remotePosition;
            RemoteIO.proxy.activateBlock(world, there.x, there.y, there.z, player, RotationHelper.getRotatedSide(0, tile.rotationY, 0, side), fx, fy, fz);
        }

        return true;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_CAMO)) {
            return tile.remotePosition.getBlock().getLightValue(tile.remotePosition.getWorld(), tile.remotePosition.x, tile.remotePosition.y, tile.remotePosition.z);
        } else {
            return super.getLightValue(world, x, y, z);
        }
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_CAMO)) {
            return tile.remotePosition.getBlock().getLightOpacity(tile.remotePosition.getWorld(), tile.remotePosition.x, tile.remotePosition.y, tile.remotePosition.z);
        } else {
            return super.getLightOpacity(world, x, y, z);
        }
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_CAMO)) {
            return tile.remotePosition.getBlock().getBlockHardness(tile.remotePosition.getWorld(), tile.remotePosition.x, tile.remotePosition.y, tile.remotePosition.z);
        } else {
            return super.getBlockHardness(world, x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile.remotePosition != null && tile.hasTransferChip(TransferType.REDSTONE)) {
            tile.remotePosition.getBlock().onNeighborBlockChange(tile.remotePosition.getWorld(), tile.remotePosition.x, tile.remotePosition.y, tile.remotePosition.z, block);
        } else {
            super.onNeighborBlockChange(world, x, y, z, block);
        }

        tile.markForUpdate();
        tile.markForRenderUpdate();
        world.updateLightByType(EnumSkyBlock.Block, x, y, z);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_CAMO)) {
            return new ItemStack(tile.remotePosition.getBlock());
        } else {
            return super.getPickBlock(target, world, x, y, z);
        }
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_CAMO)) {
            return tile.remotePosition.getMeta();
        } else {
            return super.getDamageValue(world, x, y, z);
        }
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        if (!world.isRemote) {
            TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
            if (tile != null && tile.hasTransferChip(TransferType.REDSTONE) && tile.remotePosition != null) {
                DimensionalCoords there = tile.remotePosition;
                Block remote = there.getBlock();

                if (remote.hasComparatorInputOverride()) {
                    return remote.getComparatorInputOverride(there.getWorld(), there.x, there.y, there.z, RotationHelper.getRotatedSide(0, tile.rotationY, 0, side));
                }
            }
        }
        return 0;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
            if (tile != null && tile.hasTransferChip(TransferType.REDSTONE) && tile.remotePosition != null) {
                DimensionalCoords there = tile.remotePosition;
                Block remote = there.getBlock();

                if (remote.canProvidePower()) {
                    return remote.isProvidingWeakPower(there.getWorld(), there.x, there.y, there.z, RotationHelper.getRotatedSide(0, tile.rotationY, 0, side));
                }
            }
        }
        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
            if (tile != null && tile.hasTransferChip(TransferType.REDSTONE) && tile.remotePosition != null) {
                DimensionalCoords there = tile.remotePosition;
                Block remote = there.getBlock();

                if (remote.canProvidePower()) {
                    return remote.isProvidingStrongPower(there.getWorld(), there.x, there.y, there.z, RotationHelper.getRotatedSide(0, tile.rotationY, 0, side));
                }
            }
        }
        return 0;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);
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
        return BlockRemoteInterface.renderID;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileRemoteInterface tileRemoteInterface = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tileRemoteInterface.remotePosition != null && tileRemoteInterface.remotePosition.inWorld(FMLClientHandler.instance().getWorldClient())) {
            Block block = tileRemoteInterface.remotePosition.getBlock();
            if (block.getRenderType() == 0) {
                return block.getIcon(world, tileRemoteInterface.remotePosition.x, tileRemoteInterface.remotePosition.y, tileRemoteInterface.remotePosition.z, RotationHelper.getRotatedSide(0, tileRemoteInterface.rotationY, 0, side));
            }
        }
        return super.getIcon(world, x, y, z, side);
    }

    @Override
    public TileIOCore getTileEntity() {
        return new TileRemoteInterface();
    }
}
