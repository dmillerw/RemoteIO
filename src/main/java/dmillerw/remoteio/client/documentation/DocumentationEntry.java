package dmillerw.remoteio.client.documentation;

import java.util.LinkedList;

/**
 * @author dmillerw
 */
public class DocumentationEntry {

    private String unlocalizedName;

    public LinkedList<IDocumentationPage> pages;

    public DocumentationEntry(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
    }

    public void addPage(IDocumentationPage page) {
        this.pages.add(page);
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }
}
