package remoteio.client.documentation;

import com.google.common.collect.Lists;

import java.util.EnumMap;
import java.util.List;

/**
 * @author dmillerw
 */
public class Documentation {
    private static EnumMap<Category, List<DocumentationEntry>> documentationMap = new EnumMap<Category, List<DocumentationEntry>>(Category.class);

    public static void register(Category category, List<DocumentationEntry> documentation) {
        documentationMap.put(category, documentation);
    }

    public static List<DocumentationEntry> get(Category category) {
        return documentationMap.get(category);
    }

    public static void initialize() {
        List<DocumentationEntry> list = Lists.newArrayList();
        list.add(new DocumentationEntry("documentation.block.remoteInterface").addPage(new DocumentationPageText("documentation.block.remoteInterface.page.1")));
        list.add(new DocumentationEntry("documentation.block.remoteInventory").addPage(new DocumentationPageText("documentation.block.remoteInventory.page.1")));
        list.add(new DocumentationEntry("documentation.block.skylight").addPage(new DocumentationPageText("documentation.block.skylight.page.1")));
        list.add(new DocumentationEntry("documentation.block.heater").addPage(new DocumentationPageText("documentation.block.heater.page.1")));
        list.add(new DocumentationEntry("documentation.block.reservoir").addPage(new DocumentationPageText("documentation.block.reservoir.page.1")));
        list.add(new DocumentationEntry("documentation.block.intelligentWorkbench").addPage(new DocumentationPageText("documentation.block.intelligentWorkbench.page.1")));
        register(Category.BLOCK, list);

        list = Lists.newArrayList();
        list.add(new DocumentationEntry("documentation.item.ioTool").addPage(new DocumentationPageText("documentation.item.ioTool.page.1")));
        list.add(new DocumentationEntry("documentation.item.pda").addPage(new DocumentationPageText("documentation.item.pda.page.1")));
        list.add(new DocumentationEntry("documentation.item.wirelessTransmitter").addPage(new DocumentationPageText("documentation.item.wirelessTransmitter.page.1")));
        list.add(new DocumentationEntry("documentation.item.wirelessLocationChip").addPage(new DocumentationPageText("documentation.item.wirelessLocationChip.page.1")));
        list.add(new DocumentationEntry("documentation.item.upgrades").addPage(new DocumentationPageText("documentation.item.upgrades.page.1")));
        list.add(new DocumentationEntry("documentation.item.linker").addPage(new DocumentationPageText("documentation.item.linker.page.1")));
        list.add(new DocumentationEntry("documentation.item.remoteAccessor").addPage(new DocumentationPageText("documentation.item.remoteAccessor.page.1")));
        register(Category.ITEM, list);
    }

    public static enum Category {
        BLOCK,
        ITEM,
        OTHER
    }
}
