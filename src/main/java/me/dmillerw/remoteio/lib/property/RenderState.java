package me.dmillerw.remoteio.lib.property;

import com.google.common.base.Optional;
import net.minecraft.block.properties.IProperty;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Map;

/**
 * Created by dmillerw
 */
public class RenderState {

    public static final RenderState BLANK = new RenderState("", false);

    public String block;

    public boolean camouflage;

    public Map<IProperty, Comparable> properties;
    public Map<IUnlistedProperty, Optional> unlistedProperties;

    public RenderState(String block, boolean camouflage) {
        this.block = block;
        this.camouflage = camouflage;
    }

    @Override
    public String toString() {
        return "{block: " + block + ", camouflage: " + camouflage + ", properties:" + properties + ", unlisted: " + unlistedProperties + "}";
    }
}
