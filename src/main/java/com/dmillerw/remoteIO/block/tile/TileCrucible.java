package com.dmillerw.remoteIO.block.tile;

import com.dmillerw.remoteIO.core.helper.EntityHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

/**
 * Created by Dylan Miller on 1/3/14
 */
public class TileCrucible extends TileCore {

    public ForgeDirection rotation = ForgeDirection.UNKNOWN;

    public boolean isActive() {
        return false;
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entity, ItemStack stack) {
        this.rotation = EntityHelper.getRotation2D(entity);
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("rotation", (byte) this.rotation.ordinal());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.rotation = ForgeDirection.getOrientation(nbt.getByte("rotation"));
    }

}
