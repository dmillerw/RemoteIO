package com.dmillerw.remoteIO.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.item.ItemHandler;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class CreativeTabRIO extends CreativeTabs {

	public static CreativeTabs tab = new CreativeTabRIO(ModInfo.NAME);

	public CreativeTabRIO(String label) {
		super(label.toLowerCase().replace(" ", "_"));
		LanguageRegistry.instance().addStringLocalization("itemGroup." + label.toLowerCase().replace(" ", "_"), label);
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(ItemHandler.itemTool);
	}

}
