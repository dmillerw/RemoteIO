package com.dmillerw.remoteIO.item;

import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

/**
 * Created by Dylan Miller on 1/1/14
 */
public class ItemDocumentation extends Item {

    private Icon icon;

    public ItemDocumentation(int id) {
        super(id);

        setMaxStackSize(1);
        setCreativeTab(CreativeTabRIO.tab);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isSneaking() && !world.isRemote) {
            // player.openGui(RemoteIO.instance, 2, world, 0, 0, 0);
            player.addChatMessage("Temporarily disabled. Sorry. :(");
        }
        return stack;
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
