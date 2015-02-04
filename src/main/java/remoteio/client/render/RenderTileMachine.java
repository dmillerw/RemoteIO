package remoteio.client.render;

import remoteio.common.block.BlockMachine;
import remoteio.client.helper.IORenderHelper;
import remoteio.common.core.helper.ArrayHelper;
import remoteio.common.tile.TileMachineHeater;
import remoteio.common.tile.TileMachineReservoir;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class RenderTileMachine extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        boolean render = false;
        if (tileEntity instanceof TileMachineReservoir) if (((TileMachineReservoir)tileEntity).filled) render = true;
        if (tileEntity instanceof TileMachineHeater) if (((TileMachineHeater) tileEntity).filled) render = true;
        if (!render) return;

        IIcon icon = ArrayHelper.safeGetArray(BlockMachine.overlays, tileEntity.getBlockMetadata());

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
