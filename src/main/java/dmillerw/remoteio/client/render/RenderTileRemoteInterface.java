package dmillerw.remoteio.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import dmillerw.remoteio.block.tile.TileRemoteInterface;
import dmillerw.remoteio.lib.DimensionalCoords;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
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

	public void renderRemoteInterfaceAt(TileRemoteInterface tile, double x, double y, double z, float partial) {
		if (tile.camoRenderLock) {
			return;
		}

		if (tile.remotePosition != null && tile.remotePosition.inWorld(tile.getWorldObj()) && tile.visualState == TileRemoteInterface.VisualState.REMOTE_CAMO) {
			WorldClient worldClient = FMLClientHandler.instance().getWorldClient();
			DimensionalCoords there = tile.remotePosition;

			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

			GL11.glPushMatrix();

			GL11.glTranslated(x, y, z);
			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glRotated(tile.thetaModifier, 0, 1, 0);
			GL11.glTranslated(-0.5, 0, -0.5);


			GL11.glDisable(GL11.GL_LIGHTING);

			Tessellator.instance.startDrawingQuads();
			Tessellator.instance.setColorOpaque_F(1, 1, 1);
			Tessellator.instance.setTranslation(-tile.xCoord, -tile.yCoord, -tile.zCoord);
			Tessellator.instance.addTranslation(-(there.x - tile.xCoord), -(there.y - tile.yCoord), -(there.z - tile.zCoord));
			renderBlocks.renderBlockAllFaces(tile.getWorldObj().getBlock(there.x, there.y, there.z), there.x, there.y, there.z);
//			renderBlocks.renderBlockAllFaces(Blocks.stonebrick, there.x, there.y, there.z);
			Tessellator.instance.setTranslation(0, 0, 0);
			Tessellator.instance.draw();

			GL11.glEnable(GL11.GL_LIGHTING);

			GL11.glDisable(GL11.GL_DEPTH_TEST);

			try {
				TileEntityRendererDispatcher.instance.renderTileEntityAt(tile.remotePosition.getTileEntity(worldClient), 0, 0, 0, partial);
			} catch (Exception ex) {
				FMLLog.warning("Failed to render " + tile.remotePosition.getTileEntity(worldClient).getClass().getSimpleName() + ". Reason: " + ex.getLocalizedMessage());

				tile.camoRenderLock = true;
				tile.markForRenderUpdate();
			}

			GL11.glEnable(GL11.GL_DEPTH_TEST);

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
