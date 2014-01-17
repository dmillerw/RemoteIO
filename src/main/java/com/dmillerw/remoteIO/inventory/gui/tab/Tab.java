package com.dmillerw.remoteIO.inventory.gui.tab;

import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class Tab {

    protected TabManager manager;

    protected final String uniqueID;

    private boolean open;

    public int currentShiftX = 0;
    public int currentShiftY = 0;

    protected int overlayColor = 0xffffff;
    protected int maxWidth = 124;
    protected int minWidth = 24;
    protected int currentWidth = minWidth;
    protected int maxHeight = 24;
    protected int minHeight = 24;
    protected int currentHeight = minHeight;

    public Tab(String uuid) {
        this.uniqueID = uuid;
    }

    public void update() {
        if (open && currentWidth < maxWidth) {
            currentWidth += 4;
        } else if (!open && currentWidth > minWidth) {
            currentWidth -= 4;
        }

        if (open && currentHeight < maxHeight) {
            currentHeight += 4;
        } else if (!open && currentHeight > minHeight) {
            currentHeight -= 4;
        }
    }

    public abstract void draw(int x, int y);

    public abstract String getTooltip();

    public Icon getIcon() {
        return null;
    }

    public boolean handleMouseClicked(int x, int y, int mouseButton) {
        return false;
    }

    public boolean intersectsWith(int mouseX, int mouseY, int shiftX, int shiftY) {
        if (mouseX >= shiftX && mouseX <= shiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + currentHeight) {
            return true;
        }

        return false;
    }

    public void setFullyOpen() {
        open = true;
        currentWidth = maxWidth;
        currentHeight = maxHeight;
    }

    public void toggleOpen() {
        if (open) {
            open = false;
            TabManager.clearActiveTab();
        } else {
            open = true;
            TabManager.setActiveTab(this.uniqueID);
        }
    }

    public boolean isVisible() {
        return true;
    }

    public boolean isOpen() {
        return this.open;
    }

    protected boolean isFullyOpened() {
        return currentWidth >= maxWidth;
    }

    protected void drawBackground(int x, int y) {
        float red = (float) (overlayColor >> 16 & 255) / 255.0F;
        float green = (float) (overlayColor >> 8 & 255) / 255.0F;
        float blue = (float) (overlayColor & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, 1.0F);

        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/tab.png"));

        ((Gui)manager.parentGui).drawTexturedModalRect(x, y, 0, 256 - currentHeight, 4, currentHeight);
        ((Gui)manager.parentGui).drawTexturedModalRect(x + 4, y, 256 - currentWidth + 4, 0, currentWidth - 4, 4);
        ((Gui)manager.parentGui).drawTexturedModalRect(x, y, 0, 0, 4, 4);
        ((Gui)manager.parentGui).drawTexturedModalRect(x + 4, y + 4, 256 - currentWidth + 4, 256 - currentHeight + 4, currentWidth - 4, currentHeight - 4);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
    }

    protected void drawIcon(Icon icon, int x, int y) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
        ((Gui)manager.parentGui).drawTexturedModelRectFromIcon(x, y, icon, 16, 16);
    }

}