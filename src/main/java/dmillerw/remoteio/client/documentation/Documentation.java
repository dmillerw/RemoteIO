package dmillerw.remoteio.client.documentation;

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
        list.add(new DocumentationEntry("documentation.block.test_1").addPage(new DocumentationPageText("documentation.block.test.page.1")));
        list.add(new DocumentationEntry("documentation.block.test_2").addPage(new DocumentationPageText("documentation.block.test.page.1")));
        list.add(new DocumentationEntry("documentation.block.test_3").addPage(new DocumentationPageText("documentation.block.test.page.1")));
        register(Category.BLOCK, list);
    }

    public static enum Category {
        BLOCK,
        ITEM,
        OTHER
    }
}
