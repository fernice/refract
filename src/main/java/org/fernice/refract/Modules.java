package org.fernice.refract;

import io.github.toolfactory.narcissus.Narcissus;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public final class Modules {

    private static final Map<String, Object> nameToModule;

    private static final Object ALL_UNNAMED_MODULE;
    private static final Object EVERYONE_MODULE;

    static {
        try {
            Class<?> moduleLayerClass = Reflection.forName("java.lang.ModuleLayer");

            Method bootMethod = Reflection.getDeclaredMethod(moduleLayerClass, "boot");
            Object moduleLayer = Narcissus.invokeStaticMethod(bootMethod);

            Field nameToModuleField = Reflection.getDeclaredField(moduleLayerClass, "nameToModule");
            nameToModule = (Map<String, Object>) Narcissus.getField(moduleLayer, nameToModuleField);

            Class<?> moduleClass = Reflection.forName("java.lang.Module");

            Field allUnnamedModuleField = Reflection.getDeclaredField(moduleClass, "ALL_UNNAMED_MODULE");
            ALL_UNNAMED_MODULE = Narcissus.getStaticField(allUnnamedModuleField);

            Field everyoneModuleField = Reflection.getDeclaredField(moduleClass, "EVERYONE_MODULE");
            EVERYONE_MODULE = Narcissus.getStaticField(everyoneModuleField);
        } catch (Exception exception) {
            throw new IncompatibleImplementationException(exception);
        }
    }

    public static void addExports(@NotNull String module, @NotNull String packageName, @NotNull String targetModule) {
        addExportsOrOpens(module, packageName, targetModule, false);
    }

    public static void addOpens(@NotNull String module, @NotNull String packageName, @NotNull String targetModule) {
        addExportsOrOpens(module, packageName, targetModule, true);
    }

    private static void addExportsOrOpens(@NotNull String module, @NotNull String packageName, @NotNull String targetModule, boolean opens) {
        try {
            Object moduleInstance = getModule(module);

            if (isSpecialModule(moduleInstance)) {
                String operation = opens ? "opens" : "exports";
                throw new IllegalArgumentException("cannot add " + operation + " to special module " + module);
            }

            Object targetModuleInstance = getModule(targetModule);

            Class<?> moduleClass = Reflection.forName("java.lang.Module");
            Method method = Reflection.getDeclaredMethod(moduleClass, "implAddExportsOrOpens", String.class, moduleClass, boolean.class, boolean.class);

            Narcissus.invokeMethod(moduleInstance, method, packageName, targetModuleInstance, opens, true);
        } catch (Exception exception) {
            throw new IncompatibleImplementationException(exception);
        }
    }

    private static @NotNull Object getModule(@NotNull String module) {
        if (module.equals("EVERYONE")) {
            return EVERYONE_MODULE;
        } else if (module.equals("ALL_UNNAMED")) {
            return ALL_UNNAMED_MODULE;
        } else {
            Object moduleInstance = nameToModule.get(module);
            if (moduleInstance != null) {
                return moduleInstance;
            }
        }
        throw new IllegalArgumentException("cannot find module named '" + module + "'");
    }

    private static boolean isSpecialModule(@NotNull Object module) {
        return module == EVERYONE_MODULE || module == ALL_UNNAMED_MODULE;
    }

    private Modules() {
    }
}
