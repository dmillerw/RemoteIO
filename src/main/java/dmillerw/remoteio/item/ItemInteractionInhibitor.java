package dmillerw.remoteio.item;

import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.lib.ModInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemInteractionInhibitor extends Item {

    private IIcon iconInactive;
    private IIcon iconActive;

    public ItemInteractionInhibitor() {
        super();

        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean debug) {
        // Status
        switch (itemStack.getItemDamage()) {
            case 0:
            case 2: list.add(" - " + StatCollector.translateToLocal("inhibitor.inactive")); break;
            case 1:
            case 3: list.add(" - " + StatCollector.translateToLocal("inhibitor.active")); break;
        }

        // Block/Item
        switch (itemStack.getItemDamage()) {
            case 0:
            case 1: list.add(" - " + StatCollector.translateToLocal("inhibitor.block")); break;
            case 2:
            case 3: list.add(" - " + StatCollector.translateToLocal("inhibitor.item")); break;
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (entityPlayer.isSneaking()) {
            if (itemStack.getItemDamage() == 0) {
                itemStack.setItemDamage(1);
            } else if (itemStack.getItemDamage() == 1) {
                itemStack.setItemDamage(0);
            } else if (itemStack.getItemDamage() == 2) {
                itemStack.setItemDamage(3);
            } else if (itemStack.getItemDamage() == 3) {
                itemStack.setItemDamage(2);
            }
        }
        return itemStack;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 2));
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return damage == 1 ? iconActive : iconInactive;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        iconInactive = register.registerIcon(ModInfo.RESOURCE_PREFIX + "inhibitor_inactive");
        iconActive = register.registerIcon(ModInfo.RESOURCE_PREFIX + "inhibitor_active");
    }
}
