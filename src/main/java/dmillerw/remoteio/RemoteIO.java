package dmillerw.remoteio;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.remoteio.block.BlockRemoteInterface;
import dmillerw.remoteio.block.HandlerBlock;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.core.proxy.CommonProxy;
import dmillerw.remoteio.core.tracker.BlockTracker;
import dmillerw.remoteio.item.HandlerItem;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.recipe.HandlerRecipe;
import dmillerw.remoteio.recipe.RecipeCopyLocation;
import dmillerw.remoteio.recipe.RecipeKeepTransmitter;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
public class RemoteIO {

	@Instance(ModInfo.ID)
	public static RemoteIO instance;

	@SidedProxy(serverSide = ModInfo.SERVER, clientSide = ModInfo.CLIENT)
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		HandlerBlock.initialize();
		HandlerItem.initialize();

		BlockRemoteInterface.renderID = RenderingRegistry.getNextAvailableRenderId();

		GameRegistry.addRecipe(RecipeCopyLocation.INSTANCE);
		FMLCommonHandler.instance().bus().register(RecipeCopyLocation.INSTANCE);
		FMLCommonHandler.instance().bus().register(new RecipeKeepTransmitter());
		FMLCommonHandler.instance().bus().register(BlockTracker.INSTANCE);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

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
}
