package com.dmillerw.remoteIO.inventory.gui.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureMap;

import java.util.ArrayList;

public class TabManager {

    public static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    private static String activeTab;

    public static void setActiveTab(String tabUUID) {
        activeTab = tabUUID;
    }

    public static void clearActiveTab() {
        activeTab = null;
    }

    protected ITabbedGUI parentGui;

    protected ArrayList<Tab> tabs = new ArrayList<Tab>();

    public TabManager(ITabbedGUI gui) {
        this.parentGui = gui;
    }

    public void clear() {
        this.tabs.clear();
    }

    public void add(Tab tab) {
        for (Tab other : tabs) {
            if (other.uniqueID.equals(tab.uniqueID)) {
                throw new RuntimeException("Tab already registered with unique ID " + tab.uniqueID);
            }
        }

        this.tabs.add(tab);
        if (activeTab != null && tab.uniqueID.equals(activeTab)) {
            tab.setFullyOpen();
        }

        tab.manager = this;
    }

    public void insert(Tab tab) {
        this.tabs.add(tabs.size() - 1, tab);
    }

    public Tab getTabAtPosition(int mX, int mY) {
        int xShift = ((parentGui.getWidth() - parentGui.getXSize()) / 2) + parentGui.getXSize();
        int yShift = ((parentGui.getHeight() - parentGui.getYSize()) / 2) + 8;

        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);

            if (!tab.isVisible()) {
                continue;
            }

            tab.currentShiftX = xShift;
            tab.currentShiftY = yShift;

            if (tab.intersectsWith(mX, mY, xShift, yShift)) {
                return tab;
            }

            yShift += tab.currentHeight;
        }

        return null;
    }

    public void drawTabs(int mouseX, int mouseY) {
        int xPos = 8;
        for (Tab tab : tabs) {
            tab.update();

            if (!tab.isVisible()) {
                continue;
            }

            tab.drawBackground(parentGui.getXSize(), xPos);

            if (tab.isFullyOpened()) {
                tab.draw(parentGui.getXSize(), xPos);
            } else if (!tab.isOpen()) {
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);

                if (tab.getIcon() != null) {
                    tab.drawIcon(tab.getIcon(), parentGui.getXSize() + 3, xPos + 4);
                }
            }

            xPos += tab.currentHeight;
        }

        Tab tab = getTabAtPosition(mouseX, mouseY);

        if (tab != null) {
            int startX = mouseX - ((parentGui.getWidth() - parentGui.getXSize()) / 2) + 12;
            int startY = mouseY - ((parentGui.getHeight() - parentGui.getYSize()) / 2) - 12;

            String tooltip = tab.getTooltip();
            int textWidth = fontRenderer.getStringWidth(tooltip);

            parentGui.drawGradient(startX - 3, startY - 3, startX + textWidth + 3, startY + 8 + 3, 0xc0000000, 0xc0000000);
            fontRenderer.drawStringWithShadow(tooltip, startX, startY, -1);
        }
    }

    public void handleMouseClicked(int x, int y, int mouseButton) {
        if (mouseButton == 0) {
            Tab tab = this.getTabAtPosition(x, y);

            if (tab != null && !tab.handleMouseClicked(x, y, mouseButton)) {
                for (Tab other : tabs) {
                    if (other != tab && other.isOpen()) {
                        other.toggleOpen();
                    }
                }

                tab.toggleOpen();
            }
        }
    }

}