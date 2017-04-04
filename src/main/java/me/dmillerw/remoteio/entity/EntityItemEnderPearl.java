package me.dmillerw.remoteio.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by dmillerw
 */
public class EntityItemEnderPearl extends EntityItem {

    public EntityItemEnderPearl(World world) {
        super(world);
    }

    public EntityItemEnderPearl(EntityItem entity) {
        super(entity.world, entity.posX, entity.posY, entity.posZ, entity.getEntityItem().copy());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        AxisAlignedBB aabb = new AxisAlignedBB(new BlockPos(this)).addCoord(1, 1, 1);
        List<EntityFallingBlock> entities = world.getEntitiesWithinAABB(EntityFallingBlock.class, aabb);

        for (EntityFallingBlock block : entities) {
            IBlockState state = block.getBlock();
            if (state != null && state.getBlock() == Blocks.ANVIL) {
                ItemStack stack = new ItemStack(Items.REDSTONE, rand.nextInt(5), 0);
                EntityItem entity = new EntityItem(world, posX, posY, posZ, stack);
                entity.setDefaultPickupDelay();

                setDead();

                world.spawnEntity(entity);
            }
        }
    }
}
