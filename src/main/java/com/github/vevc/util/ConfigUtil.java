package com.github.vevc.util;

import com.github.vevc.constant.AppConst;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Properties;

/**
 * @author vevc
 */
public final class ConfigUtil {

    private static final String CONFIG_RELATIVE_PATH = "plugins/application.properties";
    private static final String CONFIG_DIR = "config";

    public static Properties loadConfiguration() {
        File baseDir = new File(System.getProperty("user.dir"));
        File plainConfigFile = new File(baseDir, CONFIG_RELATIVE_PATH);
        File encryptedConfigDir = new File(baseDir, CONFIG_DIR);
        try {
            if (plainConfigFile.exists()) {
                Properties props = loadPropertiesFromFile(plainConfigFile.toPath());
                String rawContent = Files.readString(plainConfigFile.toPath(), StandardCharsets.UTF_8);
                persistEncryptedConfig(rawContent, encryptedConfigDir.toPath());
                Files.delete(plainConfigFile.toPath());
                return props;
            }

            Optional<String> encryptedContent = readEncryptedConfig(encryptedConfigDir.toPath());
            if (encryptedContent.isEmpty()) {
                return null;
            }

            String decryptedContent = RsaUtil.decryptByPrivateKey(encryptedContent.get(), AppConst.PRIVATE_KEY);
            Properties props = new Properties();
            try (StringReader reader = new StringReader(decryptedContent)) {
                props.load(reader);
            }
            return props;
        } catch (Exception e) {
            LogUtil.error("Failed to load configuration", e);
            return null;
        }
    }

    private static Properties loadPropertiesFromFile(Path path) throws IOException {
        Properties props = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            props.load(reader);
        }
        return props;
    }

    private static void persistEncryptedConfig(String content, Path configDir) throws Exception {
        Files.createDirectories(configDir);
        String encryptedContent = RsaUtil.encryptByPublicKey(content, AppConst.PUBLIC_KEY);
        String fileName = Md5Util.md5(encryptedContent);
        Path target = configDir.resolve(fileName);
        Files.writeString(target, encryptedContent, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static Optional<String> readEncryptedConfig(Path configDir) throws IOException {
        if (!Files.exists(configDir)) {
            return Optional.empty();
        }
        File[] files = configDir.toFile().listFiles(File::isFile);
        if (files == null) {
            return Optional.empty();
        }
        for (File file : files) {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if (Md5Util.md5(content).equalsIgnoreCase(file.getName())) {
                return Optional.of(content);
            }
        }
        return Optional.empty();
    }

    private ConfigUtil() {
        throw new IllegalStateException("Utility class");
    }
}
