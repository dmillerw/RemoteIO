package me.dmillerw.remoteio.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * Created by dmillerw
 */
public class TileCore extends TileEntity {

    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeToDisk(compound);
        return compound;
    }

    @Override
    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readFromDisk(compound);
    }

    public void writeToDisk(NBTTagCompound compound) {

    }

    public void readFromDisk(NBTTagCompound compound) {

    }

    public void writeDescription(NBTTagCompound compound) {

    }

    public void readDescription(NBTTagCompound compound) {

    }

    public void markDirtyAndNotify() {
        markDirty();

        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    public void notifyNeighbors() {
        world.notifyNeighborsOfStateChange(pos, blockType, true);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeDescription(compound);
        return new SPacketUpdateTileEntity(pos, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readDescription(pkt.getNbtCompound());
        world.markBlockRangeForRenderUpdate(pos, pos);
    }
}
