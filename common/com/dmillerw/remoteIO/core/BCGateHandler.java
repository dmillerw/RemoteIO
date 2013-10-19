package com.dmillerw.remoteIO.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import buildcraft.api.gates.IAction;
import buildcraft.api.gates.IActionProvider;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipe;

import com.dmillerw.remoteIO.block.tile.TileEntityIO;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

public class BCGateHandler {

	public static final String BC_API_GATE_PACKAGE = "buildcraft.api.gates";
	public static final String BC_ACTION_MANAGER_CLASS = BC_API_GATE_PACKAGE + ".ActionManager";
	
	public static final String BC_TRIGGER_PROVIDER_LIST = "triggerProviders";
	public static final String BC_ACTION_PROVIDER_LIST = "actionProviders";
	
	public static final String BC_ACTION_MANAGER_REGISTER_TRIGGER = "registerTriggerProvider";
	public static final String BC_ACTION_MANAGER_REGISTER_ACTION = "registerActionProvider";
	
	public static void inject() {
		try {
			if (Loader.isModLoaded("BuildCraft|Core")) {
				Class actionManager = Class.forName(BC_ACTION_MANAGER_CLASS);
				Method registerTrigger = actionManager.getMethod(BC_ACTION_MANAGER_REGISTER_TRIGGER, ITriggerProvider.class);
				registerTrigger.invoke(null, new IOTriggerProvider());
				Method registerAction = actionManager.getMethod(BC_ACTION_MANAGER_REGISTER_ACTION, IActionProvider.class);
				registerAction.invoke(null, new IOActionProvider());
			}
		} catch(Exception ex) {
			FMLLog.log(Level.WARNING, "[RemoteIO] Failed to register main trigger provider!", new Object[0]);
			FMLLog.log(Level.WARNING, ex.getMessage(), new Object[0]);
		}
	}
	
	public static LinkedList<ITrigger> getTriggersForIO(TileEntityIO tile) {
		LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();
		
		try {
			Class actionManager = Class.forName(BC_ACTION_MANAGER_CLASS);
			Field triggerProviders = actionManager.getDeclaredField(BC_TRIGGER_PROVIDER_LIST);
			triggerProviders.setAccessible(true);
			LinkedList<ITriggerProvider> providers = (LinkedList<ITriggerProvider>) triggerProviders.get(actionManager);

			for (ITriggerProvider provider : providers) {
				if (!(provider instanceof IOTriggerProvider)) {
					triggers.addAll(provider.getNeighborTriggers(tile.blockType, tile));
				}
			}
		} catch(Exception ex) {
			FMLLog.log(Level.WARNING, "[RemoteIO] Failed to grab triggers for IO block connection!", new Object[0]);
		}
		
		return triggers;
	}

	public static LinkedList<IAction> getActionsForIO(TileEntityIO tile) {
		LinkedList<IAction> actions = new LinkedList<IAction>();
		
		try {
			Class actionManager = Class.forName(BC_ACTION_MANAGER_CLASS);
			Field actionProviders = actionManager.getDeclaredField(BC_ACTION_PROVIDER_LIST);
			actionProviders.setAccessible(true);
			LinkedList<IActionProvider> providers = (LinkedList<IActionProvider>) actionProviders.get(actionManager);

			for (IActionProvider provider : providers) {
				if (!(provider instanceof IOActionProvider)) {
					actions.addAll(provider.getNeighborActions(tile.blockType, tile));
				}
			}
		} catch(Exception ex) {
			FMLLog.log(Level.WARNING, "[RemoteIO] Failed to grab actions for IO block connection!", new Object[0]);
		}
		
		return actions;
	}
	
	public static class IOTriggerProvider implements ITriggerProvider {

		@Override
		public LinkedList<ITrigger> getPipeTriggers(IPipe pipe) {
			return null;
		}

		@Override
		public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
			if (tile instanceof TileEntityIO) {
				return getTriggersForIO((TileEntityIO) tile);
			}
			
			return null;
		}
		
	}
	
	public static class IOActionProvider implements IActionProvider {

		@Override
		public LinkedList<IAction> getNeighborActions(Block block, TileEntity tile) {
			if (tile instanceof TileEntityIO) {
				return getActionsForIO((TileEntityIO) tile);
			}
			
			return null;
		}
		
	}
	
}
