package dmillerw.remoteio.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import dmillerw.remoteio.block.BlockRemoteInterface;
import dmillerw.remoteio.block.core.BlockIOCore;
import dmillerw.remoteio.lib.VisualState;
import dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * @author dmillerw
 */
public class RenderBlockRemoteInterface implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        IIcon inactive = BlockIOCore.icons[0];

        Tessellator tessellator = Tessellator.instance;

        int oldUV = renderer.uvRotateTop;
        renderer.uvRotateTop = 3;

        tessellator.startDrawingQuads();
        tessellator.setNormal(0, 1, 0);
        renderer.renderFaceYPos(block, -0.5, -0.5, -0.5, inactive);
        tessellator.setNormal(0, -1, 0);
        renderer.renderFaceYNeg(block, -0.5, -0.5, -0.5, inactive);
        tessellator.setNormal(1, 0, 0);
        renderer.renderFaceXPos(block, -0.5, -0.5, -0.5, inactive);
        tessellator.setNormal(-1, 0, 0);
        renderer.renderFaceXNeg(block, -0.5, -0.5, -0.5, inactive);
        tessellator.setNormal(0, 0, 1);
        renderer.renderFaceZPos(block, -0.5, -0.5, -0.5, inactive);
        tessellator.setNormal(0, 0, -1);
        renderer.renderFaceZNeg(block, -0.5, -0.5, -0.5, inactive);
        tessellator.draw();

        renderer.uvRotateTop = oldUV;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

        if (tile != null) {
            if (tile.remotePosition == null || !tile.remotePosition.inWorld(tile.getWorldObj()) || tile.visualState != VisualState.CAMOUFLAGE_REMOTE) {
                renderer.renderStandardBlock(block, x, y, z);
            } else {
                if (tile.remotePosition != null && tile.remotePosition.inWorld(tile.getWorldObj())) {
                    Block remoteBlock = tile.remotePosition.getBlock();
                    int rx = tile.remotePosition.x - x;
                    int ry = tile.remotePosition.y - y;
                    int rz = tile.remotePosition.z - z;

                    Tessellator tessellator = Tessellator.instance;

                    if (remoteBlock.getRenderType() == 0) {
                        renderer.renderStandardBlock(block, x, y, z);
                    } else {
                        if (remoteBlock.canRenderInPass(ForgeHooksClient.getWorldRenderPass())) {
                            TessellatorCapture.startCapturing();

                            TessellatorCapture.rotationAngle = 90 * tile.rotationY;
                            TessellatorCapture.offsetX = -(x + rx) - 1;
                            TessellatorCapture.offsetZ = -(z + rz) - 1;

                            tessellator.addTranslation(-rx, -ry, -rz);
                            renderer.renderBlockByRenderType(remoteBlock, tile.remotePosition.x, tile.remotePosition.y, tile.remotePosition.z);
                            tessellator.addTranslation(rx, ry, rz);

                            TessellatorCapture.reset();
                        }
                    }
                }
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
        return BlockRemoteInterface.renderID;
    }
}
