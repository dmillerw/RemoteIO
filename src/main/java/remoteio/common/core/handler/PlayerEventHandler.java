package remoteio.common.core.handler;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import remoteio.common.lib.ModItems;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author dmillerw
 */
public class PlayerEventHandler {

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            ItemStack held = event.entityPlayer.getHeldItem();
            boolean preventBlock = false;
            boolean preventItem = false;

            for (ItemStack itemStack : event.entityPlayer.inventory.mainInventory) {
                if (itemStack != null) {
                    if (itemStack.getItem() == ModItems.interactionInhibitor) {
                        if (itemStack.getItemDamage() == 1) {
                            preventBlock = true;
                            break;
                        } else if (itemStack.getItemDamage() == 3) {
                            preventItem = true;
                            break;
                        }
                    }
                }
            }

            if (held != null && held.hasTagCompound() && held.getTagCompound().hasKey("inhibit")) {
                byte inhibitor = held.getTagCompound().getByte("inhibit");
                switch (inhibitor) {
                    case 0: preventBlock = true; break;
                    case 1: preventItem = true; break;
                    default: break;
                }
            }

            // Don't fuck with things if they shouldn't be fucked with
            if (preventBlock || preventItem) {
                if (preventBlock) {
                    event.useBlock = Event.Result.DENY;
                } else {
                    event.useBlock = Event.Result.ALLOW;
                }

                if (preventItem) {
                    // Prevents annoying client block flicker
                    if (held != null && held.getItem() instanceof ItemBlock) {
                        event.setCanceled(true);
                    } else {
                        event.useItem = Event.Result.DENY;
                    }
                } else {
                    event.useItem = Event.Result.ALLOW;
                }
            }
        }
    }
}
