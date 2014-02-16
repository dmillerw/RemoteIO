package com.dmillerw.remoteIO.api.documentation;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dylan Miller on 2/15/14
 */
public class Category {

    private String name;

    private Set<Documentation> children = new HashSet<Documentation>();

    public Category(String name) {
        this.name = name;
    }

    public void push(Documentation documentation) {
        children.add(documentation);
    }

    public String getName() {
        return name;
    }

    public Documentation[] getChildren() {
        return children.toArray(new Documentation[children.size()]);
    }

}
