package me.dmillerw.remoteio.tile;

import me.dmillerw.remoteio.util.ReflectionUtil;
import me.dmillerw.remoteio.block.BlockRemoteInterface;
import me.dmillerw.remoteio.block.ModBlocks;
import me.dmillerw.remoteio.core.frequency.DeviceRegistry;
import me.dmillerw.remoteio.core.frequency.IFrequencyProvider;
import me.dmillerw.remoteio.lib.property.RenderState;
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
public class TileRemoteInterface extends TileCore implements ITickable, IFrequencyProvider {

    private BlockPos remotePosition;
    private int frequency = 0;

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        compound.setInteger("_frequency", frequency);
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        frequency = compound.getInteger("_frequency");
    }

    @Override
    public void writeDescription(NBTTagCompound compound) {
        if (remotePosition != null)
            compound.setLong("_remote_position", remotePosition.toLong());

        compound.setInteger("_frequency", frequency);
    }

    @Override
    public void readDescription(NBTTagCompound compound) {
        if (compound.hasKey("_remote_position"))
            remotePosition = BlockPos.fromLong(compound.getLong("_remote_position"));
        else
            remotePosition = null;

        frequency = compound.getInteger("_frequency");
    }

    @Override
    public void onLoad() {
        if (!worldObj.isRemote) {
            remotePosition = DeviceRegistry.getWatchedBlock(worldObj.provider.getDimension(), getFrequency());
        }
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            BlockPos pos = DeviceRegistry.getWatchedBlock(worldObj.provider.getDimension(), getFrequency());
            if (pos == null) {
                if (remotePosition != null) {
                    this.remotePosition = null;

                    notifyNeighbors();
                    markDirtyAndNotify();
                }
            } else if (!pos.equals(remotePosition)) {
                this.remotePosition = pos;

                notifyNeighbors();
                markDirtyAndNotify();
            }
        }
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public void setFrequency(int frequency) {
        this.frequency = frequency;

        notifyNeighbors();
        markDirtyAndNotify();
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    public BlockPos getRemotePosition() {
        return remotePosition;
    }

    public IBlockState getRemoteState() {
        if (remotePosition != null) {
            IBlockState state = worldObj.getBlockState(remotePosition);
            if (state.getBlock().isAir(state, worldObj, remotePosition))
                return null;

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
        if (connected == null) {
            return ((IExtendedBlockState) state)
                    .withProperty(BlockRemoteInterface.RENDER_STATE, RenderState.BLANK);
        }

        String type = ForgeRegistries.BLOCKS.getKey(connected.getBlock()).toString();
        int data = connected.getBlock().getMetaFromState(connected);

        RenderState renderState = new RenderState(type, data, true);
        if (connected instanceof IExtendedBlockState)
            renderState.unlistedProperties = ReflectionUtil.getUnlistedProperties((IExtendedBlockState) connected);

        return ((IExtendedBlockState) state)
                .withProperty(BlockRemoteInterface.RENDER_STATE, renderState);
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
