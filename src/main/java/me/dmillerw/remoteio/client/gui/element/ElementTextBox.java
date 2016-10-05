package me.dmillerw.remoteio.client.gui.element;

import com.google.common.base.Predicate;
import me.dmillerw.remoteio.client.gui.core.GuiBase;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;

/**
 * Created by dmillerw
 */
public class ElementTextBox extends ElementBase {

    public static enum Alignment {
        LEFT, CENTER, RIGHT
    }

    /* VALUE + PLACEHOLDER */
    private String value = "";
    public ElementTextBox setValue(String value) {
        if (!allowOverflow) {
            value = parentGui.getFontRenderer().trimStringToWidth(value, width);
        }
        this.value = value;
        return this;
    }

    private String placeholder = "";
    public ElementTextBox setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    /* MISC */
    private Predicate<Character> validator = new Predicate<Character>() {
        @Override
        public boolean apply(@Nullable Character input) {
            return true;
        }
    };
    public ElementTextBox setValidator(Predicate<Character> validator) {
        this.validator = validator;
        return this;
    }

    private boolean allowOverflow = false;
    public ElementTextBox allowOverflow(boolean allowOverflow) {
        this.allowOverflow = allowOverflow;
        return this;
    }

    /* FORMATTING */
    private Alignment textAlignment = Alignment.LEFT;
    public ElementTextBox setTextAlignment(Alignment alignment) {
        this.textAlignment = alignment;
        return this;
    }

    private int textOffsetX, textOffsetY = 0;
    public ElementTextBox setTextOffset(int textOffsetX, int textOffsetY) {
        this.textOffsetX = textOffsetX;
        this.textOffsetY = textOffsetY;
        return this;
    }

    private int textColor = 0xFFFFFF;
    public ElementTextBox setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    private int focusedBackgroundColor = -1;
    public ElementTextBox setFocusedBackgroundColor(int focusedBackgroundColor) {
        this.focusedBackgroundColor = focusedBackgroundColor;
        return this;
    }

    public ElementTextBox(GuiBase parentGui, String elementId, int xPosition, int yPosition, int width, int height) {
        super(parentGui, elementId, xPosition, yPosition, width, height);
    }

    @Override
    public void drawElement(int mouseX, int mouseY, float partialTicks) {
        if (focusedBackgroundColor != -1 && parentGui.getFocusedElement() == this) {
            drawRect(xPosition, yPosition, xPosition + width, yPosition + height, focusedBackgroundColor);
        }

        String toDraw = value.isEmpty() ? placeholder : value;
        int x = xPosition;

        switch (textAlignment) {
            case LEFT: break;
            case CENTER: {
                x = xPosition + (width / 2) - parentGui.getFontRenderer().getStringWidth(toDraw) / 2;
                break;
            }
            case RIGHT: {
                x = xPosition + width - parentGui.getFontRenderer().getStringWidth(toDraw);
                break;
            }
        }

        toDraw = parentGui.getFontRenderer().trimStringToWidth(toDraw, width).trim();
        parentGui.getFontRenderer().drawString(toDraw, x + textOffsetX, yPosition + textOffsetY, textColor, false);
    }

    @Override
    public void onKeyTyped(int keycode, char charTyped) {
        if (keycode == Keyboard.KEY_BACK) {
            if (value.length() > 0)
                value = value.substring(0, value.length() - 1);
        } else {
            if (validator.apply(charTyped)) {
                if (!allowOverflow) {
                    String temp = value + charTyped;
                    if (parentGui.getFontRenderer().getStringWidth(temp) > width)
                        return;
                }

                value = value + charTyped;
            }
        }
    }
}
