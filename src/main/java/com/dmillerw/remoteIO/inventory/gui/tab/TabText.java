package com.dmillerw.remoteIO.inventory.gui.tab;

import net.minecraft.client.Minecraft;

/**
 * Created by Dylan Miller on 1/17/14
 */
public class TabText extends Tab {

    private String tooltip;
    private String text;

    public TabText(String uuid, String tooltip, String text) {
        super("text_" + uuid);

        this.overlayColor = 0x0000FF;

        this.tooltip = tooltip;
        this.text = text;

        this.maxHeight = Math.max(24, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * (Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(text, maxWidth - 5).size() + 1));
    }

    @Override
    public void draw(int x, int y) {
        Minecraft.getMinecraft().fontRenderer.drawSplitString(text, x + 5, y + 5, maxWidth - 5, 0xFFFFFF);
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }
}
