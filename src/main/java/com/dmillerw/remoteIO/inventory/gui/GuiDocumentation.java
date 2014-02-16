package com.dmillerw.remoteIO.inventory.gui;

import com.dmillerw.remoteIO.api.documentation.DocumentationRegistry;
import com.dmillerw.remoteIO.inventory.ContainerNull;
import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
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

    public static final int STATE_HOME = 0;
    public static final int STATE_CATEGORY = 1;
    public static final int STATE_DOC = 2;

    public int currentState = 0;
    public int scrollIndex = 0;

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
                    boolean shouldHighlight = shouldHighlight(mouseX, mouseY, yIndex + (this.fontRenderer.FONT_HEIGHT * (2)));

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
                    boolean shouldHighlight = shouldHighlight(mouseX, mouseY, yIndex + (this.fontRenderer.FONT_HEIGHT * (2)));

                    if (shouldHighlight) {
                        currentState = 2;

                        currentDocumentation.clear();
                        currentDocumentation.add(DocumentationRegistry.getCategory(currentCategory).getChildren()[i + scrollIndex].getName());
                        String[] breakSplit = DocumentationRegistry.getCategory(currentCategory).getChildren()[i + scrollIndex].getDescription().split("\n");
                        for (String split : breakSplit) {
                            currentDocumentation.addAll(this.fontRenderer.listFormattedStringToWidth(split, SCREEN_WIDTH));
                        }

                        scrollIndex = 0;
                        break;
                    }
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

        if (lineSize > LINE_MAX) {
            scrollIndex += wheel;

            if (scrollIndex > lineSize - LINE_MAX) {
                scrollIndex = lineSize - LINE_MAX;
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
                boolean shouldHighlight = shouldHighlight(par1, par2, yIndex + (this.fontRenderer.FONT_HEIGHT * (2)));

                this.fontRenderer.drawString(DocumentationRegistry.getCategories()[i + scrollIndex].getName(), 10, yIndex, shouldHighlight ? 0xFF5555 : 0xFFFFFF);
            }
        } else if (currentState == 1) {
            for (int i=0; i<getMinIndex(DocumentationRegistry.getCategory(currentCategory).getChildren().length); i++) {
                int yIndex = 10 + (this.fontRenderer.FONT_HEIGHT * (i + 2));
                boolean shouldHighlight = shouldHighlight(par1, par2, yIndex + (this.fontRenderer.FONT_HEIGHT * (2)));

                this.fontRenderer.drawString(DocumentationRegistry.getCategory(currentCategory).getChildren()[i + scrollIndex].getName(), 10, yIndex, shouldHighlight ? 0xFF5555 : 0xFFFFFF);
            }
        } else if (currentState == 2) {
            for (int i=1; i<getMinIndex(currentDocumentation.size() - 1); i++) {
                int yIndex = 10 + (this.fontRenderer.FONT_HEIGHT * (i + 1));

                this.fontRenderer.drawString(currentDocumentation.get(i + scrollIndex), 10, yIndex, 0xFFFFFF);
            }
        }
    }

    private boolean shouldHighlight(int mouseX, int mouseY, int elementY) {
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;

        return (mouseY >= elementY && mouseY <= elementY + this.fontRenderer.FONT_HEIGHT &&
                mouseX >= guiLeft && mouseX <= guiLeft + SCREEN_WIDTH);
    }

    private int getMinIndex(int length) {
        return Math.min(length, LINE_MAX);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/documentation.png"));
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

    private void drawBreadcrumbs(int y) {
        this.fontRenderer.drawSplitString(getBreadcrumbString(), 10, y, SCREEN_WIDTH, 0xFFFFFF);
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

        return sb.toString();
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
