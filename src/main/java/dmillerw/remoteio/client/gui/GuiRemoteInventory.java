package dmillerw.remoteio.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import dmillerw.remoteio.client.gui.button.GuiButtonCustom;
import dmillerw.remoteio.core.helper.MatrixHelper;
import dmillerw.remoteio.inventory.container.ContainerRemoteInventory;
import dmillerw.remoteio.inventory.container.core.ContainerIO;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.tile.TileRemoteInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
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
public class GuiRemoteInventory extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/upgrade.png");

	private final TileRemoteInventory tile;

	private GuiButton access;

	public GuiRemoteInventory(InventoryPlayer inventoryPlayer, TileRemoteInventory tile) {
		super(new ContainerRemoteInventory(inventoryPlayer, tile));

		this.xSize = 196;
		this.ySize = 243;

		this.tile = tile;
	}

	@Override
	public void initGui() {
		super.initGui();

		this.buttonList.add(access = new GuiButtonCustom(0, 83, 21, 34, 18, "").setTexture(TEXTURE).setNormalUV(196, 0).setHighlightUV(196, 18).setOffset(guiLeft, guiTop));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		switch (tile.accessType) {
			case 0: access.displayString = "INV"; break;
			case 1: access.displayString = "ARMOR"; break;
			default: access.displayString = ""; break;
		}
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

		for (Object obj : this.buttonList) {
			((GuiButton)obj).drawButton(Minecraft.getMinecraft(), x, y);
		}
	}

}
