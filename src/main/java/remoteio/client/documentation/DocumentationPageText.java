package remoteio.client.documentation;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import remoteio.client.gui.GuiDocumentation;
import remoteio.common.lib.Strings;

/**
 * @author dmillerw
 */
public class DocumentationPageText
implements IDocumentationPage {
    private String unlocalizedPrefix;

    public DocumentationPageText(String unlocalizedPrefix) {
        this.unlocalizedPrefix = unlocalizedPrefix;
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
