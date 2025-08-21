package it.lorenzoangelino.aircrowd.common.utils;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FileUtils {
    private static final Logger LOGGER = LogManager.getLogger(FileUtils.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getFile(String parent, String name, String extension) {
        String filename = String.format("%s%s", name, extension == null ? "" : String.format(".%s", extension));
        File file = new File(parent, filename);
        if (!file.exists() && !file.isDirectory()) {
            file.getParentFile().mkdirs();
            try (InputStream in = ConfigProvider.class.getClassLoader().getResourceAsStream(filename)) {
                if (in != null) Files.copy(in, file.toPath());
            } catch (IOException e) {
                LOGGER.warn(String.format("Unable to copy default content of %s.", filename));
            }
        }
        return file;
    }
}
