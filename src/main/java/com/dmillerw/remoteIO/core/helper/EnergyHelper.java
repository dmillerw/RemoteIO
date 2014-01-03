package com.dmillerw.remoteIO.core.helper;

import cofh.api.energy.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan Miller on 1/2/14
 */
public class EnergyHelper {

    public static int distributeCharge(IInventory inventory, EnergyType type, int amount, boolean simulate) {
        for (ItemStack stack : getElectricalItems(inventory, type)) {
            if (amount > 0) {
                switch(type) {
                    case EU: {
                        amount -= ElectricItem.manager.charge(stack, amount, ((IElectricItem)stack.getItem()).getTier(stack), false, false);
                        break;
                    }

                    case RF: {
                        amount -= ((IEnergyContainerItem)stack.getItem()).receiveEnergy(stack, amount, simulate);
                        break;
                    }
                }
            } else {
                break;
            }
        }
        return amount;
    }

    public static boolean requiresCharge(IInventory inventory, EnergyType type) {
        for (ItemStack stack : getElectricalItems(inventory, type)) {
            switch(type) {
                case EU: {
                    if (ElectricItem.manager.charge(stack, 1, ((IElectricItem)stack.getItem()).getTier(stack), true, true) == 1) {
                        return true;
                    } else {
                        break;
                    }
                }

                case RF: {
                    if (((IEnergyContainerItem)stack.getItem()).receiveEnergy(stack, 1, true) == 1) {
                        return true;
                    } else {
                        break;
                    }
                }
            }
        }
        return false;
    }

    public static ItemStack[] getElectricalItems(IInventory inventory, EnergyType type) {
        return getItemsOfType(inventory, type.energyClass);
    }

    private static ItemStack[] getItemsOfType(IInventory inventory, Class clazz) {
        List<ItemStack> items = new ArrayList<ItemStack>();
        for (ItemStack stack : InventoryHelper.getContents(inventory)) {
            if (stack != null && clazz.isInstance(stack.getItem())) {
                items.add(stack);
            }
        }
        return items.toArray(new ItemStack[items.size()]);
    }

    public static enum EnergyType {
        EU(IElectricItem.class),
        RF(IEnergyContainerItem.class);

        public Class energyClass;

        private EnergyType(Class energyClass) {
            this.energyClass = energyClass;
        }
    }

}
