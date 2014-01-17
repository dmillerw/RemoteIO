package com.dmillerw.remoteIO.inventory.gui.documentation;

import com.dmillerw.remoteIO.inventory.gui.documentation.page.AbstractPage;

import java.util.Stack;

/**
 * Created by Dylan Miller on 1/16/14
 */
public class BreadcrumbHandler {

    public Stack<AbstractPage> breadcrumbs = new Stack<AbstractPage>();

    public BreadcrumbHandler() {
        reset();
    }

    private void reset() {
        breadcrumbs.clear();
    }

    public boolean availableBreadcrumbs() {
        return breadcrumbs.size() > 1;
    }

    public void push(AbstractPage crumb) {
        breadcrumbs.push(crumb);
    }

    public void back() {
        breadcrumbs.pop();
    }

    public String formatBreadcrumbs() {
        StringBuilder sb = new StringBuilder();
        for (AbstractPage crumb : breadcrumbs) {
            sb.append(crumb);
            sb.append(" > ");
        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 3, sb.length());
        }
        return sb.toString();
    }

}
