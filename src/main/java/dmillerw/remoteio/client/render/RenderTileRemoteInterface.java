package dmillerw.remoteio.client.render;

import dmillerw.remoteio.block.tile.TileRemoteInterface;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * @author dmillerw
 */
public class RenderTileRemoteInterface extends TileEntitySpecialRenderer {

	public void renderRemoteInterfaceAt(TileRemoteInterface tile, double x, double y, double z, float partial) {
		if (tile.remotePosition != null && tile.remotePosition.inWorld(tile.getWorldObj()) && tile.visualState == TileRemoteInterface.VisualState.REMOTE_CAMO) {
			TileEntityRendererDispatcher.instance.renderTileEntityAt(tile.remotePosition.getTileEntity(), x, y, z, partial);
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
		renderRemoteInterfaceAt((TileRemoteInterface) var1, var2, var4, var6, var8);
	}

}
