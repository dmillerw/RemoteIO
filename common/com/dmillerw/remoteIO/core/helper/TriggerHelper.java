package com.dmillerw.remoteIO.core.helper;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;

public class TriggerHelper {

	public static IOTriggerWrapper wrapTrigger(ITrigger trigger) {
		return new IOTriggerWrapper(trigger);
	}
	
}
