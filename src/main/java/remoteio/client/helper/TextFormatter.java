package remoteio.client.helper;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.FontRenderer;

import java.util.EnumSet;
import java.util.LinkedList;

/**
 * @author dmillerw
 */
public class TextFormatter {

    private static final char FORMATTING = '§';

    public static LinkedList<FormattedString> format(String string) {
        return format(string, -1);
    }

    public static LinkedList<FormattedString> format(String string, int overrideColor) {
        LinkedList<FormattedString> list = Lists.newLinkedList();
        EnumSet<Format> activeFormats = EnumSet.noneOf(Format.class);
        int activeColor = -1;

        string = string.trim();
        String[] words = string.split(" ");

        for (String word : words) {
            if (word == null || word.isEmpty()) {
                continue;
            }

            word = word.trim();

            StringBuilder stringBuilder = new StringBuilder();

            EnumSet<Format> formats = EnumSet.copyOf(activeFormats);
            EnumSet<Format> toRemove = EnumSet.noneOf(Format.class);

            boolean resetColor = false;

            for (int i=0; i<word.length(); i++) {
                char character = word.charAt(i);
                if (overrideColor == -1 && character == '@') {
                    boolean terminator = ((i == word.length() - 1) || ((word.charAt(i + 1) != '0') || word.length() <= i + 9) || activeColor != -1);
                    if (!terminator && activeColor != -1) {
                        // NESTED COLORS ARE BAD! STAHP!
                        activeColor = -1;
                    } else {
                        // Try and parse 6 digit HEX color
                        if (terminator) {
                            resetColor = true;
                        } else {
                            try {
                                activeColor = Integer.parseInt(word.substring(i + 3, i + 9), 16);
                            } catch (NumberFormatException ex) {
                                activeColor = -1;
                                ex.printStackTrace();
                            }
                        }

                        // If we suceeded in parsing
                        if (activeColor != -1) {
                            i += 8; // Skip the HEX color
                        }
                    }
                } else {
                    Format format = Format.getFormat(character);
                    if (format != null) {
                        if (formats.contains(format)) {
                            toRemove.add(format);
                        } else {
                            formats.add(format);
                        }
                    } else {
                        stringBuilder.append(character);
                    }
                }
            }

            if (overrideColor != -1)
                activeColor = -1;

            FormattedString formattedString = new FormattedString(stringBuilder.toString(), EnumSet.copyOf(formats), activeColor);

            formats.removeAll(toRemove);
            toRemove.clear();

            if (resetColor)
                activeColor = -1;

            formattedString.setContinuingFormat(formats, !resetColor);
            list.add(formattedString);

            activeFormats.clear();
            activeFormats.addAll(formats);

        }

        return list;
    }

    public static void draw(FontRenderer fontRenderer, LinkedList<FormattedString> list, int x, int y) {
        for (FormattedString formattedString : list) {
            formattedString.draw(fontRenderer, x, y);
            x += formattedString.getWidth(fontRenderer);
            x += fontRenderer.getCharWidth(' ');
            if (formattedString.format.contains(Format.BOLD))
                x += fontRenderer.getCharWidth(' '); // BOLD seems to eat spaces. Formatting/parsing error?
        }
    }

    public static class FormattedString {

        public String string;
        public EnumSet<Format> format;
        public int color;

        private EnumSet<Format> continuingFormats = EnumSet.noneOf(Format.class);
        private boolean continueColor = false;

        public FormattedString(String string) {
            this(string, EnumSet.noneOf(Format.class), 0xFFFFFF);
        }

        public FormattedString(String string, int color) {
            this(string, EnumSet.noneOf(Format.class), color);
        }

        public FormattedString(String string, EnumSet<Format> format) {
            this(string, format, 0xFFFFFF);
        }

        public FormattedString(String string, EnumSet<Format> format, int color) {
            this.string = string;
            this.format = format;
            this.color = color;
        }

        public FormattedString setContinuingFormat(EnumSet<Format> formats, boolean continueColor) {
            this.continuingFormats = formats;
            this.continueColor = continueColor;
            return this;
        }

        public int getWidth(FontRenderer fontRenderer) {
            return fontRenderer.getStringWidth(string);
        }

        public void draw(FontRenderer fontRenderer, int x, int y) {
            if (!format.isEmpty()) {
                // Actual word
                StringBuilder stringBuilder = new StringBuilder();
                for (Format format1 : format) {
                    stringBuilder.append(FORMATTING).append(format1.code);
                }
                stringBuilder.append(string);
                stringBuilder.append(FORMATTING).append('r');
                fontRenderer.drawString(stringBuilder.toString(), x, y, color);

                int length = fontRenderer.getStringWidth(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                x += length;

                if (!continuingFormats.isEmpty()) {
                    for (Format format2 : continuingFormats) {
                        stringBuilder.append(FORMATTING).append(format2.code);
                    }
                    stringBuilder.append(" ");
                    stringBuilder.append(FORMATTING).append('r');
                }

                fontRenderer.drawString(stringBuilder.toString(), x, y, continueColor ? color : 0xFFFFFF);
            } else {
                fontRenderer.drawString(string, x, y, color);
            }
        }

        @Override
        public String toString() {
            return string + (!format.isEmpty() ? " " + format.toString() : "");
        }
    }

    public static enum Format {
        BOLD('*', 'l'),
        ITALIC('-', 'o'),
        UNDERLINE('_', 'n');

        public char token;
        public char code;

        private Format(char token, char code) {
            this.token = token;
            this.code = code;
        }

        public static Format getFormat(char token) {
            for (Format format : Format.values()) {
                if (format.token == token) {
                    return format;
                }
            }
            return null;
        }
    }
}
