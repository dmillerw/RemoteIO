package me.dmillerw.remoteio.core.handler;

import me.dmillerw.remoteio.entity.EntityItemEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by dmillerw
 */
public class EntityEventHandler {

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
        EntityItem entityItem = event.getEntityItem();
        ItemStack item = entityItem.getEntityItem();

        if (item.getItem() == Items.ENDER_PEARL) {
            entityItem.setDead();

            EntityItemEnderPearl entity = new EntityItemEnderPearl(entityItem);
            entity.motionX = entityItem.motionX;
            entity.motionY = entityItem.motionY;
            entity.motionZ = entityItem.motionZ;
            entity.setDefaultPickupDelay();
            entityItem.worldObj.spawnEntityInWorld(entity);
        }
    }
}
