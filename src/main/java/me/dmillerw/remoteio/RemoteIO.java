package me.dmillerw.remoteio;

import me.dmillerw.remoteio.core.frequency.FrequencyRegistry;
import me.dmillerw.remoteio.core.proxy.IProxy;
import me.dmillerw.remoteio.lib.ModInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import java.io.File;

/**
 * Created by dmillerw
 */
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, dependencies = "required-after:Forge@[12.18.2.2099,)")
public class RemoteIO {

    @Mod.Instance("remoteio")
    public static RemoteIO instance;

    @SidedProxy(
            serverSide = ModInfo.CORE_PACKAGE + ".core.proxy.CommonProxy",
            clientSide = ModInfo.CORE_PACKAGE + ".core.proxy.ClientProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        FrequencyRegistry.load(new File(DimensionManager.getCurrentSaveRootDirectory(), "SavedFrequencies.cfg"));
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        FrequencyRegistry.save(new File(DimensionManager.getCurrentSaveRootDirectory(), "SavedFrequencies.cfg"));
    }
}
