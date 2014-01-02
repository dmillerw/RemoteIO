package com.dmillerw.remoteIO.inventory.gui;

import com.dmillerw.remoteIO.api.documentation.DocumentationRegistry;
import com.dmillerw.remoteIO.inventory.ContainerDocumentation;
import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiDocumentation extends GuiContainer {

    public static final int SCREEN_WIDTH = 216;
    public static final int LINE_MAX = 9;

    public List<String> currentDocumentation;

    private ContainerDocumentation container;

	private EntityPlayer player;

	public GuiDocumentation(EntityPlayer player) {
		super(new ContainerDocumentation(player));

        this.container = (ContainerDocumentation) this.inventorySlots;
		this.player = player;

        this.xSize = 252;
        this.ySize = 202;
	}

    @Override
    public void updateScreen() {
        if (container.inventoryChanged && container.holdSlot.getStackInSlot(0) != null) {
            this.currentDocumentation = this.fontRenderer.listFormattedStringToWidth(DocumentationRegistry.getDocumentation(container.holdSlot.getStackInSlot(0)), SCREEN_WIDTH - this.fontRenderer.getCharWidth('-'));
            container.maxOffset = Math.max(0, this.currentDocumentation.size() - LINE_MAX);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        int wheel = Mouse.getDWheel();

        if (wheel != 0) {
            if (wheel > 0) {
                wheel = -1;
            } else if (wheel < 0) {
                wheel = 1;
            }
        }

        container.offset(wheel);

        super.drawScreen(mouseX, mouseY, partial);
    }

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        ItemStack stack = container.holdSlot.getStackInSlot(0);

        if (stack != null) {
            // STATIC TEXT
            this.fontRenderer.drawSplitString(stack.getDisplayName(), 10, 10, SCREEN_WIDTH, 0xFFFFFF);
            String LINE_BREAK = "";
            for (int i=0; i<100; i++) {
                LINE_BREAK = LINE_BREAK + "-";
            }
            this.fontRenderer.drawString(this.fontRenderer.trimStringToWidth(LINE_BREAK, SCREEN_WIDTH - this.fontRenderer.getCharWidth('-')), 10, 10 + this.fontRenderer.FONT_HEIGHT, 0xFFFFFF);

            // RELATIVE TEXT (can be scrolled)
            int beginX = 10;
            int beginY = 10 + (this.fontRenderer.FONT_HEIGHT * 2);

            for (int i=0; i<Math.min(LINE_MAX, this.currentDocumentation.size()); i++) {
                this.fontRenderer.drawString(currentDocumentation.get(i + container.offsetValue), beginX, beginY + (this.fontRenderer.FONT_HEIGHT * i), 0xFFFFFF);
            }
        }
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/documentation.png"));
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);		
	}

}
