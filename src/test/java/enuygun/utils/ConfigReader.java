package enuygun.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    static {
        try {
            properties = new Properties();
            FileInputStream file = new FileInputStream(CONFIG_PATH);
            properties.load(file);
            file.close();
        } catch (Exception e) {
            throw new RuntimeException("Config file not found: " + CONFIG_PATH);
        }
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public static String getBaseUrl() {
        return getProperty("base.url", "https://www.enuygun.com/");
    }

    public static int getTimeout() {
        return Integer.parseInt(getProperty("timeout", "10"));
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}