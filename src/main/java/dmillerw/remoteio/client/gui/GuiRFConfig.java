package dmillerw.remoteio.client.gui;

import dmillerw.remoteio.client.gui.button.GuiBetterButton;
import dmillerw.remoteio.client.helper.TextFormatter;
import dmillerw.remoteio.inventory.container.ContainerNull;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.network.PacketHandler;
import dmillerw.remoteio.network.packet.PacketServerApplyRFConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;

/**
 * @author dmillerw
 */
public class GuiRFConfig extends GuiContainer {

    private static final ResourceLocation GUI_BLANK = new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/blank.png");

    private final ItemStack itemStack;

    public GuiBetterButton buttonDec;
    public GuiBetterButton buttonInc;

    public GuiTextField textFieldRate;

    public int maxPushRate = 0;

    public GuiRFConfig(ItemStack itemStack) {
        super(new ContainerNull());
        this.itemStack = itemStack;
    }

    private int getMaxPushRate() {
        String text = textFieldRate.getText();
        if (text != null && !text.isEmpty()) {
            try {
                return Integer.valueOf(text);
            } catch (NumberFormatException ex) {
                return 0;
            }
        }
        return 0;
    }

    public void initGui() {
        super.initGui();

        buttonList.add(buttonDec = new GuiBetterButton(0, guiLeft + 107, guiTop + 19, 12, 12, "-"));
        buttonList.add(buttonInc = new GuiBetterButton(1, guiLeft + 121, guiTop + 19, 12, 12, "+"));
        textFieldRate = new GuiTextField(mc.fontRenderer, 5, 20, 100, 10);
        textFieldRate.setFocused(true);
        textFieldRate.setCanLoseFocus(false);
        if (itemStack.hasTagCompound()) {
            textFieldRate.setText(String.valueOf(itemStack.getTagCompound().getInteger("maxPushRate")));
            maxPushRate = getMaxPushRate();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        textFieldRate.updateCursorCounter();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        Minecraft.getMinecraft().renderEngine.bindTexture(GUI_BLANK);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        GL11.glColor4f(1, 1, 1, 1);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.remoteio.rfconfig"), 5, 5, 4210752);
        fontRendererObj.drawSplitString(StatCollector.translateToLocal("container.remoteio.rfconfig_desc"), 5, 35, 170, 4210752);
        textFieldRate.drawTextBox();
    }

    @Override
    protected void keyTyped(char character, int key) {
        super.keyTyped(character, key);
        if (key == Keyboard.KEY_BACK || Character.isDigit(character)) {
            textFieldRate.textboxKeyTyped(character, key);
            maxPushRate = getMaxPushRate();
        }
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        int rate = GuiScreen.isShiftKeyDown() ? 100 : GuiScreen.isCtrlKeyDown() ? 1 : 10;
        if (guiButton.id == 0) {
            maxPushRate = (Math.max(0, maxPushRate - rate));
            textFieldRate.setText(String.valueOf(maxPushRate));
        } else if (guiButton.id == 1) {
            maxPushRate = (Math.min(1000000, maxPushRate + rate));
            textFieldRate.setText(String.valueOf(maxPushRate));
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        PacketServerApplyRFConfig packetServerApplyRFConfig = new PacketServerApplyRFConfig();
        packetServerApplyRFConfig.maxPushRate = maxPushRate;
        PacketHandler.INSTANCE.sendToServer(packetServerApplyRFConfig);
    }
}
