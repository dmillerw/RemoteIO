package me.dmillerw.remoteio.lib.property;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by dmillerw
 */
public class UnlistedString implements IUnlistedProperty<String> {

    private final String name;
    public UnlistedString(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(String value) {
        return true;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String valueToString(String value) {
        return value;
    }
}
