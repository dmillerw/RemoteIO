package me.dmillerw.remoteio.client.gui;

import com.google.common.base.Predicate;
import me.dmillerw.remoteio.core.frequency.IFrequencyProvider;
import me.dmillerw.remoteio.lib.ModInfo;
import me.dmillerw.remoteio.network.PacketHandler;
import me.dmillerw.remoteio.network.packet.server.SSetFrequency;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUnicodeGlyphButton;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Created by dmillerw
 */
public class GuiFrequencyTemp extends GuiScreen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ModInfo.MOD_ID + ":textures/gui/frequency_temp.png");

    private static final int GUI_WIDTH = 204;
    private static final int GUI_HEIGHT = 32;

    private int guiLeft;
    private int guiTop;

    private IFrequencyProvider provider;
    private int currentFrequency;

    private GuiUnicodeGlyphButton previous;
    private GuiUnicodeGlyphButton next;

    private GuiTextField frequency;

    public GuiFrequencyTemp(IFrequencyProvider provider) {
        this.provider = provider;
        this.currentFrequency = provider.getFrequency();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;

        Keyboard.enableRepeatEvents(true);

        previous = new GuiUnicodeGlyphButton(0, guiLeft + 6, guiTop + 6, 20, 20, "", "\u25C0", 1F);
        next = new GuiUnicodeGlyphButton(1, guiLeft + 31, guiTop + 6, 20, 20, "", "\u25B6", 1F);
        frequency = new GuiTextField(2, mc.fontRendererObj, guiLeft + 56, guiTop + 6, 141, 21);
        frequency.setText(Integer.toString(currentFrequency));
        frequency.setValidator(new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                if (input == null)
                    input = "";

                for (char c : input.toCharArray())
                    if (!Character.isDigit(c)) return false;
                return true;
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.renderEngine.bindTexture(GUI_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        previous.drawButton(mc, mouseX, mouseY);
        next.drawButton(mc, mouseX, mouseY);
        frequency.drawTextBox();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        frequency.mouseClicked(mouseX, mouseY, mouseButton);

        if (previous.mousePressed(mc, mouseX, mouseY)) {
            int f = currentFrequency - 1;
            if (f < 0) f = 0;
            currentFrequency = f;
            frequency.setText(Integer.toString(f));

            previous.playPressSound(mc.getSoundHandler());
            return;
        }

        if (next.mousePressed(mc, mouseX, mouseY)) {
            int f = currentFrequency + 1;
            if (f >= Integer.MAX_VALUE) f = Integer.MAX_VALUE;
            currentFrequency = f;
            frequency.setText(Integer.toString(f));

            next.playPressSound(mc.getSoundHandler());
            return;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }

        frequency.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        Keyboard.enableRepeatEvents(false);

        SSetFrequency packet = new SSetFrequency(Integer.parseInt(frequency.getText()), provider.getPosition());
        PacketHandler.INSTANCE.sendToServer(packet);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
