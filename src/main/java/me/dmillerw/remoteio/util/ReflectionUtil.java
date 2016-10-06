package me.dmillerw.remoteio.util;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.lang.reflect.Field;

/**
 * Created by dmillerw
 */
public class ReflectionUtil {

    public static ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties(IExtendedBlockState state) {
        ImmutableMap<IUnlistedProperty<?>, Optional<?>> properties = null;
        try {
            Class<?> extendedClass = Class.forName("net.minecraftforge.common.property.ExtendedBlockState$ExtendedStateImplementation");
            Field unlistedProperties = extendedClass.getDeclaredField("unlistedProperties");
            unlistedProperties.setAccessible(true);
            properties = (ImmutableMap<IUnlistedProperty<?>, Optional<?>>) unlistedProperties.get(state);
            unlistedProperties.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void setUnlistedProperties(IExtendedBlockState state, ImmutableMap<IUnlistedProperty<?>, Optional<?>> properties) {
        try {
            Class<?> extendedClass = Class.forName("net.minecraftforge.common.property.ExtendedBlockState$ExtendedStateImplementation");
            Field unlistedProperties = extendedClass.getDeclaredField("unlistedProperties");
            unlistedProperties.setAccessible(true);
            unlistedProperties.set(state, properties);
            unlistedProperties.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
