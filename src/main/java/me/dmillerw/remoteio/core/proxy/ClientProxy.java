package me.dmillerw.remoteio.core.proxy;

import me.dmillerw.remoteio.client.model.loader.BaseModelLoader;
import me.dmillerw.remoteio.client.render.RenderTileRemoteInterface;
import me.dmillerw.remoteio.network.player.ClientProxyPlayer;
import me.dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

/**
 * Created by dmillerw
 */
public class ClientProxy extends CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ModelLoaderRegistry.registerLoader(new BaseModelLoader());

        ClientRegistry.bindTileEntitySpecialRenderer(TileRemoteInterface.class, new RenderTileRemoteInterface());

//        RenderingRegistry.registerEntityRenderingHandler(EntityItemEnderPearl.class, new IRenderFactory<EntityItemEnderPearl>() {
//            @Override
//            public Render<? super EntityItemEnderPearl> createRenderFor(RenderManager manager) {
//                return new RenderEntityItem(manager, Minecraft.getMinecraft().getRenderItem());
//            }
//        });
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (entityPlayer instanceof EntityPlayerMP) {
            return super.onBlockActivated(world, pos, state, entityPlayer, hand, heldItem, side, hitX, hitY, hitZ);
        } else {
            EntityPlayerSP entityPlayerSP = (EntityPlayerSP) entityPlayer;
            ClientProxyPlayer proxyPlayer = new ClientProxyPlayer(entityPlayerSP);
            proxyPlayer.inventory = entityPlayerSP.inventory;
            proxyPlayer.inventoryContainer = entityPlayerSP.inventoryContainer;
            proxyPlayer.openContainer = entityPlayerSP.openContainer;
            proxyPlayer.movementInput = entityPlayerSP.movementInput;

            //SoundHandler.INSTANCE.translateNextSound(x, y, z); //TODO: implement sound handler

            if (heldItem != null) {
                if (heldItem.getItem().onItemUseFirst(heldItem, proxyPlayer, world, pos, side, hitX, hitY, hitZ, hand) == EnumActionResult.SUCCESS) {
                    return false; // I think?
                }
            }
            boolean result = state.getBlock().onBlockActivated(proxyPlayer.worldObj, pos, state, proxyPlayer, hand, heldItem, side, hitX, hitY, hitZ);

            if (entityPlayerSP.openContainer != proxyPlayer.openContainer) {
                entityPlayerSP.openContainer = proxyPlayer.openContainer;
            }

            return result;
        }
    }
}
