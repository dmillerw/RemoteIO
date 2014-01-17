package com.dmillerw.remoteIO.inventory.gui;

import com.dmillerw.remoteIO.inventory.ContainerNull;
import com.dmillerw.remoteIO.inventory.gui.documentation.BreadcrumbHandler;
import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiDocumentation extends GuiContainer {

    public static final int SCREEN_WIDTH = 216;
    public static final int LINE_MAX = 9;

    public static final int STATE_HOME = 0;
    public static final int STATE_CATEGORY = 1;
    public static final int STATE_DOC = 2;

    public int currentState = 0;

    public List<String> currentDocumentation;

    private BreadcrumbHandler breadcrumbs;

	private EntityPlayer player;

	public GuiDocumentation(EntityPlayer player) {
		super(new ContainerNull());

        this.breadcrumbs = new BreadcrumbHandler();
		this.player = player;

        this.xSize = 230;
        this.ySize = 202;
	}

    @Override
    public void updateScreen() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        // Mouse scrolling
        int wheel = Mouse.getDWheel();

        if (wheel != 0) {
            if (wheel > 0) {
                wheel = -1;
            } else if (wheel < 0) {
                wheel = 1;
            }
        }

        // Offset

        super.drawScreen(mouseX, mouseY, partial);
    }

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRenderer.drawSplitString(this.breadcrumbs.formatBreadcrumbs(), 10, 10, SCREEN_WIDTH, 0xFFFFFF);
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
