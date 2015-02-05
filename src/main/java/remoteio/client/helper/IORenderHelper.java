package remoteio.client.helper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

/**
 * @author dmillerw
 */
public class IORenderHelper {
    public static void renderCube(IIcon icon) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        // TOP
        tessellator.addVertexWithUV(0, 1.001, 0, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(0, 1.001, 1, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(1, 1.001, 1, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(1, 1.001, 0, icon.getMaxU(), icon.getMinV());

        // BOTTOM
        tessellator.addVertexWithUV(1, -0.001, 0, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(1, -0.001, 1, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(0, -0.001, 1, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(0, -0.001, 0, icon.getMinU(), icon.getMinV());

        // NORTH
        tessellator.addVertexWithUV(0, 1, -0.001, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(1, 1, -0.001, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(1, 0, -0.001, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(0, 0, -0.001, icon.getMinU(), icon.getMinV());

        // SOUTH
        tessellator.addVertexWithUV(0, 0, 1.001, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(1, 0, 1.001, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(1, 1, 1.001, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(0, 1, 1.001, icon.getMaxU(), icon.getMinV());

        // EAST
        tessellator.addVertexWithUV(1.001, 1, 0, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(1.001, 1, 1, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(1.001, 0, 1, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(1.001, 0, 0, icon.getMinU(), icon.getMinV());

        // WEST
        tessellator.addVertexWithUV(-0.001, 0, 0, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(-0.001, 0, 1, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(-0.001, 1, 1, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(-0.001, 1, 0, icon.getMaxU(), icon.getMinV());

        tessellator.draw();
    }
}
