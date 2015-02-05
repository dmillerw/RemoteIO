package remoteio.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import remoteio.client.documentation.Documentation;
import remoteio.client.documentation.DocumentationEntry;
import remoteio.client.documentation.IDocumentationPage;

import java.util.List;

/**
 * @author dmillerw
 */
public class GuiDocumentation
extends GuiScreen {
    private static final ResourceLocation TEXTURE = new ResourceLocation("remoteio:textures/gui/tablet_green.png");

    public static final int BACK_COLOR = 0x304029;
    public static final int TEXT_COLOR = 0x3C5033;
    public static final int TEXT_HIGHLIGHT_COLOR = 0x729A61;

    public static final int XSIZE = 142;
    public static final int YSIZE = 180;

    public static final int XPADDING = 10;
    public static final int YPADDING = 10;

    private static final int SCREEN_X = 11;
    private static final int SCREEN_Y = 9;
    private static final int SCREEN_WIDTH = 122;
    private static final int SCREEN_HEIGHT = 153;

    private static final int HOME_X = 65;
    private static final int HOME_Y = 167;
    private static final int HOME_OVER_X = 142;
    private static final int HOME_OVER_Y = 147;
    private static final int HOME_WIDTH = 18;
    private static final int HOME_HEIGHT = 8;

    private Documentation.Category currentCategory = null;
    private List<DocumentationEntry> categoryCache = null;
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

        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, XSIZE, YSIZE);

        if (mouseX >= guiLeft + HOME_X && mouseX <= guiLeft + HOME_X + HOME_WIDTH && mouseY >= guiTop + HOME_Y && mouseY <= guiTop + HOME_Y + HOME_HEIGHT) {
            drawTexturedModalRect(guiLeft + HOME_X, guiTop + HOME_Y, HOME_OVER_X, HOME_OVER_Y, HOME_WIDTH, HOME_HEIGHT);
        }

        int selection = -1;
        int minY = 0;
        int maxY = 0;
        final int mousePadding = 25;
        final int offset = mc.fontRenderer.FONT_HEIGHT / 2;
        final int standardY = guiTop - offset;
        final int middle = SCREEN_Y + SCREEN_HEIGHT / 2;

        if (currentCategory == null) {
            if (mouseX >= guiLeft + mousePadding && mouseX <= guiLeft + XSIZE - mousePadding) {
                if (mouseY >= standardY - offset + SCREEN_Y + SCREEN_HEIGHT / 4 && mouseY <= standardY + offset * 3 + SCREEN_Y + SCREEN_HEIGHT / 4) {
                    selection = 0;
                    minY = standardY - offset + SCREEN_Y + SCREEN_HEIGHT / 4;
                    maxY = standardY + offset * 3 + SCREEN_Y + SCREEN_HEIGHT / 4;
                } else if (mouseY >= standardY + middle - offset && mouseY <= standardY + middle + offset * 3) {
                    selection = 1;
                    minY = standardY + middle - offset;
                    maxY = standardY + middle + offset * 3;
                } else if (mouseY >= standardY - offset + middle + SCREEN_HEIGHT / 4 && mouseY <= standardY + offset * 3 + middle + SCREEN_HEIGHT / 4) {
                    selection = 2;
                    minY = standardY - offset + middle + SCREEN_HEIGHT / 4;
                    maxY = standardY + offset * 3 + middle + SCREEN_HEIGHT / 4;
                }
            }

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(BACK_COLOR);
            if (selection != -1) {
                tessellator.addVertex(guiLeft + mousePadding, maxY, 0);
                tessellator.addVertex(guiLeft + XSIZE - mousePadding, maxY, 0);
                tessellator.addVertex(guiLeft + XSIZE - mousePadding, minY, 0);
                tessellator.addVertex(guiLeft + mousePadding, minY, 0);
            }
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            mc.fontRenderer.drawString("BLOCK", centeredX("BLOCK"), guiTop - offset + SCREEN_Y + SCREEN_HEIGHT / 4, selection == 0 ? TEXT_HIGHLIGHT_COLOR : TEXT_COLOR);
            mc.fontRenderer.drawString("ITEM", centeredX("ITEM"), guiTop - offset + middle, selection == 1 ? TEXT_HIGHLIGHT_COLOR : TEXT_COLOR);
            mc.fontRenderer.drawString("OTHER", centeredX("OTHER"), guiTop - offset + middle + SCREEN_HEIGHT / 4, selection == 2 ? TEXT_HIGHLIGHT_COLOR : TEXT_COLOR);
        } else if (currentEntry == null) {
            mc.fontRenderer.drawString(currentCategory.name() + ":", centeredX(currentCategory.name() + ":"), guiTop + SCREEN_Y + 5, TEXT_COLOR);

            if (categoryCache != null && !categoryCache.isEmpty()) {
                for (int i=0; i<categoryCache.size(); i++) {
                    DocumentationEntry entry = categoryCache.get(i);
                    String localizedName = StatCollector.translateToLocal(entry.getUnlocalizedName()).toUpperCase();
                    if (mc.fontRenderer.getStringWidth(localizedName) >= SCREEN_WIDTH) {
                        localizedName = mc.fontRenderer.trimStringToWidth(localizedName, SCREEN_WIDTH - (mc.fontRenderer.getStringWidth(".....")));
                        localizedName = localizedName + "...";
                    }
                    boolean selected = mouseX >= guiLeft + mousePadding && mouseX <= guiLeft + XSIZE - mousePadding && mouseY >= guiTop + SCREEN_Y + 20 + (15 * i) && mouseY <= guiTop + SCREEN_Y + 20 + (15 * i) + 10;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    Tessellator tessellator = Tessellator.instance;
                    tessellator.startDrawingQuads();
                    tessellator.setColorOpaque_I(BACK_COLOR);
                    if (selected) {
                        tessellator.addVertex(guiLeft + mousePadding, guiTop + SCREEN_Y + 20 + (15 * i) + 10, 0);
                        tessellator.addVertex(guiLeft + XSIZE - mousePadding, guiTop + SCREEN_Y + 20 + (15 * i) + 10, 0);
                        tessellator.addVertex(guiLeft + XSIZE - mousePadding, guiTop + SCREEN_Y + 20 + (15 * i), 0);
                        tessellator.addVertex(guiLeft + mousePadding, guiTop + SCREEN_Y + 20 + (15 * i), 0);
                    }
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    mc.fontRenderer.drawString(localizedName, centeredX(localizedName), guiTop + SCREEN_Y + 20 + (15 * i), selected ? TEXT_HIGHLIGHT_COLOR : TEXT_COLOR);
                }
            }
        } else if(currentPage == null){
            this.currentPage = this.currentEntry.pages.getFirst();
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
            int selection = -1;
            final int mousePadding = 25;
            final int offset = mc.fontRenderer.FONT_HEIGHT / 2;
            final int standardY = guiTop - offset;
            final int middle = SCREEN_Y + SCREEN_HEIGHT / 2;

            if (currentCategory == null) {
                if (mouseX >= guiLeft + mousePadding && mouseX <= guiLeft + XSIZE - mousePadding) {
                    if (mouseY >= standardY - offset + SCREEN_Y + SCREEN_HEIGHT / 4 && mouseY <= standardY + offset * 3 + SCREEN_Y + SCREEN_HEIGHT / 4) {
                        selection = 0;
                    } else if (mouseY >= standardY + middle - offset && mouseY <= standardY + middle + offset * 3) {
                        selection = 1;
                    } else if (mouseY >= standardY - offset + middle + SCREEN_HEIGHT / 4 && mouseY <= standardY + offset * 3 + middle + SCREEN_HEIGHT / 4) {
                        selection = 2;
                    }
                }

                switch (selection) {
                    case 0:
                        currentCategory = Documentation.Category.BLOCK;
                        break;
                    case 1:
                        currentCategory = Documentation.Category.ITEM;
                        break;
                    case 2:
                        currentCategory = Documentation.Category.OTHER;
                        break;
                    default:
                        break;
                }

                if (currentCategory != null) {
                    categoryCache = Documentation.get(currentCategory);
                }
            } else if(currentEntry == null){
                if(isEntry(mouseX, mouseY)){
                    this.currentEntry = getEntry(mouseX, mouseY);
                }
            }

            if (mouseX >= guiLeft + HOME_X && mouseX <= guiLeft + HOME_X + HOME_WIDTH && mouseY >= guiTop + HOME_Y && mouseY <= guiTop + HOME_Y + HOME_HEIGHT) {
                currentCategory = null;
                categoryCache = null;
                currentEntry = null;
                currentPage = null;
            }
        }
    }

    private boolean isEntry(int x, int y){
        return this.categoryCache != null && getEntry(x, y) != null;

    }

    private DocumentationEntry getEntry(int x, int y){
        int mousePadding = 25;
        for(int i = 0; i < this.categoryCache.size(); i++){
            boolean selected = x >= guiLeft + mousePadding && x <= guiLeft + XSIZE - mousePadding && y >= guiTop + SCREEN_Y + 20 + (15 * i) && y <= guiTop + SCREEN_Y + 20 + (15 * i) + 10;
            if(selected){
                return this.categoryCache.get(i);
            }
        }

        return null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private int centeredX(String string) {
        return guiLeft + SCREEN_X + (SCREEN_WIDTH / 2) - mc.fontRenderer.getStringWidth(string) / 2;
    }
}
