package com.dmillerw.remoteIO.core.handler;

import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.util.Icon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.ForgeSubscribe;

/**
 * Created by Dylan Miller on 1/17/14
 */
public class IconHandler {

    public static final IconHandler INSTANCE = new IconHandler();

    public Icon errorIcon;

    @ForgeSubscribe
    public void onMapStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            System.out.println("STICH!!!");
            errorIcon = event.map.registerIcon(ModInfo.RESOURCE_PREFIX + "misc/error");
        }
    }

}
