package com.beepsterr.plugins.Library.Versions;

import com.beepsterr.plugins.BetterKeepInventory;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {

    public static final String URL_STABLE = "https://raw.githubusercontent.com/BeepSterr/BetterKeepInventory/refs/heads/next/versions/stable.txt";
    public static final String URL_GIT = "https://api.github.com/repos/beepsterr/BetterKeepInventory/commits/next";
    public static final String URL_LATEST = "https://api.spigotmc.org/legacy/update.php?resource=93081";

    public Version foundVersion;
    public VersionChannel channel;
    private int scheduledTask;

    public VersionChecker(VersionChannel channel) {
        this.channel = channel;
        scheduledTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BetterKeepInventory.getInstance(), new Runnable() {
            public void run() {
                Bukkit.getServer().getScheduler().runTaskAsynchronously(BetterKeepInventory.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            foundVersion = getLatestVersion(channel);
                        } catch (IOException e) {
                            BetterKeepInventory.getInstance().log("Failed to check for updates: " + e.getMessage());
                        }
                    }
                });
            }
        }, 0L, 20L * 14400); //check every 4 hours (and on startup)
    }

    public boolean IsUpdateAvailable(){

        if(foundVersion == null){
            return false;
        }

        // Git version check just compares commit hashes
        if(channel == VersionChannel.GIT){
            return foundVersion != null && !foundVersion.commitHash.equals(Version.getCommitHash());
        }

        // For the rest, we have to actually compare
        return foundVersion.Compare(BetterKeepInventory.getInstance().version) > 0;

    }

    private static Version getLatestFromUrl(String urlStr, String... headers) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestProperty("User-Agent", "BetterKeepInventory-VersionChecker");
        for (int i = 0; i < headers.length - 1; i += 2) {
            conn.setRequestProperty(headers[i], headers[i + 1]);
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line = in.readLine();

            // Trim to short SHA if using Git channel
            if (urlStr.equals(URL_GIT) && line != null && line.length() >= 7) {
                line = line.substring(0, 7);
            }

            BetterKeepInventory.getInstance().log("Checking for updates: " + line);
            return new Version(line);
        }
    }

    public static Version getLatestVersion(VersionChannel channel) throws IOException {
        return switch (channel) {
            case GIT -> getLatestFromUrl(URL_GIT, "Accept", "application/vnd.github.sha"); // example extra header
            case LATEST -> getLatestFromUrl(URL_LATEST);
            case STABLE -> getLatestFromUrl(URL_STABLE);
            default -> null;
        };
    }

    public void CancelCheck(){
        Bukkit.getScheduler().cancelTask(scheduledTask);
    }
}
