package dmillerw.remoteio.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.VisualState;
import dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class RenderTileRemoteInterface extends TileEntitySpecialRenderer {

	private RenderBlocks renderBlocks;

	public static boolean shouldRender(VisualState state) {
		return state == VisualState.CAMOUFLAGE_REMOTE || state == VisualState.CAMOUFLAGE_BOTH;
	}

	public void renderRemoteInterfaceAt(TileRemoteInterface tile, double x, double y, double z, float partial) {
		if (tile.remotePosition != null && tile.remotePosition.inWorld(tile.getWorldObj()) && shouldRender(tile.visualState)) {
			WorldClient worldClient = FMLClientHandler.instance().getWorldClient();
			DimensionalCoords there = tile.remotePosition;

			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

			GL11.glPushMatrix();

			GL11.glTranslated(x, y, z);

			GL11.glTranslated(0.5, 0.5, 0.5);

			GL11.glRotated(90 * tile.rotationY, 0, 1, 0);

			GL11.glTranslated(-0.5, -0.5, -0.5);

			Block remote = there.getBlock(worldClient);
			TileEntity remoteTile = there.getTileEntity(worldClient);

			// Don't call the block's renderer if there's a simple camo chip
			if (tile.visualState == VisualState.CAMOUFLAGE_REMOTE) {
				GL11.glDisable(GL11.GL_LIGHTING);

				Tessellator.instance.startDrawingQuads();
				Tessellator.instance.setColorOpaque_F(1, 1, 1);
				Tessellator.instance.setTranslation(-tile.xCoord, -tile.yCoord, -tile.zCoord);
				Tessellator.instance.addTranslation(-(there.x - tile.xCoord), -(there.y - tile.yCoord), -(there.z - tile.zCoord));

				for (int i=0; i<2; i++) {
					if (i == 1) {
						OpenGlHelper.glBlendFunc(770, 771, 0, 1);
						GL11.glEnable(GL11.GL_BLEND);
					}

					if (remote.canRenderInPass(i)) {
						renderBlocks.renderBlockByRenderType(remote, there.x, there.y, there.z);
					}

					if (i == 1) {
						GL11.glDisable(GL11.GL_BLEND);
					}
				}

				Tessellator.instance.setTranslation(0, 0, 0);
				Tessellator.instance.draw();

				GL11.glEnable(GL11.GL_LIGHTING);
			}

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
