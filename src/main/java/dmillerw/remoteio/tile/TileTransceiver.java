package dmillerw.remoteio.tile;

import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.tile.core.TileCore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author dmillerw
 */
public class TileTransceiver extends TileCore {

    public ForgeDirection orientation = ForgeDirection.UNKNOWN;

    public int channel = 0;

    private boolean forceUpdate = false;

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("orientation", (byte) orientation.ordinal());
        nbt.setInteger("channel", channel);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        orientation = ForgeDirection.getOrientation(nbt.getByte("orientation"));
        channel = nbt.getInteger("channel");
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            if (forceUpdate || RemoteIO.channelRegistry.pollDirty(channel)) {
                TileEntity tileEntity = worldObj.getTileEntity(xCoord + orientation.offsetX, yCoord + orientation.offsetY, zCoord + orientation.offsetZ);
                if (tileEntity != null && tileEntity instanceof TileRemoteInterface) {
                    ((TileRemoteInterface) tileEntity).setRemotePosition(RemoteIO.channelRegistry.getChannelData(channel));
                }
            }
        }
    }

    public void setChannel(int channel) {
        this.channel = channel;
        this.forceUpdate = true;
        markForUpdate();
    }
}
