package com.dmillerw.remoteIO.api.documentation;

import net.minecraft.item.ItemStack;

/**
 * Created by Dylan Miller on 2/15/14
 */
public class Documentation {

    private String name;

    private String documentation;

    private ItemStack item;

    public Documentation(String name, String documentation) {
        this.name = name;
        this.documentation = documentation;
    }

    public Documentation(String name, String documentation, ItemStack item) {
        this(name, documentation);

        this.item = item;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return documentation;
    }

    public ItemStack getItem() { return item; }

}
