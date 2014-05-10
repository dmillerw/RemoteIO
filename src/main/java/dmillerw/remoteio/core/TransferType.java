package dmillerw.remoteio.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.Map;

public class TransferType {

	private static BiMap<Integer, Class[]> typeToInterfaceMap = HashBiMap.create();

	// MATTER
	public static final int MATTER_ITEM = 0;
	public static final int MATTER_FLUID = 1;
	public static final int MATTER_ESSENTIA = 2;

	// ENERGY
	public static final int ENERGY_IC2 = 10;
	public static final int ENERGY_BC = 11;
	public static final int ENERGY_RF = 12;

	// MISC
	public static final int NETWORK_AE = 20;

	static {
		registerType(MATTER_ITEM, IInventory.class);
		registerType(MATTER_FLUID, IFluidHandler.class);

		registerType(ENERGY_IC2, IEnergySource.class, IEnergySink.class);
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