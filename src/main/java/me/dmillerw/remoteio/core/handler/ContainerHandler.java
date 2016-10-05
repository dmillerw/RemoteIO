package me.dmillerw.remoteio.core.handler;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

/**
 * @author dmillerw
 */
public class ContainerHandler {

    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public static boolean canInteractWith(Container container, EntityPlayer entityPlayer) {
        if (INSTANCE.containerWhitelist.get(entityPlayer.getDisplayNameString()) == container)
            return true;
        else
            return container.canInteractWith(entityPlayer);
    }

    public static final ContainerHandler INSTANCE = new ContainerHandler();

    public Map<String, Container> containerWhitelist = Maps.newHashMap();

    @SubscribeEvent
    public void onContainerOpen(PlayerContainerEvent.Close event) {
        EntityPlayer entityPlayer = event.getEntityPlayer();
        containerWhitelist.remove(entityPlayer.getDisplayNameString());
    }
}