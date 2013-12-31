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

	public IInventory upgrades = new InventoryBasic("Upgrades", false, 9);
	
	public boolean unlimitedRange = false;
	public boolean remoteRequired = false;
	public boolean lastClientState = false;
	
	public int state = 0;
	
	public String owner;
	
	private InventoryPlayer getInventory() {
		MinecraftServer server = MinecraftServer.getServer();
		
		if (owner != null && !(owner.isEmpty()) && server != null) {
			EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(owner);
			if (player != null) {
				if ((player.worldObj.provider.dimensionId == this.worldObj.provider.dimensionId)) {
					if (Math.abs(player.getDistance(xCoord, yCoord, zCoord)) <= getRange() || unlimitedRange) {
						return (ItemTransmitter.hasSelfRemote(player) || !remoteRequired) ? player.inventory : null;
					}
				} else {
					if (InventoryHelper.inventoryContains(upgrades, Upgrade.CROSS_DIMENSIONAL.toItemStack(), false)) {
						return (ItemTransmitter.hasSelfRemote(player) || !remoteRequired) ? player.inventory : null;
					}
				}
			}
		}
		
		return null;
	}
	
	private int getRange() {
		int maxRange = RemoteIO.instance.defaultRange;
		maxRange += (upgradeCount(Upgrade.RANGE_T1) * RemoteIO.instance.rangeUpgradeT1Boost);
		maxRange += (upgradeCount(Upgrade.RANGE_T2) * RemoteIO.instance.rangeUpgradeT2Boost);
		maxRange += (upgradeCount(Upgrade.RANGE_T3) * RemoteIO.instance.rangeUpgradeT3Boost);
		maxRange += (upgradeCount(Upgrade.RANGE_WITHER) * RemoteIO.instance.rangeUpgradeWitherBoost);
		return maxRange;
	}
	
	private int upgradeCount(Upgrade upgrade) {
		return InventoryHelper.amountContained(upgrades, upgrade.toItemStack(), false);
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
		
		nbt.setBoolean("unlimitedRange", unlimitedRange);
		nbt.setBoolean("remoteRequired", remoteRequired);
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
		
		unlimitedRange = nbt.getBoolean("unlimitedRange");
		remoteRequired = nbt.getBoolean("remoteRequired");
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
