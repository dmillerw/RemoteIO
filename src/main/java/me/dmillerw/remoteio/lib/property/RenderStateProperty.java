package me.dmillerw.remoteio.lib.property;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by dmillerw
 */
public class RenderStateProperty implements IUnlistedProperty<RenderState> {

    private final String name;
    public RenderStateProperty(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(RenderState value) {
        return true;
    }

    @Override
    public Class<RenderState> getType() {
        return RenderState.class;
    }

    @Override
    public String valueToString(RenderState value) {
        return null;
    }
}
