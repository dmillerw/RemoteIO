package dmillerw.remoteio.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import dmillerw.remoteio.block.tile.TileRemoteInterface;
import dmillerw.remoteio.client.gui.button.GuiButtonCustom;
import dmillerw.remoteio.core.helper.MatrixHelper;
import dmillerw.remoteio.inventory.ContainerRemoteInterface;
import dmillerw.remoteio.lib.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

/**
 * @author dmillerw
 */
public class GuiRemoteInterface extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/upgrade.png");

	private Matrix4f initialMatrix;

	private final RenderBlocks renderBlocks;

	private final TileRemoteInterface tile;

	public GuiRemoteInterface(InventoryPlayer inventoryPlayer, TileRemoteInterface tile) {
		super(new ContainerRemoteInterface(inventoryPlayer, tile));

		this.xSize = 196;
		this.ySize = 243;

		this.initialMatrix = MatrixHelper.getRotationMatrix(Minecraft.getMinecraft().renderViewEntity);
		this.renderBlocks = new RenderBlocks(FMLClientHandler.instance().getWorldClient());
		this.tile = tile;
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

	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		if (tile.remotePosition != null) {
			WorldClient world = FMLClientHandler.instance().getWorldClient();

			GL11.glPushMatrix();

			double scale = 32;

			//FIXME: This is hard coded for a 32D scale
			GL11.glTranslated(k + (61 + 37.5), l + (71 + 37.5) - (scale / 2), scale);
			GL11.glScaled(scale, -scale, scale);

			MatrixHelper.loadMatrix(initialMatrix);

			GL11.glPushMatrix();

			TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, -0.5, -0.5, -0.5, 0);

			GL11.glPopMatrix();

			GL11.glPopMatrix();
		}

		for (int i=0; i<buttonList.size(); i++) {
			GuiButton button = (GuiButton) buttonList.get(i);
			button.drawButton(Minecraft.getMinecraft(), x, y);
		}
	}

}
