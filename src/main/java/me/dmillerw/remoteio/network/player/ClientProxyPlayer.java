package me.dmillerw.remoteio.network.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

/**
 * @author dmillerw
 */
public class ClientProxyPlayer extends EntityPlayerSP {

    private EntityPlayerSP parentPlayer;
    public ClientProxyPlayer(EntityPlayerSP parentPlayer) {
        super(Minecraft.getMinecraft(),parentPlayer.world, parentPlayer.connection, parentPlayer.getStatFileWriter());
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