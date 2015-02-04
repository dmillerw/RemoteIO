package remoteio.common.tile;

import remoteio.common.tile.core.TileCore;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * @author dmillerw
 */
public class TileMachineReservoir extends TileCore implements IFluidHandler {

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
            if (block != null && (block == Blocks.water || block == Blocks.flowing_water)) found++;
        }
        boolean newFilled = found >= 2;
        if (filled != newFilled) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        filled = newFilled;
    }

    private void push() {
        int found = 0;
        for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
            if (tileEntity != null && tileEntity instanceof IFluidHandler) found++;
        }

        final int amount = (int) ((float) FluidContainerRegistry.BUCKET_VOLUME / (float) found);

        for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
            if (tileEntity != null && tileEntity instanceof IFluidHandler) {
                ((IFluidHandler) tileEntity).fill(forgeDirection.getOpposite(), new FluidStack(FluidRegistry.WATER, amount), true);
            }
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return new FluidStack(FluidRegistry.WATER, resource.amount);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return new FluidStack(FluidRegistry.WATER, maxDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{
                new FluidTankInfo(new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME), FluidContainerRegistry.BUCKET_VOLUME)
        };
    }
}
