package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.block.BlockMachine;

import net.minecraft.block.BlockColored;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;

public class ItemBlockMachine extends ItemBlock {

	public ItemBlockMachine(int id) {
		super(id);
	}

	public int getMetadata(int damage) {
		return damage;
	}

	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + BlockMachine.INTERNAL_NAMES[stack.getItemDamage()];
	}
	
}