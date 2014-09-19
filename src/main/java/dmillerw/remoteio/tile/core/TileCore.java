package dmillerw.remoteio.tile.core;

import dmillerw.remoteio.network.VanillaPacketHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * @author dmillerw
 */
public abstract class TileCore extends TileEntity {

	public abstract void writeCustomNBT(NBTTagCompound nbt);

	public abstract void readCustomNBT(NBTTagCompound nbt);

	public void onClientUpdate(NBTTagCompound nbt) {}

    public void onNeighborUpdated() {}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeCustomNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readCustomNBT(nbt);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	public void markForUpdate() {
		if (hasWorldObj()) {
			getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public void markForRenderUpdate() {
		if (hasWorldObj()) {
			getWorldObj().markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}
	}

	public void updateNeighbors() {
		if (hasWorldObj()) {
			getWorldObj().notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		}
	}

	public void sendNBTUpdate() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void sendClientUpdate(NBTTagCompound tag) {
		VanillaPacketHelper.sendToAllWatchingTile(this, new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag));
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		switch(pkt.func_148853_f()) {
			case 0: readFromNBT(pkt.func_148857_g()); break;
			case 1: onClientUpdate(pkt.func_148857_g()); break;
			default: break;
		}
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}

}
