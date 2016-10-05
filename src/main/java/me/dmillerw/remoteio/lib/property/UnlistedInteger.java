package me.dmillerw.remoteio.lib.property;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by dmillerw
 */
public class UnlistedInteger implements IUnlistedProperty<Integer> {

    private final String name;
    public UnlistedInteger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Integer value) {
        return true;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public String valueToString(Integer value) {
        return Integer.toString(value);
    }
}
