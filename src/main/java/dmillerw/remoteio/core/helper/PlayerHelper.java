package dmillerw.remoteio.core.helper;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

import java.util.Iterator;

/**
 * @author dmillerw
 */
public class PlayerHelper {

    public static EntityPlayerMP getPlayerForUsername(String username) {
        ServerConfigurationManager serverConfigurationManager = MinecraftServer.getServer().getConfigurationManager();

        Iterator iterator = serverConfigurationManager.playerEntityList.iterator();
        EntityPlayerMP entityplayermp;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entityplayermp = (EntityPlayerMP) iterator.next();
        }
        while (!entityplayermp.getCommandSenderName().equalsIgnoreCase(username));

        return entityplayermp;
    }
}
