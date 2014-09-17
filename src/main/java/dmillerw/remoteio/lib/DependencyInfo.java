package dmillerw.remoteio.lib;

/**
 * @author dmillerw
 */
public class DependencyInfo {

    public static final class ModIds {
        public static final String COFH_API = "CoFHAPI";
        public static final String THAUMCRAFT = "Thaumcraft";
        public static final String IC2 = "IC2";
    }

    public static final class Paths {
        public static final class COFH {
            public static final String IENERGYHANDLER = "cofh.api.energy.IEnergyHandler";
        }

        public static final class IC2 {
            private static final String COMMON_PATH = "ic2.api.tile.";
            public static final String IWRENCHABLE = COMMON_PATH + "IWrenchable";
            public static final String IENERGYSTORAGE = COMMON_PATH + "IEnergyStorage";
            public static final String IENERGYSINK = COMMON_PATH + "energy.IEnergySink";
            public static final String IENERGYSOURCE = COMMON_PATH + "energy.IEnergySource";
        }

        public static final class Thaumcraft {
            private static final String COMMON_PATH = "thaumcraft.api.";
            public static final String IESSENTIATRANSPORT = COMMON_PATH + "aspects.IEssentiaTransport";
            public static final String IASPECTSOURCE = COMMON_PATH + "aspects.IAspectSource";
            public static final String IASPECTCONTAINER = COMMON_PATH + "aspects.IAspectContainer";
            public static final String IWANDABLE = COMMON_PATH + "wands.IWandable";
        }
    }
}
