package me.dmillerw.remoteio.core.handler;

import me.dmillerw.remoteio.core.frequency.FrequencyRegistry;
import me.dmillerw.remoteio.network.PacketHandler;
import me.dmillerw.remoteio.network.packet.client.CBulkFrequencyUpdate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Created by dmillerw
 */
public class PlayerEventHandler {

    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
    }

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.world.isRemote) {
            CBulkFrequencyUpdate packet = new CBulkFrequencyUpdate(FrequencyRegistry.getSavedFrequencies());
            PacketHandler.INSTANCE.sendTo(packet, (EntityPlayerMP) event.player);
        }
    }
}
