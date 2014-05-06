package dmillerw.remoteio.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dmillerw.remoteio.block.BlockRemoteInterface;
import dmillerw.remoteio.block.tile.TileRemoteInterface;
import dmillerw.remoteio.lib.DimensionalCoords;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * @author dmillerw
 */
public class RenderBlockRemoteInterface implements ISimpleBlockRenderingHandler {

	public static int renderID;
	static {
		renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		IIcon inactive = ((BlockRemoteInterface)block).icons[0];

		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 1, 0);
		renderer.renderFaceYPos(block, 0, 0, 0, inactive);
		tessellator.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

		if (tile != null) {
			if (tile.remotePosition != null && tile.visualState == TileRemoteInterface.VisualState.REMOTE_CAMO) {
				DimensionalCoords there = tile.remotePosition;
				DimensionalCoords here = DimensionalCoords.create(tile);
				int offsetX = there.x - here.x;
				int offsetY = there.y - here.y;
				int offsetZ = there.z - here.z;

				Tessellator.instance.addTranslation(-offsetX, -offsetY, -offsetZ);
				renderer.renderBlockAllFaces(world.getBlock(there.x, there.y, there.z), there.x, there.y, there.z);
				Tessellator.instance.addTranslation(offsetX, offsetY, offsetZ);
			} else {
				renderer.renderStandardBlock(block, x, y, z);
			}
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

}
