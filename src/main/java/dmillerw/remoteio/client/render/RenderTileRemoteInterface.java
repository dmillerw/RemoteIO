package dmillerw.remoteio.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import dmillerw.remoteio.block.core.BlockIOCore;
import dmillerw.remoteio.client.helper.IORenderHelper;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.VisualState;
import dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class RenderTileRemoteInterface extends TileEntitySpecialRenderer {

    private RenderBlocks renderBlocks;

    private static boolean shouldRender(VisualState visualState) {
        return visualState == VisualState.CAMOUFLAGE_REMOTE || visualState == VisualState.CAMOUFLAGE_BOTH;
    }

    public void renderRemoteInterfaceAt(TileRemoteInterface tile, double x, double y, double z, float partial) {
        if (tile.remotePosition != null && tile.remotePosition.inWorld(tile.getWorldObj()) && tile.visualState.isCamouflage()) {
            WorldClient worldClient = FMLClientHandler.instance().getWorldClient();
            DimensionalCoords there = tile.remotePosition;

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

            GL11.glPushMatrix();

            GL11.glTranslated(x, y, z);
            GL11.glTranslated(0.5, 0.5, 0.5);
            GL11.glRotated(90 * tile.rotationY, 0, 1, 0);
            GL11.glTranslated(-0.5, -0.5, -0.5);

            TileEntity remoteTile = there.getTileEntity(worldClient);

            if (remoteTile != null) {
                try {
                    TileEntityRendererDispatcher.instance.renderTileEntityAt(tile.remotePosition.getTileEntity(worldClient), 0, 0, 0, partial);
                } catch (Exception ex) {
                    FMLLog.warning("Failed to render " + tile.remotePosition.getTileEntity(worldClient).getClass().getSimpleName() + ". Reason: " + ex.getLocalizedMessage());

                    // Maybe bring this back if becomes an issue
//					tile.camoRenderLock = true;
                    tile.markForRenderUpdate();
                }
            }

            GL11.glPopMatrix();
        } else {
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
    }

    @Override
    public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
        renderRemoteInterfaceAt((TileRemoteInterface) var1, var2, var4, var6, var8);
    }

    @Override
    public void func_147496_a(World world) {
        renderBlocks = new RenderBlocks(world);
    }
}
