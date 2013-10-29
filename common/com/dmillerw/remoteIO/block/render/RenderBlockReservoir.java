package com.dmillerw.remoteIO.block.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.dmillerw.remoteIO.block.BlockHeater;
import com.dmillerw.remoteIO.block.BlockReservoir;
import com.dmillerw.remoteIO.block.tile.TileEntityHeater;
import com.dmillerw.remoteIO.block.tile.TileEntityReservoir;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderBlockReservoir extends BlockRenderer implements ISimpleBlockRenderingHandler {

	public static int renderID;

	static {
		renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		block.setBlockBounds(0.1F, 0.1F, 0.1F, 0.9F, 0.9F, 0.9F);
		drawFaces(renderer, block, Block.waterStill.getBlockTextureFromSide(0), true);
		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderer.setRenderBoundsFromBlock(block);
		drawFaces(renderer, block, ((BlockReservoir)block).iconFrame, true);
		drawFaces(renderer, block, ((BlockReservoir)block).iconGlass, true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		TileEntityReservoir tile = (TileEntityReservoir) world.getBlockTileEntity(x, y, z);
		GL11.glColor4f(1, 1, 1, 1);
		if (tile != null && tile.hasWater) {
			setBrightness(world, x, y, z, block);
			block.setBlockBounds(0.02F, 0.02F, 0.02F, 0.98F, 0.98F, 0.98F);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.clearOverrideBlockTexture();
		}
		
		for (int i = 1; i <= 20; i++) {
			final float adjustConstant = 0.001F;
			block.setBlockBounds(0F + (i * adjustConstant), 0F + (i * adjustConstant), 0F + (i * adjustConstant), 1F - (i * adjustConstant), 1F - (i * adjustConstant), 1F - (i * adjustConstant));
			renderer.setRenderBoundsFromBlock(block);
			renderAllSides(world, x, y, z, block, renderer, ((BlockReservoir)block).iconFrame);
		}
		block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
		renderer.setRenderBoundsFromBlock(block);
		renderAllSides(world, x, y, z, block, renderer, ((BlockReservoir)block).iconGlass);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

}
