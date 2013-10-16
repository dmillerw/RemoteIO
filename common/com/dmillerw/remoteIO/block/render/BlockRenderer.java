package com.dmillerw.remoteIO.block.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

public class BlockRenderer {

	protected static float W1 = 0.0625F;
	protected static float W2 = 0.125F;
	protected static float W3 = 0.1875F;
	protected static float W4 = 0.25F;
	protected static float W5 = 0.3125F;
	protected static float W6 = 0.375F;
	protected static float W7 = 0.4375F;
	protected static float W8 = 0.5F;
	protected static float W9 = 0.5625F;
	protected static float W10 = 0.625F;
	protected static float W11 = 0.6875F;
	protected static float W12 = 0.75F;
	protected static float W13 = 0.8125F;
	protected static float W15 = 0.9375F;

	public static void drawFaces(RenderBlocks renderblocks, Block block, int metadata, boolean solidTop) {
		Icon[] icons = new Icon[ForgeDirection.VALID_DIRECTIONS.length];
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			icons[dir.ordinal()] = block.getIcon(dir.ordinal(), metadata);
		}
		drawFaces(renderblocks, block, icons[1], icons[0], icons[3], icons[5], icons[2], icons[4], solidTop);
	}

	public static void drawFaces(RenderBlocks renderblocks, Block block, Icon icon, boolean st) {
		drawFaces(renderblocks, block, icon, icon, icon, icon, icon, icon, st);
	}

	public static void drawFaces(RenderBlocks renderblocks, Block block, Icon front, Icon back, Icon side, boolean st) {
		drawFaces(renderblocks, block, side, side, front, back, side, side, st);
	}

	public static void drawFaces(RenderBlocks renderblocks, Block block, Icon i1, Icon i2, Icon i3, Icon i4, Icon i5, Icon i6, boolean solidtop) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		if (i1 != null) {
			renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, i1);
		}
		tessellator.draw();
		if (solidtop)
			GL11.glDisable(GL11.GL_ALPHA_TEST);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		if (i2 != null) {
			renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, i2);
		}
		tessellator.draw();
		if (solidtop)
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		if (i3 != null) {
			renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, i3);
		}
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		if (i4 != null) {
			renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, i4);
		}
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		if (i5 != null) {
			renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, i5);
		}
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		if (i6 != null) {
			renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, i6);
		}
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	protected static int setBrightness(IBlockAccess blockAccess, int i, int j, int k, Block block) {
		Tessellator tessellator = Tessellator.instance;
		int mb = block.getMixedBrightnessForBlock(blockAccess, i, j, k);
		tessellator.setBrightness(mb);

		float f = 1.0F;

		int l = block.colorMultiplier(blockAccess, i, j, k);
		float f1 = (l >> 16 & 0xFF) / 255.0F;
		float f2 = (l >> 8 & 0xFF) / 255.0F;
		float f3 = (l & 0xFF) / 255.0F;
		if (EntityRenderer.anaglyphEnable) {
			float f6 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
			float f4 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
			float f7 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
			f1 = f6;
			f2 = f4;
			f3 = f7;
		}
		tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
		return mb;
	}

	protected static void renderSide(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer, Icon tex, ForgeDirection side) {
		renderSide(world, x, y, z, block, renderer, tex, side, true);
	}

	protected static void renderSide(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer, Icon tex, ForgeDirection side, boolean allsides) {
		switch (side) {
			case EAST: {
				if ((allsides) || (block.shouldSideBeRendered(world, x + 1, y, z, 6))) {
					renderer.renderFaceXNeg(block, x, y, z, tex);
				}
				break;
			}

			case WEST: {
				if ((allsides) || (block.shouldSideBeRendered(world, x - 1, y, z, 6))) {
					renderer.renderFaceXPos(block, x, y, z, tex);
				}
				break;
			}
			case NORTH: {
				if ((allsides) || (block.shouldSideBeRendered(world, x, y, z - 1, 6))) {
					renderer.renderFaceZPos(block, x, y, z, tex);
				}
				break;
			}

			case SOUTH: {
				if ((allsides) || (block.shouldSideBeRendered(world, x, y, z + 1, 6))) {
					renderer.renderFaceZNeg(block, x, y, z, tex);
				}
				break;
			}
			case UP: {
				if ((allsides) || (block.shouldSideBeRendered(world, x, y + 1, z, 6))) {
					renderer.renderFaceYNeg(block, x, y, z, tex);
				}
				break;
			}
			case DOWN: {
				if ((allsides) || (block.shouldSideBeRendered(world, x, y - 1, z, 6))) {
					renderer.renderFaceYPos(block, x, y, z, tex);
				}
				break;
			}
			default:
				break;
		}
	}

	protected static void renderAllSides(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer, Icon tex) {
		renderAllSides(world, x, y, z, block, renderer, tex, true);
	}

	protected static void renderAllSides(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer, Icon tex, boolean allsides) {
		if ((allsides) || (block.shouldSideBeRendered(world, x + 1, y, z, 6)))
			renderer.renderFaceXNeg(block, x, y, z, tex);
		if ((allsides) || (block.shouldSideBeRendered(world, x - 1, y, z, 6)))
			renderer.renderFaceXPos(block, x, y, z, tex);
		if ((allsides) || (block.shouldSideBeRendered(world, x, y, z + 1, 6)))
			renderer.renderFaceZNeg(block, x, y, z, tex);
		if ((allsides) || (block.shouldSideBeRendered(world, x, y, z - 1, 6)))
			renderer.renderFaceZPos(block, x, y, z, tex);
		if ((allsides) || (block.shouldSideBeRendered(world, x, y + 1, z, 6)))
			renderer.renderFaceYNeg(block, x, y, z, tex);
		if ((allsides) || (block.shouldSideBeRendered(world, x, y - 1, z, 6)))
			renderer.renderFaceYPos(block, x, y, z, tex);
	}

	protected static void renderAllSidesInverted(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer, Icon tex, boolean allsides) {
		if ((allsides) || (!block.shouldSideBeRendered(world, x - 1, y, z, 6)))
			renderer.renderFaceXNeg(block, x - 1, y, z, tex);
		if ((allsides) || (!block.shouldSideBeRendered(world, x + 1, y, z, 6)))
			renderer.renderFaceXPos(block, x + 1, y, z, tex);
		if ((allsides) || (!block.shouldSideBeRendered(world, x, y, z - 1, 6)))
			renderer.renderFaceZNeg(block, x, y, z - 1, tex);
		if ((allsides) || (!block.shouldSideBeRendered(world, x, y, z + 1, 6)))
			renderer.renderFaceZPos(block, x, y, z + 1, tex);
		if ((allsides) || (!block.shouldSideBeRendered(world, x, y - 1, z, 6)))
			renderer.renderFaceYNeg(block, x, y - 1, z, tex);
		if ((allsides) || (!block.shouldSideBeRendered(world, x, y + 1, z, 6)))
			renderer.renderFaceYPos(block, x, y + 1, z, tex);
	}

	protected static void renderAllSides(int x, int y, int z, Block block, RenderBlocks renderer, Icon tex) {
		renderer.renderFaceXNeg(block, x - 1, y, z, tex);
		renderer.renderFaceXPos(block, x + 1, y, z, tex);
		renderer.renderFaceZNeg(block, x, y, z - 1, tex);
		renderer.renderFaceZPos(block, x, y, z + 1, tex);
		renderer.renderFaceYNeg(block, x, y - 1, z, tex);
		renderer.renderFaceYPos(block, x, y + 1, z, tex);
	}
}