package dmillerw.remoteio.client.render;

import dmillerw.remoteio.block.BlockIntelligentWorkbench;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class RenderTileIntelligentWorkbench extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partial) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glTranslated(x, y, z);

        bindTexture(TextureMap.locationBlocksTexture);

        char c0 = 61680;
        int j = c0 % 65536;
        int k = c0 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
        GL11.glColor4f(1, 1, 1, 1);

        IIcon icon = BlockIntelligentWorkbench.overlay;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        tessellator.addVertexWithUV(0, 1.001, 0, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(0, 1.001, 1, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(1, 1.001, 1, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(1, 1.001, 0, icon.getMaxU(), icon.getMinV());

        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
