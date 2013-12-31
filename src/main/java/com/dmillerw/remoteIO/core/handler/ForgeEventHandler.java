package com.dmillerw.remoteIO.core.handler;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

public class ForgeEventHandler {

	@ForgeSubscribe
	public void onContainerInteract(PlayerOpenContainerEvent event) {
		//Oh god the hackishness of this :(
		
		if (!event.canInteractWith) { //TODO Only force on Vanilla (?)
			event.setResult(Result.ALLOW);
		}
	}
	
}
