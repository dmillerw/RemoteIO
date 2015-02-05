package remoteio.client.documentation;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import remoteio.client.gui.GuiDocumentation;
import remoteio.client.helper.TextFormatter;
import remoteio.common.lib.Strings;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dmillerw
 */
public class DocumentationPageText implements IDocumentationPage {

    private String unlocalizedPrefix;

    private LinkedList<TextFormatter.FormattedString>[] localizedText;

    public DocumentationPageText(String unlocalizedPrefix) {
        this.unlocalizedPrefix = unlocalizedPrefix;
        String text = StatCollector.translateToLocal(unlocalizedPrefix);
        List<String> trimmedText = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(text, GuiDocumentation.XSIZE - GuiDocumentation.XPADDING);
        localizedText = new LinkedList[trimmedText.size()];
        for (int i=0; i<localizedText.length; i++) {
            localizedText[i] = TextFormatter.format(trimmedText.get(i));
        }
    }

    @Override
    public void renderScreen(GuiScreen guiScreen, int mouseX, int mouseY) {
        FontRenderer fRenderer = FMLClientHandler.instance().getClient().fontRenderer;
        String localized = StatCollector.translateToLocal(this.unlocalizedPrefix);
        String[] lines = Strings.wrap(localized, 20).split("\n");
        int x = 15;
        int y = 0;
        for(String str : lines){
            fRenderer.drawString(str, x, y += fRenderer.FONT_HEIGHT + 2, GuiDocumentation.TEXT_COLOR);
        }
    }

    @Override
    public void updateScreen(GuiScreen guiScreen) {

    }
}
