package com.dmillerw.remoteIO.core.helper;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;

import com.dmillerw.remoteIO.block.tile.TileEntityIO;

public class IOTriggerWrapper implements ITrigger {

	public ITrigger trigger;
	
	public IOTriggerWrapper(ITrigger trigger) {
		if (trigger == null) {
			throw new RuntimeException("[RemoteIO] Attempted to wrap a null trigger!");
		}
		this.trigger = trigger;
	}

	@Override
	public int getLegacyId() {
		return trigger.getLegacyId();
	}

	@Override
	public String getUniqueTag() {
		return trigger.getUniqueTag();
	}

	@Override
	public Icon getIcon() {
		return trigger.getIcon();
	}

	@Override
	public void registerIcons(IconRegister iconRegister) {
		trigger.registerIcons(iconRegister);
	}

	@Override
	public boolean hasParameter() {
		return trigger.hasParameter();
	}

	@Override
	public String getDescription() {
		return trigger.getDescription();
	}

	@Override
	public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter) {
		if (tile instanceof TileEntityIO) {
			TileEntity tile2 = ((TileEntityIO)tile).getTileEntity();
			
			return trigger.isTriggerActive(side, tile2, parameter);
		}
		
		return trigger.isTriggerActive(side, tile, parameter);
	}

	@Override
	public ITriggerParameter createParameter() {
		return trigger.createParameter();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ITrigger) {
			ITrigger trigger = (ITrigger) obj;
			
			return (this.trigger.getLegacyId() == trigger.getLegacyId()) || (this.trigger.getUniqueTag() == trigger.getUniqueTag());
		}
		
		return false;
	}
	
}
