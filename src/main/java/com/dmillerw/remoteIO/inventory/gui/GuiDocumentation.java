package com.dmillerw.remoteIO.inventory.gui;

import com.dmillerw.remoteIO.inventory.ContainerDocumentation;
import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiDocumentation extends GuiContainer {

	private EntityPlayer player;

	public GuiDocumentation(EntityPlayer player) {
		super(new ContainerDocumentation(player));
		
		this.player = player;

        this.xSize = 198;
        this.ySize = 166;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {

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
