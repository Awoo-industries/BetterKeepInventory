package com.beepsterr.plugins;

import com.beepsterr.plugins.Commands.MainCommand;
import com.beepsterr.plugins.Depends.BetterKeepInventoryPlaceholderExpansion;
import com.beepsterr.plugins.Events.OnPlayerDeath;
import com.beepsterr.plugins.Exceptions.ConfigurationException;
import com.beepsterr.plugins.Library.Config;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;

public final class BetterKeepInventory extends JavaPlugin implements Listener {

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

        // event handlers
        getServer().getPluginManager().registerEvents(new OnPlayerDeath(this), this);

        // Command registration
        Objects.requireNonNull(this.getCommand("betterkeepinventory")).setExecutor(new MainCommand());

        // misc
        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 11596);

        // Enable PAPI Integration
        if(checkDependency("PlaceholderAPI")){
            getLogger().log(Level.INFO, "Hello PlaceholderAPI! It's good to see you!");
            new BetterKeepInventoryPlaceholderExpansion().register();
        }

        // Enable PAPI Integration
        if(checkDependency("Vault")){
            getLogger().log(Level.INFO, "Hello Vault! How's the economy?");
        }

        // Enable PAPI Integration
        if(checkDependency("Towny")){
            getLogger().log(Level.INFO, "Hello Towny! How are the kids?");
        }

    }

    @Override
    public void onDisable() {

    }

    public static BetterKeepInventory getInstance(){
        return instance;
    }

    public boolean checkDependency(String dep){
        return Bukkit.getServer().getPluginManager().getPlugin(dep) != null;
    }

    public void CrashAndDisable(String message) {
        String alert = "\n"
                + "=====================[ CRITICAL ERROR ]=====================\n"
                + "BetterKeepInventory encountered a irrecoverable error:\n\n"
                + message + "\n\n"
                + "The plugin has been disabled, and deaths will be handled by vanilla (!!)\n"
                + "You should fix the issue, and restart the server to re-enable the plugin\n"
                + "============================================================\n";

        getServer().getPluginManager().disablePlugin(this);
        getLogger().log(Level.SEVERE, alert);
        getLogger().log(Level.INFO, "Continuing with server start in 10 seconds...");

        try{
            // Pause the server for 5 seconds to allow the message to be read (hopefully)
            // this should only ever be called during startup or by dummies using /reload
            Thread.sleep(10000);
        }catch (InterruptedException e){
           // do nothing, we're crashing anyway
        }

    }

}
