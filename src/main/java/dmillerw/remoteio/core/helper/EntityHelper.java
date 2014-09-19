package dmillerw.remoteio.core.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

/**
 * @author dmillerw
 */
public class EntityHelper {

    public static Vec3[] getEntityLookVectors(EntityLivingBase entity) {
        Vec3 vec3 = entity.getPosition(1F);
        Vec3 vec31 = entity.getLook(1F);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * 5, vec31.yCoord * 5, vec31.zCoord * 5);

        return new Vec3[]{vec3, vec32};
    }
}
