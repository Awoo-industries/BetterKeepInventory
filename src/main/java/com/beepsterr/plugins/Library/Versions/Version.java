package com.beepsterr.plugins.Library.Versions;

import com.beepsterr.plugins.BetterKeepInventory;

import java.io.IOException;
import java.util.Properties;

public class Version {

    public int major = 0;
    public int minor = 0;
    public int patch = 0;
    public String channel;
    public String commitHash;

    private boolean scheduled = false;

    public Version(String versionStr) {
        if (versionStr.matches("\\d+\\.\\d+\\.\\d+(-[A-Za-z]+)?")) {
            String[] parts = versionStr.split("\\.");
            this.major = Integer.parseInt(parts[0]);
            this.minor = Integer.parseInt(parts[1]);
            this.patch = Integer.parseInt(parts[2].split("-")[0]);
            this.channel = parts[2].contains("-") ? parts[2].split("-")[1].toUpperCase() : "STABLE";
            this.commitHash = getCommitHash();
        } else if (versionStr.matches("[a-fA-F0-9]{7,40}")) {
            this.channel = "GIT";
            this.commitHash = versionStr;
        } else {
            throw new IllegalArgumentException("Unknown version format: " + versionStr);
        }
    }

    public int Compare(Version other) {
        int result = Integer.compare(this.major, other.major);
        if (result != 0) return result;

        result = Integer.compare(this.minor, other.minor);
        if (result != 0) return result;

        result = Integer.compare(this.patch, other.patch);
        if (result != 0) return result;

        if (this.commitHash != null && other.commitHash != null) {
            return this.commitHash.equals(other.commitHash) ? 0 : -1;
        }

        return 0;
    }

    public static String getVersionWithoutChannel() {
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

    @Override
    public String toString() {
        if (commitHash != null) return "0.0.0-" + channel + " (" + commitHash + ")";
        return major + "." + minor + "." + patch + "-" + channel;
    }
}
