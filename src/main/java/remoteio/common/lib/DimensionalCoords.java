package remoteio.common.lib;

import remoteio.common.RemoteIO;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class DimensionalCoords {

    public static DimensionalCoords create(TileEntity tile) {
        return new DimensionalCoords(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    }

    public static DimensionalCoords create(EntityLivingBase entity) {
        return new DimensionalCoords(entity.worldObj, entity.posX, entity.posY, entity.posZ);
    }


    public static DimensionalCoords fromNBT(NBTTagCompound nbt) {
        return new DimensionalCoords(nbt.getInteger("dimension"), nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
    }

    public int dimensionID;

    public int x;
    public int y;
    public int z;

    public DimensionalCoords(World world, int x, int y, int z) {
        this(world.provider.dimensionId, x, y, z);
    }

    public DimensionalCoords(World world, double x, double y, double z) {
        this(world.provider.dimensionId, x, y, z);
    }

    public DimensionalCoords(int dimensionID, double x, double y, double z) {
        this(dimensionID, (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    public DimensionalCoords(int dimensionID, int x, int y, int z) {
        this.dimensionID = dimensionID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean withinRange(DimensionalCoords coords, int range) {
        int xRange = Math.abs(this.x - coords.x);
        int yRange = Math.abs(this.y - coords.y);
        int zRange = Math.abs(this.z - coords.z);

        return (xRange <= range && yRange <= range && zRange <= range);
    }

    /* WORLD WRAPPERS */

    public boolean inWorld(World world) {
        return world.provider.dimensionId == this.dimensionID;
    }

    public World getWorld() {
        return RemoteIO.proxy.getWorld(dimensionID);
    }

    public boolean blockExists() {
        return getBlock() != null && !getBlock().isAir(getWorld(), x, y, z);
    }

    public boolean blockExists(World world) {
        return getBlock(world) != null && !getBlock().isAir(world, x, y, z);
    }

    public Block getBlock() {
        return getWorld() != null ? getWorld().getBlock(x, y, z) : null;
    }

    public int getMeta() {
        return getWorld() != null ? getWorld().getBlockMetadata(x, y, z) : 0;
    }

    public TileEntity getTileEntity() {
        return getWorld() != null ? getWorld().getTileEntity(x, y, z) : null;
    }

    public Block getBlock(World world) {
        return world.getBlock(x, y, z);
    }

    public int getMeta(World world) {
        return world.getBlockMetadata(x, y, z);
    }

    public TileEntity getTileEntity(World world) {
        return world.getTileEntity(x, y, z);
    }

    public void markForUpdate() {
        if (getWorld() != null) getWorld().markBlockForUpdate(x, y, z);
    }

    /* END */

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("dimension", this.dimensionID);
        nbt.setInteger("x", this.x);
        nbt.setInteger("y", this.y);
        nbt.setInteger("z", this.z);
    }

    public int hashCode() {
        return this.dimensionID & this.x & this.y & this.z;
    }

    public DimensionalCoords copy() {
        return new DimensionalCoords(this.dimensionID, this.x, this.y, this.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DimensionalCoords)) {
            return false;
        }

        return equals((DimensionalCoords)obj);
    }

    public boolean equals(DimensionalCoords coords) {
        return (
                (this.dimensionID == coords.dimensionID) &&
                        (this.x == coords.x) &&
                        (this.y == coords.y) &&
                        (this.z == coords.z)
        );
    }

    @Override
    public String toString() {
        return "[" + dimensionID + " : " + x + ", " + y + ", " + z + "]";
    }
}