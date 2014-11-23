package dmillerw.remoteio.client.documentation;

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

    public static enum Category {
        BLOCK,
        ITEM,
        OTHER
    }
}
