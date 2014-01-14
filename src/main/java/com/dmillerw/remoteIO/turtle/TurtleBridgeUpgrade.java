package com.dmillerw.remoteIO.turtle;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.oredict.OreDictionary;

import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.item.ItemHandler;

import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.ITurtleAccess;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.api.TurtleSide;
import dan200.turtle.api.TurtleUpgradeType;
import dan200.turtle.api.TurtleVerb;

public class TurtleBridgeUpgrade implements ITurtleUpgrade {

    @Override
    public int getUpgradeID() {
        return 190;
    }

    @Override
    public String getAdjective() {
        return "Bridged";
    }

    @Override
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.Peripheral;
    }

    @Override
    public ItemStack getCraftingItem() {
        return new ItemStack(ItemHandler.itemUpgrade, 1, OreDictionary.WILDCARD_VALUE);
    }

    @Override
    public boolean isSecret() {
        return false;
    }

    @Override
    public IHostedPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new TurtleBridgePeripheral(turtle);
    }

    @Override
    public boolean useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
        return false;
    }

    @Override
    public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
        return BlockHandler.blockBridge.getIcon(1, 1);
    }

}
