package org.fernice.refract.internal;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Reflection {

    static {
        Libraries.load("refract");
    }

    public static native void setAccessible(AccessibleObject method, boolean accessible);

    public static @NotNull Field getDeclaredField(@NotNull Class<?> clazz, @NotNull String name) throws NoSuchFieldException {
        for (Field field : getDeclaredFields(clazz)) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        throw new NoSuchFieldException(name);
    }

    public static @NotNull Field[] getDeclaredFields(@NotNull Class<?> clazz) {
        try {
            Method method = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
            setAccessible(method, true);
            return (Field[]) method.invoke(clazz, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("failed to list declared fields of class " + clazz, e);
        }
    }

    private Reflection() {
    }
}
