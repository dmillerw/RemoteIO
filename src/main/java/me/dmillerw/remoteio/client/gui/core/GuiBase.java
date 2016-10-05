package me.dmillerw.remoteio.client.gui.core;

import com.google.common.collect.Maps;
import me.dmillerw.remoteio.client.gui.element.ElementBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.Map;

/**
 * Created by dmillerw
 */
public class GuiBase extends GuiScreen {

    private Map<String, ElementBase> guiElements = Maps.newHashMap();

    private String focusedElement = "";

    protected int xSize = 256;
    protected int ySize = 256;
    protected int guiLeft;
    protected int guiTop;

    protected final <T extends ElementBase> T registerElement(T element) {
        guiElements.put(element.elementId, element);
        return element;
    }

    public ElementBase getFocusedElement() {
        return focusedElement.isEmpty() ? null : guiElements.get(focusedElement);
    }

    public void setFocus(ElementBase element) {
        this.focusedElement = element != null ? element.elementId : "";
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;

        guiElements.clear();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (ElementBase element : guiElements.values())
            element.drawElement(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        ElementBase element = guiElements.get(focusedElement);
        if (element != null)
            element.onKeyTyped(keyCode, typedChar);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        for (ElementBase element : guiElements.values())
            if (element.onMouseClicked(mouseX, mouseY, button)) return;

        setFocus(null);
    }

    /* PASSTHROUGH GETTERS */
    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }
}
