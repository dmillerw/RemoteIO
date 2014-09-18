package dmillerw.remoteio.core.handler;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dmillerw.remoteio.item.HandlerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

/**
 * @author dmillerw
 */
public class PlayerEventHandler {

    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerOpenContainerEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (player.getHeldItem() != null && player.getHeldItem().getItem() == HandlerItem.wirelessTransmitter) {
            event.setResult(Event.Result.ALLOW);
        }
    }
}
