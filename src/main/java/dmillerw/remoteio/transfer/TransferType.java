package dmillerw.remoteio.transfer;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.HashMap;
import java.util.Map;

public class TransferType {

	private static Map<Integer, Class> typeToInterfaceMap = new HashMap<Integer, Class>();

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
		typeToInterfaceMap.put(MATTER_ITEM, IInventory.class);
		typeToInterfaceMap.put(MATTER_FLUID, IFluidHandler.class);
	}

	public static boolean isTypeRegistered(int type) {
		return typeToInterfaceMap.containsKey(type);
	}

	public static Class getInterfaceForType(int type) {
		return typeToInterfaceMap.get(type);
	}

}