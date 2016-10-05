package me.dmillerw.remoteio.block;

import me.dmillerw.remoteio.lib.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by dmillerw
 */
@GameRegistry.ObjectHolder(ModInfo.MOD_ID)
public class ModBlocks {

    public static final BlockRemoteInterface remote_interface = null;
    @GameRegistry.ObjectHolder(ModInfo.MOD_ID + ":remote_interface")
    public static final ItemBlock remote_interface_item = null;

    public static final BlockAnalyzer analyzer = null;
    @GameRegistry.ObjectHolder(ModInfo.MOD_ID + ":analyzer")
    public static final ItemBlock analyzer_item = null;

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(
                    new BlockRemoteInterface().setRegistryName(ModInfo.MOD_ID, "remote_interface"),
                    new BlockAnalyzer().setRegistryName(ModInfo.MOD_ID, "analyzer")
            );
        }

        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    new ItemBlock(ModBlocks.remote_interface).setRegistryName(ModInfo.MOD_ID, "remote_interface"),
                    new ItemBlock(ModBlocks.analyzer).setRegistryName(ModInfo.MOD_ID, "analyzer")
            );
        }
    }
}
