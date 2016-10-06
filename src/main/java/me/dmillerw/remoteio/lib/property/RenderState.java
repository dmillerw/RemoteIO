package me.dmillerw.remoteio.lib.property;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by dmillerw
 */
public class RenderState {

    public static final RenderState BLANK = new RenderState("", -1, false);

    public String block;
    public int state;

    public boolean camouflage;

    public ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties;

    public RenderState(String block, int state, boolean camouflage) {
        this.block = block;
        this.state = state;
        this.camouflage = camouflage;
    }

    @Override
    public String toString() {
        return "{block: " + block + ", state: " + state + ", camouflage: " + camouflage + "unlisted: " + unlistedProperties + "}";
    }
}
