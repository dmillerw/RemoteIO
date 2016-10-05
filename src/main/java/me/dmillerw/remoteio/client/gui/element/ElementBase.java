package me.dmillerw.remoteio.client.gui.element;

import me.dmillerw.remoteio.client.gui.core.GuiBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Created by dmillerw
 */
public class ElementBase {

    public final String elementId;

    protected GuiBase parentGui;

    protected int xPosition;
    protected int yPosition;
    protected int width;
    protected int height;

    protected float zLevel;

    public ElementBase setZLevel(float zLevel) {
        this.zLevel = zLevel;
        return this;
    }

    public ElementBase(GuiBase parentGui, String elementId, int xPosition, int yPosition, int width, int height) {
        this.parentGui = parentGui;
        this.elementId = elementId;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
    }

    /* CALLBACKS */
    public void drawElement(int mouseX, int mouseY, float partialTicks) {
    }

    public void onKeyTyped(int keycode, char charTyped) {
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean intersects = mouseIntersectsSelf(mouseX, mouseY);
        if (intersects) {
            parentGui.setFocus(this);
        }
        return intersects;
    }

    /* UTILITY METHODS */
    protected boolean mouseIntersectsSelf(int mouseX, int mouseY) {
        return mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height;
    }

    protected void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, 1F);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos((double) left, (double) bottom, zLevel).endVertex();
        vertexbuffer.pos((double) right, (double) bottom, zLevel).endVertex();
        vertexbuffer.pos((double) right, (double) top, zLevel).endVertex();
        vertexbuffer.pos((double) left, (double) top, zLevel).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
