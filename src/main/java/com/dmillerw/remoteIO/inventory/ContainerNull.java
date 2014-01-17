package com.dmillerw.remoteIO.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Created by Dylan Miller on 1/16/14
 */
public class ContainerNull extends Container {
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
}
