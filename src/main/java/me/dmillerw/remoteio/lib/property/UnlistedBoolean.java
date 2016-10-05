package me.dmillerw.remoteio.lib.property;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by dmillerw
 */
public class UnlistedBoolean implements IUnlistedProperty<Boolean> {

    private final String name;
    public UnlistedBoolean(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Boolean value) {
        return true;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public String valueToString(Boolean value) {
        return Boolean.toString(value);
    }
}
