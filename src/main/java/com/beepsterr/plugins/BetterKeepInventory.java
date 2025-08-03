package com.beepsterr.plugins;

import com.beepsterr.plugins.Commands.MainCommand;
import com.beepsterr.plugins.Depends.BetterKeepInventoryPlaceholderExpansion;
import com.beepsterr.plugins.Events.OnPlayerDeath;
import com.beepsterr.plugins.Exceptions.ConfigurationException;
import com.beepsterr.plugins.Library.Config;
import com.beepsterr.plugins.Library.Versions.Version;
import com.beepsterr.plugins.Library.Versions.VersionChannel;
import com.beepsterr.plugins.Library.Versions.VersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;

public final class BetterKeepInventory extends JavaPlugin implements Listener {

    public Version version = new Version(getDescription().getVersion());
    public VersionChecker versionChecker;
    public Random rng = new Random();
    public Config config;
    static public BetterKeepInventory instance;

    @Override
    public void onEnable() {

        instance = this;

        try {
            config = new Config(getConfig());
        }catch (ConfigurationException e){
            CrashAndDisable("Configuration failed to load!\n" + e.getMessage() + "\nCaused at path: " + e.path);
            return;
        }

        if(config.getNotifyChannel() != VersionChannel.NONE){
            versionChecker = new VersionChecker(config.getNotifyChannel());
        }

        // event handlers
        getServer().getPluginManager().registerEvents(new OnPlayerDeath(this), this);

        // Command registration
        Objects.requireNonNull(this.getCommand("betterkeepinventory")).setExecutor(new MainCommand());

        // misc
        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 11596);

        // Enable PAPI Integration
        if(checkDependency("PlaceholderAPI")){
            log("Hello PlaceholderAPI!");
            new BetterKeepInventoryPlaceholderExpansion().register();
        }

        // Enable PAPI Integration
        if(checkDependency("Vault")){
            log("Hello Vault!");
        }

        // Enable PAPI Integration
        if(checkDependency("Towny")){
            log("Hello Towny!");
        }

    }

    @Override
    public void onDisable() {
        // Cancel version checks (not sure if needed in onDisable? but can't hurt. (hopefully)
        if(versionChecker != null){
            versionChecker.CancelCheck();
        }
    }

    public static BetterKeepInventory getInstance(){
        return instance;
    }

    public boolean checkDependency(String dep){
        return Bukkit.getServer().getPluginManager().getPlugin(dep) != null;
    }

    public void CrashAndDisable(String message) {
        String alert = "\n" +
                ChatColor.DARK_RED + "=====================[ CRITICAL ERROR ]=====================\n"
                + ChatColor.RED +  "BetterKeepInventory encountered a irrecoverable error:\n\n"
                + ChatColor.YELLOW +  message + "\n\n"
                + ChatColor.RED + "The plugin has been disabled, and deaths will be handled by vanilla (!!)\n"
                + ChatColor.RED + "You should fix the issue, and restart the server to re-enable the plugin\n"
                + ChatColor.DARK_RED + "============================================================\n";

        getServer().getPluginManager().disablePlugin(this);
        getLogger().log(Level.SEVERE, alert);
        getServer().getConsoleSender().sendMessage(alert);
        getLogger().log(Level.INFO, "Continuing with server start in 10 seconds...");

        try{
            // Intentionally introduces a large delay during startup to hopefully catch the administrator's attention
            // Because this plugin is critical to death handling, we want to make sure the admin sees the error
            // This method should only be called if the plugin CANNOT continue working.
            Thread.sleep(10000);
        }catch (InterruptedException e){
           // do nothing, we're crashing anyway
        }

        // Disable the plugin
        getServer().getPluginManager().disablePlugin(this);

    }

    public void debug(Player player, String message){
        if(config.isDebug()){
            log("[DEBUG] (" + player.getName() + ") " + message);
        }
    }

    public void debug(String message){
        if(config.isDebug()){
            getLogger().log(Level.INFO, "[DEBUG] " + message);
        }
    }

    public void log(String message){
        getLogger().log(Level.INFO, message);
    }

}
