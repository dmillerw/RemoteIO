package remoteio.common.core.helper;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author dmillerw
 */
public class RotationHelper {

    public static int getRotatedSide(int rotationX, int rotationY, int rotationZ, int side) {
        ForgeDirection forgeDirection = ForgeDirection.getOrientation(side);

        if (rotationX > 0) {
            for (int i = 0; i < rotationX; i++) {
                forgeDirection = forgeDirection.getRotation(ForgeDirection.EAST);
            }
        }

        if (rotationY > 0) {
            for (int i = 0; i < rotationY; i++) {
                forgeDirection = forgeDirection.getRotation(ForgeDirection.UP);
            }
        }

        if (rotationZ > 0) {
            for (int i = 0; i < rotationZ; i++) {
                forgeDirection = forgeDirection.getRotation(ForgeDirection.NORTH);
            }
        }

        return forgeDirection.ordinal();
    }
}
