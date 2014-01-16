package com.dmillerw.remoteIO.core.helper;

import cofh.api.energy.IEnergyContainerItem;
import dan200.turtle.api.ITurtleAccess;
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

    public static int distributeCharge(ITurtleAccess turtle, EnergyType type, int amount, boolean simulate) {
        int amountUsed = 0;

        while(amount - type.fuelValue > 0) {
            amount -= type.fuelValue;
            amountUsed += type.fuelValue;

            if (!simulate) {
                turtle.consumeFuel(-type.fuelValue);
            }
        }

        switch(type) {
            case EU: return amount;
            case RF: return amountUsed;
            default: return 0;
        }
    }

    public static int distributeCharge(IInventory inventory, EnergyType type, int amount, boolean simulate) {
        int amountUsed = 0;

        for (ItemStack stack : getElectricalItems(inventory, type)) {
            if (amount > 0) {
                switch(type) {
                    case EU: {
                        int used = ElectricItem.manager.charge(stack, amount, ((IElectricItem)stack.getItem()).getTier(stack), false, false);
                        amount -= used;
                        amountUsed += used;
                        break;
                    }

                    case RF: {
                        int used = ((IEnergyContainerItem)stack.getItem()).receiveEnergy(stack, amount, simulate);
                        amount -= used;
                        amountUsed += used;
                        break;
                    }
                }
            } else {
                break;
            }
        }

        switch(type) {
            case EU: return amount;
            case RF: return amountUsed;
            default: return 0;
        }
    }

    public static boolean requiresCharge(ITurtleAccess turtle, EnergyType type) {
        return true; // For now? Turtles don't have a fuel limit that I know of
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
        EU(IElectricItem.class, 5),
        RF(IEnergyContainerItem.class, 350);

        public Class energyClass;

        public int fuelValue;

        private EnergyType(Class energyClass, int fuelValue) {
            this.energyClass = energyClass;
            this.fuelValue = fuelValue;
        }
    }

}
