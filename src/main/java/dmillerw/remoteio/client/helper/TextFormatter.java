package dmillerw.remoteio.client.helper;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.FontRenderer;

import java.util.EnumSet;
import java.util.LinkedList;

/**
 * @author dmillerw
 */
public class TextFormatter {

    private static final char FORMATTING = '§';

    public static LinkedList<FormattedString> format(String ... strings) {
        LinkedList<FormattedString> list = Lists.newLinkedList();
        Format lastFormat = null;

        for (String str : strings) {
            str = str.trim();
            String[] words = str.split(" ");

            for (String word : words) {
                if (word == null || word.isEmpty()) {
                    continue;
                }

                word = word.trim();

                Format format = null;
                boolean trimStart = false;
                boolean trimEnd = false;
                boolean continuingFormat = false;

                char start = word.charAt(0);
                char end = word.charAt(word.length() - 1);

                // If they don't match (like one is a letter and the other is symbol)
                if (start != end) {
                    Format fStart = Format.getFormat(start);
                    Format fEnd = Format.getFormat(end);

                    if (fStart != null)
                        trimStart = true;

                    if (fEnd != null)
                        trimEnd = true;

                    // If last format isn't null, we ignore the starting token, and see if the current format should be ended
                    if (lastFormat != null) {
                        if (fEnd == lastFormat) {
                            format = lastFormat;
                            lastFormat = null;
                        } else {
                            continuingFormat = true;
                        }
                    } else {
                        // Otherwise, we set the last format to the starting token, ignoring the end
                        format = fStart;
                        lastFormat = fStart;
                        continuingFormat = true;
                    }
                } else {
                    // They match, so the format is specific to this word
                    format = Format.getFormat(start);
                    trimStart = true;
                    trimEnd = true;
                }

                if (format != null) {
                    list.add(new FormattedString(word.substring(trimStart ? 1 : 0, word.length() - (trimEnd ? 1 : 0)), EnumSet.of(format)).setContinuingFormat(continuingFormat));
                } else {
                    list.add(new FormattedString(word));
                }
            }
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

        private boolean continuingFormat = false;

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

        public FormattedString setContinuingFormat(boolean continuingFormat) {
            this.continuingFormat = continuingFormat;
            return this;
        }

        public int getWidth(FontRenderer fontRenderer) {
            return fontRenderer.getStringWidth(string);
        }

        public void draw(FontRenderer fontRenderer, int x, int y) {
            if (!format.isEmpty()) {
                StringBuffer stringBuffer = new StringBuffer();
                for (Format format1 : format) {
                    stringBuffer.append(FORMATTING).append(format1.code);
                    stringBuffer.append(string);
                    if (continuingFormat) {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append(FORMATTING).append('r');
                }
                fontRenderer.drawString(stringBuffer.toString(), x, y, color);
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
