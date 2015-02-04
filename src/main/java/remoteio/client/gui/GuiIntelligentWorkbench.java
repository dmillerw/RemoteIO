package remoteio.client.gui;

import remoteio.client.gui.button.GuiBetterButton;
import remoteio.common.inventory.container.ContainerIntelligentWorkbench;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class GuiIntelligentWorkbench extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/crafting_table.png");

    private GuiBetterButton buttonPrevious;
    private GuiBetterButton buttonNext;

    private int recipeIndex = 0;

    public GuiIntelligentWorkbench(InventoryPlayer inventoryPlayer, World world, int x, int y, int z) {
        super(new ContainerIntelligentWorkbench(inventoryPlayer, world, x, y, z));
    }

    public void initGui() {
        super.initGui();

        buttonList.add(buttonPrevious = new GuiBetterButton(0, width / 2 + 31, height / 2 - 26, 12, 12, "<"));
        buttonList.add(buttonNext = new GuiBetterButton(1, width / 2 + 45, height / 2 - 26, 12, 12, ">"));
    }

    @Override
    public void updateScreen() {
        if (((ContainerIntelligentWorkbench)inventorySlots).resultChanged) {
            recipeIndex = 0;
            ((ContainerIntelligentWorkbench)inventorySlots).resultChanged = false;
        }

        if (recipeIndex <= 0) {
            buttonPrevious.enabled = false;
        } else {
            buttonPrevious.enabled = true;
        }

        if (recipeIndex >= ((ContainerIntelligentWorkbench)inventorySlots).resultCount - 1) {
            buttonNext.enabled = false;
        } else {
            buttonNext.enabled = true;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        if (button.id == 0) {
            recipeIndex--;
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, -1);
        } else if (button.id == 1) {
            recipeIndex++;
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
        }

        if (recipeIndex <= 0) {
            buttonPrevious.enabled = false;
        } else {
            buttonPrevious.enabled = true;
        }

        if (recipeIndex >= ((ContainerIntelligentWorkbench)inventorySlots).resultCount) {
            buttonNext.enabled = false;
        } else {
            buttonNext.enabled = true;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) {
        this.fontRendererObj.drawString(I18n.format("container.crafting"), 28, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partial, int mx, int my) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    }
}
