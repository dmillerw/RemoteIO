package com.dmillerw.remoteIO.item;

import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import java.util.List;

public class ItemUpgrade extends Item {

	public Icon[] icons;
	
	public ItemUpgrade(int id) {
		super(id);
		
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return Upgrade.values()[stack.getItemDamage()] == Upgrade.RANGE_WITHER;
	}
	
	@Override
	public Icon getIconFromDamage(int damage) {
		return this.icons[damage];
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (Upgrade upgrade : Upgrade.values()) {
			if ((upgrade == Upgrade.BLANK) || upgrade.enabled) {
				list.add(upgrade.toItemStack());
			}
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[Upgrade.values().length];
		
		for (Upgrade upgrade : Upgrade.values()) {
			this.icons[upgrade.ordinal()] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "upgrade/" + upgrade.texture);
		}
	}

    @Override
    public String getItemDisplayName(ItemStack stack) {
        return super.getItemDisplayName(stack) + " " + I18n.getString("upgrade.word");
    }

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "upgrade." + Upgrade.values()[stack.getItemDamage()].texture;
	}	
	
	public static enum Upgrade {
		BLANK(
			"blank", 
			new boolean[3]
		), 
		
		ITEM(
			"item", 
			new boolean[] {
			        true,
			        true,
			        true,
                    true
			}
		), 
				
		FLUID(
			"fluid",
			new boolean[] {
                    true,
                    false,
                    false,
                    true
            }
		), 
				
		POWER_MJ(
			"powerBC",
			new boolean[] {
                    true,
                    false,
                    false,
                    true
            }
		), 
				
		RANGE_T1(
			"rangeT1",
			new boolean[] {
                    true,
                    true,
                    true,
                    false
            }
		), 
				
		CROSS_DIMENSIONAL(
			"dimension", 
			new boolean[] {
                    true,
                    true,
                    true,
                    false
            }
		), 
				
		ISIDED_AWARE(
			"side", 
			new boolean[] {
                    true,
                    false,
                    false,
                    false
            }
		), 
				
		REDSTONE(
			"redstone",
			new boolean[] {
                    true,
                    true,
                    true,
                    true
            }
		), 
				
		CAMO(
			"camo",
			new boolean[] {
                    true,
                    true,
                    true,
                    true
            }
		), 
				
		LOCK(
			"lock",
			new boolean[] {
                    true,
                    true,
                    true,
                    true
            }
		), 
				
		POWER_RF(
			"powerRF", 
	        new boolean[] {
                    true,
                    true,
                    true,
                    true
            }
		), 
				
		POWER_EU(
			"powerEU",
	        new boolean[] {
                    true,
                    true,
                    true,
                    true
            }
		),
		
		POWER_UE(
			"powerUE",
			new boolean[] {
                    false,
                    false,
                    false,
                    false
            }
		),
		
		LINK_PERSIST(
			"persist",
			new boolean[] {
                    false,
                    false,
                    false,
                    false
            }
		),
		
		RANGE_T2(
			"rangeT2",
	        new boolean[] {
                    true,
                    true,
                    true,
                    false
            }
		),
		
		RANGE_T3(
			"rangeT3",
	        new boolean[] {
                    true,
                    true,
                    true,
                    false
            }
		),
		
		RANGE_WITHER(
			"rangeWither",
	        new boolean[] {
                    true,
                    true,
                    true,
                    false
            }
		),

        AE(
            "ae",
            new boolean[] {
                    true,
                    false,
                    false,
                    false
            }
        );
		
		public String texture;
		
		/** Simple boolean array.<br />Index 0: IO Block<br />Index 1: Remote Inventory<br />Index 2: Turtle Bridge */
		public boolean[] validMachines;

        public boolean enabled = true;

		private Upgrade(String texture, boolean[] validArray) {
			this.texture = texture;
			this.validMachines = validArray;
		}
		
		public ItemStack toItemStack() {
			return new ItemStack(ItemHandler.itemUpgrade, 1, this.ordinal());
		}
		
	}
	
}
