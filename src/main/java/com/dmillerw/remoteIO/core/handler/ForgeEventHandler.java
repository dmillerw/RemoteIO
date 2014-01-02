package com.dmillerw.remoteIO.core.handler;

import com.dmillerw.remoteIO.item.ItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

public class ForgeEventHandler {

	@ForgeSubscribe
	public void onContainerInteract(PlayerOpenContainerEvent event) {
        // Ignores the container's canInteract method only if player is holding transceiver

		if (!event.canInteractWith) {
            ItemStack playerHeld = event.entityPlayer.getCurrentEquippedItem();
            if (playerHeld != null && playerHeld.getItem() == ItemHandler.itemTransmitter) {
                event.setResult(Event.Result.ALLOW);
            }
		}
	}
	
}
