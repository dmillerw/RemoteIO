package dmillerw.remoteio.client.gui;

import dmillerw.remoteio.client.documentation.Documentation;
import dmillerw.remoteio.client.documentation.DocumentationEntry;
import dmillerw.remoteio.client.documentation.IDocumentationPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @author dmillerw
 */
public class GuiDocumentation extends GuiScreen {

    private static final ResourceLocation TEXTURE = new ResourceLocation("remoteio:textures/gui/tablet.png");

    public static final int XSIZE = 142;
    public static final int YSIZE = 180;

    public static final int XPADDING = 10;
    public static final int YPADDING = 10;

    private static final int HOME_X = 65;
    private static final int HOME_Y = 167;
    private static final int HOME_OVER_X = 142;
    private static final int HOME_OVER_Y = 147;
    private static final int HOME_WIDTH = 18;
    private static final int HOME_HEIGHT = 8;

    private Documentation.Category currentCategory = null;
    private DocumentationEntry currentEntry = null;
    private IDocumentationPage currentPage = null;
    private int currentPageIndex = 0;

    private List<IDocumentationPage> entryCache;

    private int guiLeft;
    private int guiTop;

    @Override
    public void initGui() {
        currentCategory = null;
        currentEntry = null;
        currentPage = null;

        this.guiLeft = (this.width - XSIZE) / 2;
        this.guiTop = (this.height - YSIZE) / 2;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (currentPage != null)
            currentPage.updateScreen(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        super.drawScreen(mouseX, mouseY, partial);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, XSIZE, YSIZE);

        if (mouseX >= guiLeft + HOME_X && mouseX <= guiLeft + HOME_X + HOME_WIDTH && mouseY >= guiTop + HOME_Y && mouseY <= guiTop + HOME_Y + HOME_HEIGHT) {
            drawTexturedModalRect(guiLeft + HOME_X, guiTop + HOME_Y, HOME_OVER_X, HOME_OVER_Y, HOME_WIDTH, HOME_HEIGHT);
        }

        if (currentPage != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(guiLeft, guiTop, 0);
            currentPage.renderScreen(this, mouseX, mouseY);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            currentCategory = null;
            currentEntry = null;
            currentPage = null;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
