package remoteio.client.render;

import remoteio.common.tile.TileTransceiver;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class RenderTileTransceiver extends TileEntitySpecialRenderer {

    public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(new ResourceLocation("remoteio:models/transceiver.obj"));

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partial) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);

        switch (((TileTransceiver)tileEntity).orientation) {
            case UP: {
                GL11.glRotated(180, 1, 0, 0);
                GL11.glTranslated(0, -1, 0);
                break;
            }

            case NORTH: {
                GL11.glRotated(90, 1, 0, 0);
                GL11.glTranslated(0, -0.5, -0.5);
                break;
            }

            case SOUTH: {
                GL11.glRotated(-90, 1, 0, 0);
                GL11.glTranslated(0, -0.5, 0.5);
                break;
            }

            case WEST: {
                GL11.glRotated(-90, 0, 0, 1);
                GL11.glTranslated(-0.5, -0.5, 0);
                break;
            }

            case EAST: {
                GL11.glRotated(90, 0, 0, 1);
                GL11.glTranslated(0.5, -0.5, 0);
                break;
            }
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        MODEL.renderAll();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
    }
}
