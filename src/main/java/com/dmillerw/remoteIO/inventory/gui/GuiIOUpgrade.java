package com.dmillerw.remoteIO.inventory.gui;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.dmillerw.remoteIO.block.tile.TileIO;
import com.dmillerw.remoteIO.inventory.ContainerIOUpgrade;
import com.dmillerw.remoteIO.lib.ModInfo;

public class GuiIOUpgrade extends GuiContainer {

	private EntityPlayer player;
	
	private TileIO tile;
	
	public GuiIOUpgrade(EntityPlayer player, TileIO tile) {
		super(new ContainerIOUpgrade(player, tile));
		
		this.player = player;
		this.tile = tile;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString(I18n.getString("gui.ioUpgrades"), 8, 6, 4210752);
		this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/ioUpgrades.png"));
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);		
	}

}
