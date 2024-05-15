package org.fernice.refract.internal;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Libraries {

    public static void load(@NotNull String name) {
        try {
            String libraryName = System.mapLibraryName(name + "-" + libraryArchitecture());

            String resource = "/org/fernice/refract/lib/" + libraryName;
            URL url = Libraries.class.getResource(resource);

            if (url == null) {
                throw new IllegalStateException("cannot find native library: " + resource);
            }

            Path path;
            if (!url.toString().contains("!")) {
                path = Paths.get(url.toURI());
            } else {
                String prefix = substringBeforeLast(libraryName, '.', libraryName);
                String suffix = substringAfterLast(libraryName, '.', "");

                Path file = Files.createTempFile(prefix, !suffix.isEmpty() ? "." + suffix : "");
                file.toFile().deleteOnExit();

                try (InputStream inputStream = url.openStream();
                     OutputStream outputStream = Files.newOutputStream(file)) {
                    copy(inputStream, outputStream);
                }

                path = file.toAbsolutePath();
            }

            System.load(path.toString());
        } catch (Throwable e) {
            throw new RuntimeException("cannot load native libraries", e);
        }
    }

    private static @NotNull String libraryArchitecture() {
        String architecture = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        if (architecture.contains("amd64")) {
            return "x86_64";
        } else if (architecture.contains("x86")) {
            if (architecture.contains("64")) {
                return "x86_64";
            } else {
                return "x86";
            }
        } else if (architecture.contains("aarch64")) {
            return "arm_64";
        } else if (architecture.contains("arm")) {
            if (architecture.contains("64")) {
                return "arm_64";
            } else {
                return "arm";
            }
        } else {
            throw new IllegalStateException("unsupported architecture: " + architecture);
        }
    }

    private static @NotNull String substringBeforeLast(
            @NotNull String value,
            char delimiter,
            @NotNull String missingDelimiterValue
    ) {
        int index = value.lastIndexOf(delimiter);
        return index == -1 ? missingDelimiterValue : value.substring(0, index);
    }

    private static @NotNull String substringAfterLast(
            @NotNull String value,
            char delimiter,
            @NotNull String missingDelimiterValue
    ) {
        int index = value.lastIndexOf(delimiter);
        return index == -1 ? missingDelimiterValue : value.substring(index + 1);
    }

    private static void copy(@NotNull InputStream inputStream, @NotNull OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int n;
        while ((n = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, n);
        }
    }
}
