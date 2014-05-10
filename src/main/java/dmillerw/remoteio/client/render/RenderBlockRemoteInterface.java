package dmillerw.remoteio.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dmillerw.remoteio.block.BlockRemoteInterface;
import dmillerw.remoteio.block.tile.TileRemoteInterface;
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
		tessellator.addTranslation(0, -0.0625F * 2, 0);
		tessellator.setNormal(0, 1, 0);
		renderer.renderFaceYPos(block, 0, 0, 0, inactive);
		tessellator.setNormal(0, -1, 0);
		renderer.renderFaceYNeg(block, 0, 0, 0, inactive);
		tessellator.setNormal(1, 0, 0);
		renderer.renderFaceXPos(block, 0, 0, 0, inactive);
		tessellator.setNormal(-1, 0, 0);
		renderer.renderFaceXNeg(block, 0, 0, 0, inactive);
		tessellator.setNormal(0, 0, 1);
		renderer.renderFaceZPos(block, 0, 0, 0, inactive);
		tessellator.setNormal(0, 0, -1);
		renderer.renderFaceZNeg(block, 0, 0, 0, inactive);
		tessellator.addTranslation(0, 0.0625F * 2, 0);
		tessellator.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

		if (tile != null) {
			if (tile.remotePosition == null || !tile.remotePosition.inWorld(FMLClientHandler.instance().getWorldClient()) || tile.visualState != TileRemoteInterface.VisualState.REMOTE_CAMO || tile.camoRenderLock) {
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
