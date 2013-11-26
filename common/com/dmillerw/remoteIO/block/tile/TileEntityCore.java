package com.dmillerw.remoteIO.block.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityCore extends TileEntity {

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
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, tag);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void updateEntity() {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (side.isClient()) {
			updateClient();
		} else if (side.isServer()) {
			updateServer();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void updateClient() {
		
	}
	
	@SideOnly(Side.SERVER)
	public void updateServer() {
		
	}
	
	public abstract void writeCustomNBT(NBTTagCompound nbt);
	
	public abstract void readCustomNBT(NBTTagCompound nbt);
	
}
