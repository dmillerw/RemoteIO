package com.dmillerw.remoteIO.item;

import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

/**
 * Created by Dylan Miller on 1/1/14
 */
public class ItemScreen extends Item {

    private Icon icon;

    public ItemScreen(int id) {
        super(id);

        setMaxStackSize(1);
        setCreativeTab(CreativeTabRIO.tab);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int meta) {
        return this.icon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        this.icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "itemScreen");
    }

}
