package dmillerw.remoteio.client.gui;

import dmillerw.remoteio.client.documentation.Documentation;
import dmillerw.remoteio.client.documentation.DocumentationEntry;
import dmillerw.remoteio.client.documentation.IDocumentationPage;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @author dmillerw
 */
public class GuiDocumentation extends GuiScreen {

    public static final int XSIZE = 0;
    public static final int YSIZE = 0;

    public static final int XPADDING = 10;
    public static final int YPADDING = 10;

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

        if (currentPage != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(guiLeft, guiTop, 0);
            currentPage.renderScreen(this, mouseX, mouseY);
            GL11.glPopMatrix();
        }
    }
}
