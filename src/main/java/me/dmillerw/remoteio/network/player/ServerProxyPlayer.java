package me.dmillerw.remoteio.network.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created by dmillerw
 */
public class ServerProxyPlayer extends EntityPlayerMP {

    private EntityPlayerMP proxyPlayer;
    public ServerProxyPlayer(EntityPlayerMP proxyPlayer) {
        super(proxyPlayer.getServer(), proxyPlayer.getServerWorld(), proxyPlayer.getGameProfile(), proxyPlayer.interactionManager);
    }

    @Override
    public float getDistanceToEntity(Entity entity) {
        return 6;
    }

    @Override
    public double getDistanceSq(double x, double y, double z) {
        return 6;
    }

    @Override
    public double getDistance(double x, double y, double z) {
        return 6;
    }

    @Override
    public double getDistanceSqToEntity(Entity entity) {
        return 6;
    }
}
