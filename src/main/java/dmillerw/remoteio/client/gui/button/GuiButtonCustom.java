package dmillerw.remoteio.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class GuiButtonCustom extends GuiButton {

    protected ResourceLocation texture;

    protected int offsetX;
    protected int offsetY;

    protected int uNormal;
    protected int vNormal;
    protected int uHighlight;
    protected int vHighlight;

    public GuiButtonCustom(int id, int x, int y, int w, int h) {
        super(id, x, y, w, h, "");
    }

    public GuiButtonCustom(int id, int x, int y, int w, int h, String text) {
        super(id, x, y, w, h, text);
    }

    public GuiButtonCustom setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public GuiButtonCustom setNormalUV(int u, int v) {
        this.uNormal = u;
        this.vNormal = v;
        return this;
    }

    public GuiButtonCustom setHighlightUV(int u, int v) {
        this.uHighlight = u;
        this.vHighlight = v;
        return this;
    }

    public GuiButtonCustom setOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
        return this;
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        if (visible) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = x >= offsetX + this.xPosition && y >= offsetY + this.yPosition && x < offsetX + this.xPosition + this.width && y < offsetY + this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

            if (k == 1) { // Default
                this.drawTexturedModalRect(offsetX + this.xPosition, offsetY + this.yPosition, uNormal, vNormal, this.width, this.height);
            } else if (k == 2) { // Hover
                this.drawTexturedModalRect(offsetX + this.xPosition, offsetY + this.yPosition, uHighlight, vHighlight, this.width, this.height);
            }

            this.mouseDragged(mc, x, y);

            if (!displayString.isEmpty()) {
                int l = 14737632;

                if (!this.enabled) {
                    l = 10526880;
                } else if (this.field_146123_n) {
                    l = 16777120;
                }

                this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, this.displayString, offsetX + this.xPosition + this.width / 2, offsetY + this.yPosition + (this.height - 8) / 2, l);
            }
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int x, int y) {
        return this.enabled && this.visible && x >= offsetX + this.xPosition && y >= offsetY + this.yPosition && x < offsetX + this.xPosition + this.width && y < offsetY + this.yPosition + this.height;
    }
}
