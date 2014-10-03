package dmillerw.remoteio;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.remoteio.block.BlockRemoteInterface;
import dmillerw.remoteio.block.HandlerBlock;
import dmillerw.remoteio.core.handler.BlockUpdateTicker;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.core.handler.PlayerEventHandler;
import dmillerw.remoteio.core.helper.EventHelper;
import dmillerw.remoteio.core.proxy.CommonProxy;
import dmillerw.remoteio.core.tracker.BlockTracker;
import dmillerw.remoteio.item.HandlerItem;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.network.PacketHandler;
import dmillerw.remoteio.recipe.HandlerRecipe;
import dmillerw.remoteio.recipe.RecipeCopyLocation;
import dmillerw.remoteio.recipe.RecipeKeepTransmitter;
import net.minecraft.item.Item;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
public class RemoteIO {

    @Instance(ModInfo.ID)
    public static RemoteIO instance;

    @SidedProxy(serverSide = ModInfo.SERVER, clientSide = ModInfo.CLIENT)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModMetadata modMetadata = event.getModMetadata();
        modMetadata.version = ModInfo.VERSION;

        HandlerBlock.initialize();
        HandlerItem.initialize();

        BlockRemoteInterface.renderID = RenderingRegistry.getNextAvailableRenderId();

        GameRegistry.addRecipe(RecipeCopyLocation.INSTANCE);

        EventHelper.register(RecipeCopyLocation.INSTANCE);
        EventHelper.register(new RecipeKeepTransmitter());
        EventHelper.register(BlockTracker.INSTANCE);
        EventHelper.register(new PlayerEventHandler());
        EventHelper.register(new BlockUpdateTicker());

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        PacketHandler.initialize();

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // We do recipe setup in post-init as some recipes rely on other mods
        HandlerRecipe.initialize();

        proxy.postInit(event);
    }

    @EventHandler
    public void checkMappings(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping map : event.getAll()) {
            if (map.name.startsWith("remoteio:")) {
                String name = map.name.substring(map.name.indexOf(":") + 1);
                if (map.type == GameRegistry.Type.BLOCK) {
                    map.remap(GameRegistry.findBlock(ModInfo.ID, name));
                } else if (map.type == GameRegistry.Type.ITEM) {
                    if (name.equalsIgnoreCase("remote_interface") || name.equalsIgnoreCase("remote_inventory")) {
                        map.remap(Item.getItemFromBlock(GameRegistry.findBlock(ModInfo.ID, name)));
                    } else {
                        map.remap(GameRegistry.findItem(ModInfo.ID, name));
                    }
                }
            }
        }
    }
}
