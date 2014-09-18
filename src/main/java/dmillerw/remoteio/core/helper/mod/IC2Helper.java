package dmillerw.remoteio.core.helper.mod;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dmillerw.remoteio.lib.DependencyInfo;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author dmillerw
 */
public class IC2Helper {

    public static void loadEnergyTile(TileEntity tileEntity) {
        if (Loader.isModLoaded(DependencyInfo.ModIds.IC2)) {
            internalLoadEnergyTile(tileEntity);
        }
    }

    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    private static void internalLoadEnergyTile(TileEntity tileEntity) {
        MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) tileEntity));
    }

    public static void unloadEnergyTile(TileEntity tileEntity) {
        if (Loader.isModLoaded(DependencyInfo.ModIds.IC2)) {
            internalUnloadEnergyTile(tileEntity);
        }
    }

    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    private static void internalUnloadEnergyTile(TileEntity tileEntity) {
        MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) tileEntity));
    }
}
