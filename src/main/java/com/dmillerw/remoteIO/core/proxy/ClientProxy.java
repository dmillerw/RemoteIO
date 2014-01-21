package com.dmillerw.remoteIO.core.proxy;

import com.dmillerw.remoteIO.client.fx.FXParticlePath;
import com.dmillerw.remoteIO.core.handler.IconHandler;
import com.dmillerw.remoteIO.item.ItemGoggles;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.Random;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(IconHandler.INSTANCE);

		super.preInit(event);
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
    public void ioPathFX(World world, TileEntity tile, double tx, double ty, double tz, float speed) {
        if (ItemGoggles.isPlayerWearing(FMLClientHandler.instance().getClient().thePlayer)) {
            Random rand = new Random();
            for (int i=0; i<rand.nextInt(5); i++) {
                FXParticlePath path = new FXParticlePath(world, tile, tx, ty, tz, speed);
                path.setRBGColorF(0.35F, 0.35F, 1F);
                Minecraft.getMinecraft().effectRenderer.addEffect(path);
            }
        }
    }

}
