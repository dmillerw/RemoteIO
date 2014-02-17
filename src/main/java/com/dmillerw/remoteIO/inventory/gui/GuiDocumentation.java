package com.dmillerw.remoteIO.inventory.gui;

import com.dmillerw.remoteIO.api.documentation.Documentation;
import com.dmillerw.remoteIO.api.documentation.DocumentationRegistry;
import com.dmillerw.remoteIO.core.helper.RecipeHelper;
import com.dmillerw.remoteIO.core.helper.StringHelper;
import com.dmillerw.remoteIO.inventory.ContainerNull;
import com.dmillerw.remoteIO.lib.ModInfo;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiDocumentation extends GuiContainer {

    private static String LINE = "";

    public static final int SCREEN_WIDTH = 216;
    public static final int LINE_MAX = 18;

    public int currentState = 0;
    public int scrollIndex = 0;

    public boolean showRecipe = false;
    public ItemStack[] recipe = null;

    public String currentCategory = "";

    public List<String> currentDocumentation = new ArrayList<String>();

	private EntityPlayer player;

	public GuiDocumentation(EntityPlayer player) {
		super(new ContainerNull());

		this.player = player;

        this.xSize = 230;
        this.ySize = 202;
	}

    @Override
    public void updateScreen() {

    }

    @Override
    protected void keyTyped(char key, int keycode) {
        if (currentState == 0) {
            super.keyTyped(key, keycode);
        } else {
            if (keycode == Keyboard.KEY_ESCAPE) {
                if (currentState == 2) {
                    showRecipe = false;
                    recipe = null;
                    currentDocumentation.clear();
                }
                if (currentState == 1) {
                    currentCategory = "";
                }
                scrollIndex = 0;
                currentState--;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (button == 0) {
            if (currentState == 0) {
                for (int i=0; i<getMinIndex(DocumentationRegistry.getCategories().length); i++) {
                    int yIndex = 10 + (this.fontRenderer.FONT_HEIGHT * (i + 2));
                    boolean shouldHighlight = shouldHighlight(mouseX, mouseY, yIndex);

                    if (shouldHighlight) {
                        currentState = 1;
                        currentCategory = DocumentationRegistry.getCategories()[i + scrollIndex].getName();

                        scrollIndex = 0;
                        break;
                    }
                }
            } else if (currentState == 1) {
                for (int i=0; i<getMinIndex(DocumentationRegistry.getCategory(currentCategory).getChildren().length); i++) {
                    int yIndex = 10 + (this.fontRenderer.FONT_HEIGHT * (i + 2));
                    boolean shouldHighlight = shouldHighlight(mouseX, mouseY, yIndex);

                    if (shouldHighlight) {
                        currentState = 2;

                        currentDocumentation.clear();

                        Documentation documentation = DocumentationRegistry.getCategory(currentCategory).getChildren()[i + scrollIndex];

                        String[] breakSplit = documentation.getDescription().split("\n");
                        for (String split : breakSplit) {
                            currentDocumentation.addAll(this.fontRenderer.listFormattedStringToWidth(split, SCREEN_WIDTH - this.fontRenderer.getCharWidth('_')));
                        }

                        currentDocumentation.add(0, DocumentationRegistry.getCategory(currentCategory).getChildren()[i + scrollIndex].getName());

                        recipe = RecipeHelper.getFirstRecipeForItem(documentation.getItem());
                        if (recipe != null) {
                            boolean filled = false;
                            for (ItemStack stack : recipe) {
                                if (stack != null) {
                                    filled = true;
                                }
                            }

                            if (!filled) {
                                recipe = null;
                            }
                        }

                        scrollIndex = 0;
                        break;
                    }
                }
            } else if (currentState == 2 && recipe != null) {
                int yIndex = 10 + this.fontRenderer.FONT_HEIGHT * (LINE_MAX + 1);

                if (shouldHighlight(mouseX, mouseY, yIndex)) {
                    showRecipe = !showRecipe;
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        // Mouse scrolling
        int wheel = Mouse.getDWheel();

        if (wheel != 0) {
            if (wheel > 0) {
                wheel = -1;
            } else if (wheel < 0) {
                wheel = 1;
            }
        }

        // Offset
        int lineSize = 0;
        if (currentState == 0) {
            lineSize = DocumentationRegistry.getCategories().length;
        } else if (currentState == 1) {
            lineSize = DocumentationRegistry.getCategory(currentCategory).getChildren().length;
        } else if (currentState == 2) {
            lineSize = currentDocumentation.size();
        }

        if (lineSize > getLineMax()) {
            scrollIndex += wheel;

            if (scrollIndex > lineSize - getLineMax()) {
                scrollIndex = lineSize - getLineMax();
            } else if (scrollIndex < 0) {
                scrollIndex = 0;
            }
        }

        super.drawScreen(mouseX, mouseY, partial);
    }

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        drawBreadcrumbs(10);
        drawLine(10 + this.fontRenderer.FONT_HEIGHT);

        if (currentState == 0) {
            for (int i=0; i<getMinIndex(DocumentationRegistry.getCategories().length); i++) {
                int yIndex = 10 + (this.fontRenderer.FONT_HEIGHT * (i + 2));
                boolean shouldHighlight = shouldHighlight(par1, par2, yIndex);

                this.fontRenderer.drawString(DocumentationRegistry.getCategories()[i + scrollIndex].getName(), 10, yIndex, shouldHighlight ? 0xFF5555 : 0xFFFFFF);
            }
        } else if (currentState == 1) {
            for (int i=0; i<getMinIndex(DocumentationRegistry.getCategory(currentCategory).getChildren().length); i++) {
                int yIndex = 10 + (this.fontRenderer.FONT_HEIGHT * (i + 2));
                boolean shouldHighlight = shouldHighlight(par1, par2, yIndex);

                this.fontRenderer.drawString(DocumentationRegistry.getCategory(currentCategory).getChildren()[i + scrollIndex].getName(), 10, yIndex, shouldHighlight ? 0xFF5555 : 0xFFFFFF);
            }
        } else if (currentState == 2) {
            for (int i=1; i<getMinIndex(currentDocumentation.size()); i++) {
                int yIndex = 10 + (this.fontRenderer.FONT_HEIGHT * (i + 1));

                this.fontRenderer.drawString(currentDocumentation.get(i + scrollIndex), 10, yIndex, 0xFFFFFF);

                if (recipe != null) {
                    drawLine(10 + this.fontRenderer.FONT_HEIGHT * (LINE_MAX));
                    int showYIndex = 10 + this.fontRenderer.FONT_HEIGHT * (LINE_MAX + 1);
                    String str = showRecipe ? "Hide Recipe" : "Show Recipe";
                    this.fontRenderer.drawString(str, StringHelper.getCenterOffset(str, this.mc.fontRenderer, SCREEN_WIDTH), showYIndex, shouldHighlight(par1, par2, showYIndex) ? 0xFF5555 : 0xFFFFFF);
                }
            }
        }
    }

    private boolean shouldHighlight(int mouseX, int mouseY, int elementY) {
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;

        int mx = mouseX - guiLeft;
        int my = mouseY - guiTop;

        return (my >= elementY && my <= elementY + this.fontRenderer.FONT_HEIGHT &&
                mx >= 0 && mx <= SCREEN_WIDTH);
    }

    private int getMinIndex(int length) {
        return Math.min(length, getLineMax());
    }

    private int getLineMax() {
        if (currentState == 2 && recipe != null) {
            return LINE_MAX - 2;
        } else {
            return LINE_MAX;
        }
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/documentation.png"));
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        if (showRecipe && recipe != null) {
            this.mc.getTextureManager().bindTexture(new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/grid.png"));
            this.drawTexturedModalRect(k - 68, l, 0, 0, 68, 68);

            // Variables for recipe drawing
            int startX = -60;
            int startY = 8;
            int adjust = 18;

            int currX = startX;
            int currY = startY;

            for (int i=0; i<9; i++) {
                ItemStack stack = recipe[i];

                if (stack != null) {
                    drawItemStack(stack, k + currX, l + currY, "");

                    int mx = a - k;
                    int my = b - l;
                    if (mx >= currX && mx <= currX + adjust && my >= currY && my <= currY + adjust) {
                        drawItemStackTooltip(stack, mx + adjust, my + adjust);
                    }
                }

                currX += adjust;
                if (i + 1 == 3 || i + 1 == 6) {
                    currY += adjust;
                    currX = startX;
                }
            }
        }
	}

    private void drawItemStack(ItemStack stack, int x, int y, String str) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRenderer.zLevel = 200.0F;
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glColor3f(1f, 1f, 1f);
        GL11.glEnable(GL11.GL_NORMALIZE);
        FontRenderer font = null;
        if (stack != null) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = Minecraft.getMinecraft().fontRenderer;
        itemRenderer.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), stack, x, y);
        itemRenderer.renderItemOverlayIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), stack, x, y, str);
        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
    }

    protected void drawItemStackTooltip(ItemStack stack, int x, int y) {
        final Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = Objects.firstNonNull(stack.getItem().getFontRenderer(stack), mc.fontRenderer);

        @SuppressWarnings("unchecked")
        List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

        List<String> colored = Lists.newArrayListWithCapacity(1);
        colored.add(getRarityColor(stack) + list.get(0));

        drawHoveringText(colored, x, y, font);
    }

    protected EnumChatFormatting getRarityColor(ItemStack stack) {
        return EnumChatFormatting.values()[stack.getRarity().rarityColor];
    }

    private void drawBreadcrumbs(int y) {
        this.fontRenderer.drawString((String)this.fontRenderer.listFormattedStringToWidth(getBreadcrumbString(), SCREEN_WIDTH).get(0), 10, y, 0xFFFFFF);
    }

    private void drawLine(int y) {
        this.fontRenderer.drawString(getLine(), 10, y, 0xFFFFFF);
    }

    private String getBreadcrumbString() {
        StringBuilder sb = new StringBuilder();

        if (currentState >= 0) {
            sb.append("Home");
        }
        if (currentState >= 1) {
            sb.append(" -> ");
            sb.append(currentCategory);
        }
        if (currentState >= 2) {
            sb.append(" -> ");
            sb.append(currentDocumentation.get(0));
        }

        return StringHelper.squish(sb.toString(), this.mc.fontRenderer, SCREEN_WIDTH - 10);
    }

    private String getLine() {
        if (LINE.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<SCREEN_WIDTH; i++) {
                sb.append("-");
            }
            LINE = (String) this.fontRenderer.listFormattedStringToWidth(sb.toString(), SCREEN_WIDTH - this.fontRenderer.getCharWidth('-')).get(0);
        }

        return LINE;
    }

}
