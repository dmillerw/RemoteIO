package com.dmillerw.remoteIO.inventory.gui;

import com.dmillerw.remoteIO.block.tile.TileIOCore;
import com.dmillerw.remoteIO.inventory.ContainerUpgrade;
import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiUpgrade extends GuiContainer {

	private EntityPlayer player;
	
    private final TileIOCore tile;

	private final String tag;
	
	public GuiUpgrade(EntityPlayer player, TileIOCore tile, int machineType, String tag) {
		super(new ContainerUpgrade(player, tile, machineType));
		
		this.player = player;
        this.tile = tile;
		this.tag = tag;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String tag = I18n.getString(this.tag);
        String inv = I18n.getString("container.inventory");

		this.fontRenderer.drawString(I18n.getString(tag), this.xSize / 2 - (this.fontRenderer.getStringWidth(tag) / 2), 6, 4210752);
		this.fontRenderer.drawString(I18n.getString(inv), this.xSize / 2 - (this.fontRenderer.getStringWidth(inv) / 2), this.ySize - 96 + 2, 4210752);

        this.fontRenderer.drawString("Fuel: " + tile.fuelHandler.fuelLevel, 8, 55 - this.fontRenderer.FONT_HEIGHT, 4210752);
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
