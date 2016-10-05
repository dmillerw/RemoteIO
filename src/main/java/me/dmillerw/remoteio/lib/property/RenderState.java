package me.dmillerw.remoteio.lib.property;

import com.google.common.base.Optional;

import java.util.Map;

/**
 * Created by dmillerw
 */
public class RenderState {

    public static final RenderState BLANK = new RenderState("", -1, false);

    public String block;
    public int state;

    public boolean camouflage;

    public Map<String, Optional<?>> unlistedProperties;

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
