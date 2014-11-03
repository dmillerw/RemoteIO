package dmillerw.remoteio.client.gui;

import dmillerw.remoteio.client.gui.button.GuiBetterButton;
import dmillerw.remoteio.inventory.container.ContainerNull;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.network.PacketHandler;
import dmillerw.remoteio.network.packet.PacketServerApplyRFConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class GuiRFConfig extends GuiContainer {

    private static final ResourceLocation GUI_BLANK = new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/blank.png");

    private final ItemStack itemStack;

    private int maxPushRate;
    private boolean pushPower;

    public GuiBetterButton buttonDec;
    public GuiBetterButton buttonInc;
    public GuiBetterButton buttonToggle;

    public GuiRFConfig(ItemStack itemStack) {
        super(new ContainerNull());
        this.itemStack = itemStack;

        if (itemStack.hasTagCompound()) {
            maxPushRate = itemStack.getTagCompound().getInteger("maxPushRate");
            pushPower = itemStack.getTagCompound().getBoolean("pushPower");
        }
    }

    public void initGui() {
        super.initGui();

        buttonList.add(buttonDec = new GuiBetterButton(0, width / 2 + 31, height / 2 - 26, 12, 12, "-"));
        buttonList.add(buttonInc = new GuiBetterButton(1, width / 2 + 45, height / 2 - 26, 12, 12, "+"));
        buttonList.add(buttonToggle = new GuiBetterButton(2, width / 2 + 45, height / 2, 200, 24, pushPower ? "ON" : "OFF"));
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
        drawString(mc.fontRenderer, Integer.toString(maxPushRate), 5, 5, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        int rate = GuiScreen.isShiftKeyDown() ? 100 : GuiScreen.isCtrlKeyDown() ? 1 : 10;
        if (guiButton.id == 0) {
            maxPushRate = (Math.max(0, maxPushRate - rate));
        } else if (guiButton.id == 1) {
            maxPushRate = (Math.min(10000, maxPushRate + rate));
        } else if (guiButton.id == 2) {
            pushPower = !pushPower;
            buttonToggle.displayString = (pushPower ? "ON" : "OFF");
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        PacketServerApplyRFConfig packetServerApplyRFConfig = new PacketServerApplyRFConfig();
        packetServerApplyRFConfig.pushPower = pushPower;
        packetServerApplyRFConfig.maxPushPower = maxPushRate;
        PacketHandler.INSTANCE.sendToServer(packetServerApplyRFConfig);
    }
}
