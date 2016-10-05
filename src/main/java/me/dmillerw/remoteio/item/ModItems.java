package me.dmillerw.remoteio.item;

import me.dmillerw.remoteio.lib.ModInfo;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by dmillerw
 */
@GameRegistry.ObjectHolder(ModInfo.MOD_ID)
public class ModItems {

    public static final ItemPocketGadget pocket_gadget = null;

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    new ItemPocketGadget().setRegistryName(ModInfo.MOD_ID, "pocket_gadget")
            );
        }
    }
}
