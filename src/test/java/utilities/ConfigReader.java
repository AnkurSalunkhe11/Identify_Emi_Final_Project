package utilities;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                System.err.println("config.properties not found on classpath");
            }
        } catch (Exception e) {
            System.err.println("Failed to load config.properties: " + e.getMessage());
        }
    }

    public static String getAppURL() {
        return props.getProperty("appURL", "https://emicalculator.net/");
    }

    public static String getBrowser() {
        return props.getProperty("browser", "chrome");
    }

    public static String getExecutionEnv() {
        return props.getProperty("execution_env", "local");
    }
}

