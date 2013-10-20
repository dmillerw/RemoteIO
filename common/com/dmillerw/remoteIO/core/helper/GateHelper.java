package com.dmillerw.remoteIO.core.helper;

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

public class GateHelper {

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
				Field triggerProviderF = actionManager.getDeclaredField(BC_TRIGGER_PROVIDER_LIST);
				triggerProviderF.setAccessible(true);
				Field actionProviderF = actionManager.getDeclaredField(BC_ACTION_PROVIDER_LIST);
				actionProviderF.setAccessible(true);
				LinkedList<ITriggerProvider> triggerProviders = (LinkedList<ITriggerProvider>) triggerProviderF.get(actionManager);
				LinkedList<IActionProvider> actionProviders = (LinkedList<IActionProvider>) actionProviderF.get(actionManager);
				ITriggerProvider firstTrigger = triggerProviders.getFirst();
				IActionProvider firstAction = actionProviders.getFirst();
				
				triggerProviders.add(0, new IOTriggerProvider());
				actionProviders.add(0, new IOActionProvider());
				triggerProviders.add(firstTrigger);
				actionProviders.add(firstAction);
			}
		} catch(Exception ex) {
			FMLLog.log(Level.WARNING, "[RemoteIO] Failed to register main trigger provider!", new Object[0]);
		}
	}
	
	public static LinkedList<ITrigger> getTriggersForIO(TileEntity tile) {
		LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();
		
		try {
			Class actionManager = Class.forName(BC_ACTION_MANAGER_CLASS);
			Field triggerProviders = actionManager.getDeclaredField(BC_TRIGGER_PROVIDER_LIST);
			triggerProviders.setAccessible(true);
			LinkedList<ITriggerProvider> providers = (LinkedList<ITriggerProvider>) triggerProviders.get(actionManager);

			for (ITriggerProvider provider : providers) {
				if (provider != null && !(provider instanceof IOTriggerProvider)) {
					LinkedList<ITrigger> providedTriggers = provider.getNeighborTriggers(tile.blockType, tile);
					
					if (providedTriggers != null) {
						for (ITrigger trigger : providedTriggers) {
							if (trigger != null) {
								triggers.add(TriggerHelper.wrapTrigger(trigger));
							}
						}
					}
				}
			}
		} catch(Exception ex) {
			FMLLog.log(Level.WARNING, "[RemoteIO] Failed to grab triggers for IO block connection!", new Object[0]);
			ex.printStackTrace();
		}
		
		return triggers;
	}

	public static LinkedList<IAction> getActionsForIO(TileEntity tile) {
		LinkedList<IAction> actions = new LinkedList<IAction>();
		
		try {
			Class actionManager = Class.forName(BC_ACTION_MANAGER_CLASS);
			Field actionProviders = actionManager.getDeclaredField(BC_ACTION_PROVIDER_LIST);
			actionProviders.setAccessible(true);
			LinkedList<IActionProvider> providers = (LinkedList<IActionProvider>) actionProviders.get(actionManager);

			for (IActionProvider provider : providers) {
				if (provider != null && !(provider instanceof IOActionProvider)) {
					LinkedList<IAction> providedActions = provider.getNeighborActions(tile.blockType, tile);
					
					if (providedActions != null) {
						for (IAction action : providedActions) {
							if (action != null) {
								actions.add(action);
							}
						}
					}
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
				return getTriggersForIO(((TileEntityIO)tile).getTileEntity());
			}
			
			return null;
		}
		
	}
	
	public static class IOActionProvider implements IActionProvider {

		@Override
		public LinkedList<IAction> getNeighborActions(Block block, TileEntity tile) {
			if (tile instanceof TileEntityIO) {
				return getActionsForIO(((TileEntityIO)tile).getTileEntity());
			}
			
			return null;
		}
		
	}
	
}
