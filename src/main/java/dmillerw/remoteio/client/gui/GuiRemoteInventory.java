package dmillerw.remoteio.client.gui;

import dmillerw.remoteio.client.gui.button.GuiButtonCustom;
import dmillerw.remoteio.inventory.container.ContainerRemoteInventory;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.tile.TileRemoteInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class GuiRemoteInventory extends GuiContainer {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/upgrade.png");

    private final TileRemoteInventory tile;

    public GuiRemoteInventory(InventoryPlayer inventoryPlayer, TileRemoteInventory tile) {
        super(new ContainerRemoteInventory(inventoryPlayer, tile));

        this.xSize = 196;
        this.ySize = 243;

        this.tile = tile;
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
    }
}
