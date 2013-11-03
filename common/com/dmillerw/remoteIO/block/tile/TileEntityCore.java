package com.dmillerw.remoteIO.block.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class TileEntityCore extends TileEntity {

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		onUpdatePacket(pkt.data);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void sendUpdateToClient(NBTTagCompound tag) {
		PacketDispatcher.sendPacketToAllInDimension(new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, tag), this.worldObj.provider.dimensionId);
	}
	
	public abstract void onUpdatePacket(NBTTagCompound tag);
	
}
