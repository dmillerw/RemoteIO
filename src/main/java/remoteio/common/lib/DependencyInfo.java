package remoteio.common.lib;

/**
 * @author dmillerw
 */
public class DependencyInfo {
    public static final class ModIds {
        public static final String COFH_API = "CoFHAPI";
        public static final String THAUMCRAFT = "Thaumcraft";
        public static final String IC2 = "IC2";
        public static final String AE2 = "appliedenergistics2";
    }

    public static final class Paths {
        public static final class COFH {
            public static final String IENERGYHANDLER = "cofh.api.energy.IEnergyHandler";
            public static final String IENERGYRECEIVER = "cofh.api.energy.IEnergyReceiver";
        }

        public static final class IC2 {
            private static final String COMMON_PATH = "ic2.api.";
            public static final String IWRENCHABLE = COMMON_PATH + "tile.IWrenchable";
            public static final String IENERGYSTORAGE = COMMON_PATH + "tile.IEnergyStorage";
            public static final String IENERGYSINK = COMMON_PATH + "energy.tile.IEnergySink";
            public static final String IENERGYACCEPTOR = COMMON_PATH + "energy.tile.IEnergyAcceptor";
            public static final String IENERGYSOURCE = COMMON_PATH + "energy.tile.IEnergySource";
            public static final String IENERGYEMITTER = COMMON_PATH + "energy.tile.IEnergyEmitter";
            public static final String IENERGYTILE = COMMON_PATH + "energy.tile.IEnergyTile";
            public static final String IHEATSOURCE = COMMON_PATH + "energy.tile.IHeatSource";
        }

        public static final class Thaumcraft {
            private static final String COMMON_PATH = "thaumcraft.api.";
            public static final String IESSENTIATRANSPORT = COMMON_PATH + "aspects.IEssentiaTransport";
            public static final String IASPECTSOURCE = COMMON_PATH + "aspects.IAspectSource";
            public static final String IASPECTCONTAINER = COMMON_PATH + "aspects.IAspectContainer";
            public static final String IWANDABLE = COMMON_PATH + "wands.IWandable";
        }

        public static final class AE2 {
            public static final String IGRIDHOST = "appeng.api.networking.IGridHost";
            public static final String IGRIDBLOCK = "appeng.api.networking.IGridBlock";
        }
    }
}
