package remoteio.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;

/**
 * @author dmillerw
 */
public class ClientProxyPlayer extends EntityClientPlayerMP {

    private EntityClientPlayerMP parentPlayer;

    public ClientProxyPlayer(EntityClientPlayerMP parentPlayer) {
        super(Minecraft.getMinecraft(), parentPlayer.worldObj, Minecraft.getMinecraft().getSession(), parentPlayer.sendQueue, parentPlayer.getStatFileWriter());
    }

    @Override
    public float getDistanceToEntity(Entity p_70032_1_) {
        return 6;
    }

    @Override
    public double getDistanceSq(double p_70092_1_, double p_70092_3_, double p_70092_5_) {
        return 6;
    }

    @Override
    public double getDistance(double p_70011_1_, double p_70011_3_, double p_70011_5_) {
        return 6;
    }

    @Override
    public double getDistanceSqToEntity(Entity p_70068_1_) {
        return 6;
    }
}
