package dmillerw.remoteio.core.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.remoteio.core.handler.PlayerEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public World getWorld(int dimension) {
        return MinecraftServer.getServer().worldServerForDimension(dimension);
    }

    public boolean canPlayerOpenContainer(EntityPlayer player) {
        return PlayerEventHandler.whitelist.contains(player.getCommandSenderName());
    }

    public void resetPlayerWhitelist(EntityPlayer player) {
        PlayerEventHandler.whitelist.remove(player.getCommandSenderName());
    }
}
