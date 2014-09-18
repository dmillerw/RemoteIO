package dmillerw.remoteio.core.handler;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dmillerw.remoteio.item.HandlerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

import java.util.Set;

/**
 * @author dmillerw
 */
public class PlayerEventHandler {

    public static Set<String> whitelist = Sets.newHashSet();

    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerOpenContainerEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (whitelist.contains(player.getCommandSenderName())) {
            event.setResult(Event.Result.ALLOW);
            whitelist.remove(player.getCommandSenderName());
        } else if (player.getHeldItem() != null && player.getHeldItem().getItem() == HandlerItem.wirelessTransmitter) {
            event.setResult(Event.Result.ALLOW);
        }
    }
}
