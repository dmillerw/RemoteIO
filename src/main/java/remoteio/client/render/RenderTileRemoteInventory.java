package remoteio.client.render;

import remoteio.common.block.core.BlockIOCore;
import remoteio.client.helper.IORenderHelper;
import remoteio.common.tile.TileRemoteInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class RenderTileRemoteInventory extends TileEntitySpecialRenderer {
    public void renderRemoteInterfaceAt(TileRemoteInventory tile, double x, double y, double z, float partial) {
        if (!tile.visualState.isCamouflage()) {
            IIcon icon = BlockIOCore.overlays[tile.visualState.ordinal()];

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glTranslated(x, y, z);

            bindTexture(TextureMap.locationBlocksTexture);

            char c0 = 61680;
            int j = c0 % 65536;
            int k = c0 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
            GL11.glColor4f(1, 1, 1, 1);

            IORenderHelper.renderCube(icon);

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
        renderRemoteInterfaceAt((TileRemoteInventory) var1, var2, var4, var6, var8);
    }
}
