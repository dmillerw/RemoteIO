package dmillerw.remoteio.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.Map;

import static dmillerw.remoteio.lib.DependencyInfo.Paths.*;

public class TransferType {

    private static BiMap<Integer, String[]> typeToInterfaceMap = HashBiMap.create();

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
        registerType(MATTER_ESSENTIA, Thaumcraft.IASPECTCONTAINER, Thaumcraft.IASPECTSOURCE, Thaumcraft.IESSENTIATRANSPORT);

        registerType(ENERGY_IC2, IC2.IENERGYTILE, IC2.IENERGYSOURCE, IC2.IENERGYEMITTER, IC2.IENERGYSINK, IC2.IENERGYACCEPTOR, IC2.IENERGYSTORAGE);
        registerType(ENERGY_RF, COFH.IENERGYHANDLER);
    }

    public static void registerType(int type, Class... classes) {
        String[] names = new String[classes.length];
        for (int i=0; i<classes.length; i++) {
            names[i] = classes[i].getName();
        }
        registerType(type, names);
    }

    public static void registerType(int type, String ... classes) {
        typeToInterfaceMap.put(type, classes);
    }

    public static int getTypeForInterface(Class cls) {
        for (Map.Entry<String[], Integer> entry : typeToInterfaceMap.inverse().entrySet()) {
            for (String clz : entry.getKey()) {
                if (cls.getName().equals(clz)) {
                    return entry.getValue();
                }
            }
        }
        return -1;
    }
}