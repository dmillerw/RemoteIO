package remoteio.client.gui;

import remoteio.common.inventory.InventoryItem;
import remoteio.common.inventory.container.ContainerSimpleCamo;
import remoteio.common.lib.ModInfo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class GuiSimpleCamo extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/simple_camo.png");

    public GuiSimpleCamo(EntityPlayer player, InventoryItem inventory) {
        super(new ContainerSimpleCamo(player, inventory));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}