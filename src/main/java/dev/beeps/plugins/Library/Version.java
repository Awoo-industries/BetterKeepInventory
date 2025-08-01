package dev.beeps.plugins.Library;

import java.io.IOException;
import java.util.Properties;

public class Version {
    public static String getCommitHash() {
        Properties properties = new Properties();
        try {
            properties.load(Version.class.getClassLoader().getResourceAsStream("application.properties"));
            return properties.getProperty("git.commit.id.abbrev");
        } catch (IOException e) {
            e.printStackTrace();
            return "unknown";
        }
    }
}
