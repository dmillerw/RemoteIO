package dmillerw.remoteio.core;

import cofh.api.energy.IEnergyHandler;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ic2.api.energy.tile.*;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;

import java.util.Map;

public class TransferType {

	private static BiMap<Integer, Class[]> typeToInterfaceMap = HashBiMap.create();

	// MATTER
	public static final int MATTER_ITEM = 0;
	public static final int MATTER_FLUID = 1;
	public static final int MATTER_ESSENTIA = 2;

	// ENERGY
	public static final int ENERGY_IC2 = 10;
//	public static final int ENERGY_BC = 11; // BC power has since been replaced with RF
	public static final int ENERGY_RF = 12;

	// MISC
	public static final int NETWORK_AE = 20;

	static {
		registerType(MATTER_ITEM, IInventory.class, ISidedInventory.class);
		registerType(MATTER_FLUID, IFluidHandler.class);
		registerType(MATTER_ESSENTIA, IAspectContainer.class, IAspectSource.class, IEssentiaTransport.class);

		registerType(ENERGY_IC2, IEnergyTile.class, IEnergySource.class, IEnergyEmitter.class, IEnergySink.class, IEnergyAcceptor.class, IEnergyStorage.class);
		// ENERGY_BC is handled via tile
		registerType(ENERGY_RF, IEnergyHandler.class);
	}

	public static void registerType(int type, Class ... classes) {
		typeToInterfaceMap.put(type, classes);
	}

	public static int getTypeForInterface(Class cls) {
		for (Map.Entry<Class[], Integer> entry : typeToInterfaceMap.inverse().entrySet()) {
			for (Class clz : entry.getKey()) {
				if (cls == clz) {
					return entry.getValue();
				}
			}
		}
		return -1;
	}
}