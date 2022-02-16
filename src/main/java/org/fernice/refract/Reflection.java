package org.fernice.refract;

import io.github.toolfactory.narcissus.Narcissus;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public final class Reflection {

    /// Class

    private static final StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static @NotNull Class<?> forName(@NotNull String name) throws ClassNotFoundException {
        Class<?> callerClass = stackWalker.getCallerClass();
        return forName(name, true, callerClass.getClassLoader());
    }

    public static @NotNull Class<?> forName(@NotNull String name, boolean initialize, @NotNull ClassLoader classLoader) throws ClassNotFoundException {
        try {
            Method method = getDeclaredMethod(Class.class, "forName0", String.class, boolean.class, ClassLoader.class, Class.class);

            return (Class<?>) Narcissus.invokeStaticMethod(method, name, initialize, classLoader, null);
        } catch (NoSuchMethodException exception) {
            throw new ClassNotFoundException("could not load class " + name, exception);
        }
    }

    /// Field

    public static @NotNull Field getDeclaredField(@NotNull Class<?> clazz, @NotNull String name) throws NoSuchFieldException {
        Field[] fields = Narcissus.getDeclaredFields(clazz);
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        throw new NoSuchFieldException(name);
    }

    /// Method

    public static @NotNull Method getDeclaredMethod(@NotNull Class<?> clazz, @NotNull String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method[] methods = Narcissus.getDeclaredMethods(clazz);
        Method candidate = null;
        for (Method method : methods) {
            if (method.getName().equals(name) && Arrays.equals(parameterTypes, method.getParameterTypes())) {
                if (candidate == null ||
                        candidate.getReturnType() != method.getReturnType() && candidate.getReturnType().isAssignableFrom(method.getReturnType())) {
                    candidate = method;
                }
            }
        }
        if (candidate == null) {
            throw new NoSuchMethodException(methodToString(clazz, name, parameterTypes));
        }
        return candidate;
    }

    private static String methodToString(@NotNull Class<?> clazz, @NotNull String name, Class<?>[] argTypes) {
        return clazz.getName() + '.' + name + ((argTypes == null || argTypes.length == 0) ? "()"
                : Arrays.stream(argTypes).map(c -> c == null ? "null" : c.getName()).collect(Collectors.joining(",", "(", ")")));
    }

    /// Constructor

    public static <T> @NotNull Constructor<T> getDeclaredConstructor(@NotNull Class<T> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        Constructor<T>[] constructors = Narcissus.getDeclaredConstructors(clazz);
        for (Constructor<T> constructor : constructors) {
            if (Arrays.equals(parameterTypes, constructor.getParameterTypes())) {
                return constructor;
            }
        }
        throw new NoSuchMethodException(methodToString(clazz, "<init>", parameterTypes));
    }

    private Reflection() {
    }
}
