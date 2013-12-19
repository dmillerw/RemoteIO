package com.dmillerw.remoteIO.block.tile;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.item.ItemTransmitter;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.server.FMLServerHandler;

public class TileRemoteInventory extends TileCore implements IInventory {

	public IInventory upgrades = new InventoryBasic("Upgrades", false, 2);
	
	public boolean lastClientState = false;
	
	public int state = 0;
	
	public String owner;
	
	private InventoryPlayer getInventory() {
		MinecraftServer server = MinecraftServer.getServer();
		
		if (owner != null && !(owner.isEmpty()) && server != null) {
			EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(owner);
			if (player != null && (Math.abs(player.getDistance(xCoord, yCoord, zCoord)) <= (RemoteIO.instance.rangeUpgradeBoost * InventoryHelper.amountContained(upgrades, Upgrade.RANGE.toItemStack(), false)) + RemoteIO.instance.defaultRange)) {
				return ItemTransmitter.hasSelfRemote(player) ? player.inventory : null;
			}
		}
		
		return null;
	}
	
	private InventoryPlayer getInventoryAndUpdate() {
		InventoryPlayer inv = getInventory();
		boolean connection = inv != null;
		if (lastClientState != connection) {
			lastClientState = connection;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		return inv;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		if (owner != null && !(owner.isEmpty())) {
			nbt.setString("owner", owner);
		}
		
		nbt.setBoolean("state", lastClientState);
		
		if (upgrades != null) {
			NBTTagCompound upgradeNBT = new NBTTagCompound();
			InventoryHelper.writeToNBT(upgrades, upgradeNBT);
			nbt.setCompoundTag("upgrades", upgradeNBT);
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("owner")) {
			owner = nbt.getString("owner");
		} else {
			owner = "";
		}
		
		lastClientState = nbt.getBoolean("state");
		
		if (nbt.hasKey("upgrades")) {
			ItemStack[] items = InventoryHelper.readFromNBT(upgrades, nbt.getCompoundTag("upgrades"));
			for (int i=0; i<items.length; i++) {
				this.upgrades.setInventorySlotContents(i, items[i]);
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return getInventoryAndUpdate() != null ? getInventory().getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getInventoryAndUpdate() != null ? getInventory().getStackInSlot(i) : null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getInventoryAndUpdate() != null ? getInventory().decrStackSize(i, j) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return getInventoryAndUpdate() != null ? getInventory().getStackInSlotOnClosing(i) : null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (getInventoryAndUpdate() != null) getInventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return "";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openChest() {
		
	}

	@Override
	public void closeChest() {
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return getInventoryAndUpdate() != null ? getInventory().isItemValidForSlot(i, itemstack) : false;
	}

}
