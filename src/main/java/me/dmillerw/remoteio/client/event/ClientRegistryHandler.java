package me.dmillerw.remoteio.client.event;

import me.dmillerw.remoteio.block.ModBlocks;
import me.dmillerw.remoteio.lib.ModInfo;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by dmillerw
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientRegistryHandler {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent evt) {
        ModelLoader.setCustomModelResourceLocation(ModBlocks.remote_interface_item, 0, new ModelResourceLocation(ModInfo.MOD_ID + ":remote_interface", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModBlocks.analyzer_item, 0, new ModelResourceLocation(ModInfo.MOD_ID + ":analyzer", "inventory"));
    }
}
