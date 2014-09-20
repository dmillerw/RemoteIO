package dmillerw.remoteio.tile;

import cpw.mods.fml.common.Optional;
import dmillerw.remoteio.lib.DependencyInfo;
import dmillerw.remoteio.tile.core.TileCore;
import ic2.api.energy.tile.IHeatSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author dmillerw
 */
@Optional.Interface(iface = DependencyInfo.Paths.IC2.IHEATSOURCE, modid = DependencyInfo.ModIds.IC2)
public class TileMachineHeater extends TileCore implements IHeatSource {

    public boolean filled = false;

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setBoolean("filled", filled);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        filled = nbt.getBoolean("filled");
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 20 == 0) {
            update();
            if (filled) push();
        }
    }

    @Override
    public void onNeighborUpdated() {
        update();
    }

    private void update() {
        int found = 0;
        for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
            Block block = worldObj.getBlock(xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
            if (block != null && (block == Blocks.lava || block == Blocks.flowing_lava)) found++;
        }
        boolean newFilled = found >= 2;
        if (filled != newFilled) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        filled = newFilled;
    }

    private void push() {
        for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
            if (tileEntity != null && tileEntity instanceof TileEntityFurnace) {
                ((TileEntityFurnace) tileEntity).furnaceBurnTime = 200;
                if (tileEntity.getBlockType() == Blocks.furnace) {
                    BlockFurnace.updateFurnaceBlockState(true, worldObj, xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
                }
            }
        }
    }

    /* IHEATSOURCE */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int maxrequestHeatTick(ForgeDirection directionFrom) {
        return 1;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int requestHeat(ForgeDirection directionFrom, int requestheat) {
        return filled ? requestheat : 0;
    }
}
