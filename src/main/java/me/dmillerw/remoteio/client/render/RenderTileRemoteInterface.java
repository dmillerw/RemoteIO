package me.dmillerw.remoteio.client.render;

import me.dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLLog;

/**
 * Created by dmillerw
 */
public class RenderTileRemoteInterface extends TileEntitySpecialRenderer<TileRemoteInterface> {

    @Override
    public void renderTileEntityAt(TileRemoteInterface te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.getRemoteState() != null) {
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            TileEntity remote = getWorld().getTileEntity(te.getRemotePosition());
            if (remote != null) {
                try {
                    TileEntityRendererDispatcher.instance.renderTileEntityAt(remote, 0, 0, 0, partialTicks);
                } catch (Exception ex) {
                    FMLLog.warning("Failed to render " + remote.getClass().getSimpleName() + ". Reason: " + ex.getLocalizedMessage());
                }
            }

            GlStateManager.popMatrix();
        }
    }
}
