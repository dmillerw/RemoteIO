package me.dmillerw.remoteio.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import me.dmillerw.remoteio.client.gui.core.GuiBase;
import me.dmillerw.remoteio.client.gui.element.ElementTextBox;
import me.dmillerw.remoteio.lib.ModInfo;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by dmillerw
 */
public class GuiFrequency extends GuiBase {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ModInfo.MOD_ID + ":textures/gui/frequency.png");

    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 166;

    private static final int LIST_WIDTH = 240;
    private static final int LIST_HEIGHT = 128;

    private static final int FREQUENCY_X = 9;
    private static final int FREQUENCY_Y = 9;
    private static final int FREQUENCY_WIDTH = 36;
    private static final int FREQUENCY_HEIGHT = 14;

    private static final int NAME_X = 53;
    private static final int NAME_Y = 9;
    private static final int NAME_WIDTH = 150;
    private static final int NAME_HEIGHT = 14;

    public static Map<Integer, String> frequencies = Maps.newHashMap();

    private ElementTextBox frequencyField;
    private ElementTextBox nameField;

    public GuiFrequency() {
        this.xSize = GUI_WIDTH;
        this.ySize = GUI_HEIGHT;
        this.zLevel = 0.0F;
    }

    public void initGui() {
        super.initGui();

        frequencyField = registerElement(new ElementTextBox(this, "frequency", guiLeft + FREQUENCY_X, guiTop + FREQUENCY_Y, FREQUENCY_WIDTH, FREQUENCY_HEIGHT));
        frequencyField.setPlaceholder("0");
        frequencyField.setTextAlignment(ElementTextBox.Alignment.CENTER);
        frequencyField.setFocusedBackgroundColor(0x0d0d39);
        frequencyField.setTextOffset(0, 4);
        frequencyField.setValidator(new Predicate<Character>() {
            @Override
            public boolean apply(@Nullable Character input) {
                return Character.isDigit(input);
            }
        });

        nameField = registerElement(new ElementTextBox(this, "name", guiLeft + NAME_X, guiTop + NAME_Y, NAME_WIDTH, NAME_HEIGHT));
        nameField.setPlaceholder("Name...");
        nameField.setFocusedBackgroundColor(0x0d0d39);
        nameField.setTextOffset(2, 4);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.renderEngine.bindTexture(GUI_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        int i = 0;
        for (Map.Entry<Integer, String> entry : frequencies.entrySet()) {
            fontRendererObj.drawString(entry.getValue(), guiLeft + 9, guiTop + 31 + ((fontRendererObj.FONT_HEIGHT * i) + 2), 0xFFFFFF);
            i++;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
