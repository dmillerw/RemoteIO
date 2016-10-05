package me.dmillerw.remoteio.tile;

import me.dmillerw.remoteio.block.BlockRemoteInterface;
import me.dmillerw.remoteio.block.ModBlocks;
import me.dmillerw.remoteio.core.frequency.DeviceRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by dmillerw
 */
public class TileRemoteInterface extends TileCore implements ITickable {

    private BlockPos remotePosition;
    private boolean runSync = false;

    @Override
    public void writeDescription(NBTTagCompound compound) {
        if (remotePosition != null)
            compound.setLong("_remote_position", remotePosition.toLong());
    }

    @Override
    public void readDescription(NBTTagCompound compound) {
        if (compound.hasKey("_remote_position"))
            remotePosition = BlockPos.fromLong(compound.getLong("_remote_position"));
        else
            remotePosition = null;
    }

    @Override
    public void onLoad() {
        if (!worldObj.isRemote) {
            remotePosition = DeviceRegistry.getWatchedBlock(getFrequency());

            if (remotePosition != null)
                runSync = true;
        }
    }

    @Override
    public void update() {
        if (!worldObj.isRemote && !runSync) {
            BlockPos pos = DeviceRegistry.getWatchedBlock(getFrequency());
            if (pos == null) {
                if (remotePosition != null) {
                    this.remotePosition = null;
                    markDirtyAndNotify();
                }
            } else if (!pos.equals(remotePosition)) {
                this.remotePosition = pos;
                markDirtyAndNotify();
            }
        }
    }

    private int getFrequency() {
        return 0;
    }

    public BlockPos getRemotePosition() {
        return remotePosition;
    }

    public IBlockState getRemoteState() {
        if (remotePosition != null) {
            IBlockState state = worldObj.getBlockState(remotePosition);
            if (state.getBlock() == ModBlocks.analyzer || state.getBlock() == ModBlocks.remote_interface)
                return null;

            return state;
        }
        else
            return null;
    }

    @SideOnly(Side.CLIENT)
    public IExtendedBlockState getExtendedBlockState(IBlockState state) {
        IBlockState connected = getRemoteState();
        if (connected == null)
            return (IExtendedBlockState) state;

        String type = ForgeRegistries.BLOCKS.getKey(connected.getBlock()).toString();
        int data = connected.getBlock().getMetaFromState(connected);

        return ((IExtendedBlockState) state)
                .withProperty(BlockRemoteInterface.MIMICK_BLOCK, type)
                .withProperty(BlockRemoteInterface.MIMICK_VALUE, data);
    }

    /* START CAPABILITY HANDLING */

    private TileEntity getRemoteTile() {
        if (remotePosition != null)
            return worldObj.getTileEntity(remotePosition);
        else
            return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        TileEntity remoteTile = getRemoteTile();
        return remoteTile != null && remoteTile.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return getRemoteTile().getCapability(capability, facing);
    }
}
