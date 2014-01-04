package com.dmillerw.remoteIO.core.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;

/**
 * Created by Dylan Miller on 1/3/14
 */
public class EntityHelper {

    public static ForgeDirection getRotation2D(EntityLivingBase entity) {
        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        {
            return ForgeDirection.getOrientation(2);
        }

        if (l == 1)
        {
            return ForgeDirection.getOrientation(5);
        }

        if (l == 2)
        {
            return ForgeDirection.getOrientation(3);
        }

        if (l == 3)
        {
            return ForgeDirection.getOrientation(4);
        }

        return ForgeDirection.UNKNOWN;
    }

}
