package org.fernice.refract;

import org.fernice.refract.internal.Reflection;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Modules {

    public static final @NotNull String ALL_UNNAMED = "ALL_UNNAMED";
    public static final @NotNull String EVERYONE = "EVERYONE";

    private static final Module ALL_UNNAMED_MODULE;
    private static final Module EVERYONE_MODULE;

    static {
        try {
            Field allUnnamedModuleField = Reflection.getDeclaredField(Module.class, "ALL_UNNAMED_MODULE");
            Reflection.setAccessible(allUnnamedModuleField, true);
            ALL_UNNAMED_MODULE = (Module) allUnnamedModuleField.get(Module.class);

            Field everyoneModuleField = Reflection.getDeclaredField(Module.class, "EVERYONE_MODULE");
            Reflection.setAccessible(everyoneModuleField, true);
            EVERYONE_MODULE = (Module) everyoneModuleField.get(Module.class);
        } catch (Exception exception) {
            throw new IncompatibleImplementationException(exception);
        }
    }

    public static @NotNull Module getModule(@NotNull String module) {
        if (module.equals(EVERYONE)) {
            return EVERYONE_MODULE;
        } else if (module.equals(ALL_UNNAMED)) {
            return ALL_UNNAMED_MODULE;
        } else {
            return ModuleLayer.boot().findModule(module)
                    .orElseThrow(() -> new IllegalArgumentException("cannot find module named '" + module + "'"));
        }
    }

    private static boolean isSpecialModule(@NotNull Object module) {
        return module == EVERYONE_MODULE || module == ALL_UNNAMED_MODULE;
    }

    public static void addExports(@NotNull String module, @NotNull String packageName, @NotNull String targetModule) {
        addExports(getModule(module), packageName, getModule(targetModule));
    }

    public static void addExports(@NotNull Module module, @NotNull String packageName, @NotNull Module targetModule) {
        addExportsOrOpens(module, packageName, targetModule, false);
    }

    public static void addOpens(@NotNull String module, @NotNull String packageName, @NotNull String targetModule) {
        addOpens(getModule(module), packageName, getModule(targetModule));
    }

    public static void addOpens(@NotNull Module module, @NotNull String packageName, @NotNull Module targetModule) {
        addExportsOrOpens(module, packageName, targetModule, true);
    }

    private static void addExportsOrOpens(@NotNull Module module, @NotNull String packageName, @NotNull Module targetModule, boolean opens) {
        try {
            if (isSpecialModule(module)) {
                String operation = opens ? "opens" : "exports";
                throw new IllegalArgumentException("cannot add " + operation + " to special module " + module);
            }

            Method method = Module.class.getDeclaredMethod("implAddExportsOrOpens", String.class, Module.class, boolean.class, boolean.class);
            Reflection.setAccessible(method, true);
            method.invoke(module, packageName, targetModule, opens, true);
        } catch (Exception exception) {
            throw new IncompatibleImplementationException(exception);
        }
    }

    private Modules() {
    }
}
