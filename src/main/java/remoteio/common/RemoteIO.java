package remoteio.common;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import remoteio.common.block.BlockRemoteInterface;
import remoteio.common.core.ChannelRegistry;
import remoteio.common.core.handler.*;
import remoteio.common.core.helper.EventHelper;
import remoteio.common.core.proxy.CommonProxy;
import remoteio.common.core.tracker.BlockTracker;
import remoteio.common.lib.ModBlocks;
import remoteio.common.lib.ModInfo;
import remoteio.common.lib.ModItems;
import remoteio.common.network.PacketHandler;
import remoteio.common.recipe.ModRecipes;
import remoteio.common.recipe.RecipeCopyLocation;
import remoteio.common.recipe.RecipeInhibitorApply;
import remoteio.common.recipe.RecipeRemoteInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
public class RemoteIO {

    @Instance(ModInfo.ID)
    public static RemoteIO instance;

    @SidedProxy(serverSide = ModInfo.SERVER, clientSide = ModInfo.CLIENT)
    public static CommonProxy proxy;

    public static ChannelRegistry channelRegistry;

    public static LocalizationUpdater localizationUpdater;

    public static Configuration configuration;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configuration = new Configuration(event.getSuggestedConfigurationFile());
        configuration.load();

        ModMetadata modMetadata = event.getModMetadata();
        modMetadata.version = ModInfo.VERSION;

        channelRegistry = new ChannelRegistry();

        ModBlocks.initialize();
        ModItems.initialize();

        BlockRemoteInterface.renderID = RenderingRegistry.getNextAvailableRenderId();

        // Used for clearing location chips
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.locationChip), new ItemStack(ModItems.locationChip));

        GameRegistry.addRecipe(RecipeCopyLocation.INSTANCE);
        GameRegistry.addRecipe(new RecipeInhibitorApply());
        GameRegistry.addRecipe(new RecipeRemoteInventory());

        EventHelper.register(RecipeCopyLocation.INSTANCE);
        EventHelper.register(new RecipeRemoteInventory());
        EventHelper.register(BlockTracker.INSTANCE);
        EventHelper.register(new BlockUpdateTicker());
        EventHelper.register(ContainerHandler.INSTANCE);
        EventHelper.register(new PlayerEventHandler());

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        PacketHandler.initialize();

        if (Loader.isModLoaded("Waila")) {
            FMLInterModComms.sendMessage("Waila", "register", "remoteio.common.core.compat.WailaProvider.registerProvider");
        }

        localizationUpdater = new LocalizationUpdater("dmillerw", "RemoteIO", "17", "src/main/resources/assets/remoteio/lang/");
        localizationUpdater.initializeThread(configuration);

        proxy.preInit(event);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // We do recipe setup in post-init as some recipes rely on other mods
        ModRecipes.initialize();

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
