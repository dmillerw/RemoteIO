package remoteio.client.documentation;

import com.google.common.collect.Lists;

import java.util.LinkedList;

/**
 * @author dmillerw
 */
public class DocumentationEntry {

    private String unlocalizedName;

    public LinkedList<IDocumentationPage> pages;

    public DocumentationEntry(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        this.pages = Lists.newLinkedList();
    }

    public DocumentationEntry addPage(IDocumentationPage page) {
        this.pages.add(page);
        return this;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }
}
