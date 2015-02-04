package remoteio.client.gui;

import remoteio.client.gui.button.GuiButtonCustom;
import remoteio.common.core.helper.MatrixHelper;
import remoteio.common.inventory.container.ContainerRemoteInterface;
import remoteio.common.lib.ModInfo;
import remoteio.common.tile.TileRemoteInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

/**
 * @author dmillerw
 */
public class GuiRemoteInterface extends GuiContainer {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/upgrade_display.png");

    private final TileRemoteInterface tile;

    private RenderBlocks renderBlocks;

    private Matrix4f initialMatrix;

    public GuiRemoteInterface(InventoryPlayer inventoryPlayer, TileRemoteInterface tile) {
        super(new ContainerRemoteInterface(inventoryPlayer, tile));

        this.xSize = 196;
        this.ySize = 243;

        this.tile = tile;
        this.renderBlocks = new RenderBlocks(tile.getWorldObj());
        this.initialMatrix = MatrixHelper.getRotationMatrix(Minecraft.getMinecraft().renderViewEntity);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(new GuiButtonCustom(0, 39, 106, 17, 18).setTexture(TEXTURE).setNormalUV(196, 54).setHighlightUV(196, 72).setOffset(guiLeft, guiTop));
        this.buttonList.add(new GuiButtonCustom(1, 141, 106, 17, 18).setTexture(TEXTURE).setNormalUV(213, 54).setHighlightUV(213, 72).setOffset(guiLeft, guiTop));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        fontRendererObj.drawString(I18n.format("container.remoteio.transfer"), 16, 4, 4210752);
        String upgrade = I18n.format("container.remoteio.upgrade");
        fontRendererObj.drawString(upgrade, 180 - fontRendererObj.getStringWidth(upgrade), 4, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        if (tile.remotePosition != null) {
            GL11.glPushMatrix();

            double scale = 32;

            //FIXME: This is hard coded for a 32D scale
            GL11.glTranslated(k + (61 + 37.5), l + (71 + 37.5) - (scale / 2), scale);
            GL11.glScaled(scale, -scale, scale);

            MatrixHelper.loadMatrix(initialMatrix);

            GL11.glPushMatrix();

            //TODO Properly allow for rendering even without upgrades
            TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, -0.5, -0.5, -0.5, 0);

            GL11.glPopMatrix();

            GL11.glPopMatrix();
        }

        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            button.drawButton(Minecraft.getMinecraft(), x, y);
        }
    }
}
