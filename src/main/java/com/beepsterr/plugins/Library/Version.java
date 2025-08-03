package com.beepsterr.plugins.Library;

import com.beepsterr.plugins.BetterKeepInventory;

import java.io.IOException;
import java.util.Properties;

public class Version {

    public final int major;
    public final int minor;
    public final int patch;
    public final String channel;

    public Version(String version) {
        String[] parts = version.split("\\.");
        if (parts.length >= 3) {
            this.major = Integer.parseInt(parts[0]);
            this.minor = Integer.parseInt(parts[1]);
            this.patch = Integer.parseInt(parts[2].split("-")[0]); // Handle potential release channel
            this.channel = parts[2].contains("-") ? parts[2].split("-")[1].toUpperCase() : "STABLE";
        } else {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }
    }

    public static String getVersionWithoutChannel(){
        String version = BetterKeepInventory.getInstance().getDescription().getVersion();
        if (version.contains("-")) {
            return version.split("-")[0];
        }
        return version;
    }

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
