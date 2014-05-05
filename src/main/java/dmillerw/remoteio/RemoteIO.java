package dmillerw.remoteio;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.remoteio.core.proxy.CommonProxy;
import dmillerw.remoteio.lib.ModInfo;

@Mod(modid= ModInfo.ID, name=ModInfo.NAME, version=ModInfo.VERSION)
public class RemoteIO {

	@Instance(ModInfo.ID)
	public static RemoteIO instance;
	
	@SidedProxy(serverSide=ModInfo.SERVER, clientSide=ModInfo.CLIENT)
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
	
}
