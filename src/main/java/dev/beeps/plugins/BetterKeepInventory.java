package dev.beeps.plugins;

/*
    COPYRIGHT (c) 2021 Jill "BeepSterr" Megens
    @author BeepSterr
 */

import dev.beeps.plugins.Commands.CmdMain;
import dev.beeps.plugins.Events.OnPlayerDeath;
import dev.beeps.plugins.Events.OnPlayerRespawn;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.logging.Level;

public final class BetterKeepInventory extends JavaPlugin implements Listener {

    public int[] armorSlots = new int[]{ 36,37,38,39 };
    public int[] hotbarSlots = new int[]{ 0,1,2,3,4,5,6,7,8,40 };

    public FileConfiguration _config = getConfig();
    public BetterConfig config;

    public Random randomizer = new Random();

    // Cache maps
    public Map<UUID, Integer> hungerMap = new HashMap<UUID, Integer>();
    public Map<UUID, ArrayList<PotionEffect>> potionMap = new HashMap<UUID, ArrayList<org.bukkit.potion.PotionEffect>>();

    @Override
    public void onEnable() {

        config = new BetterConfig(this, _config);

        // event handlers
        getServer().getPluginManager().registerEvents(new OnPlayerDeath(this), this);
        getServer().getPluginManager().registerEvents(new OnPlayerRespawn(this), this);

        // command handlers
        Objects.requireNonNull(this.getCommand("betterkeepinventory")).setExecutor(new CmdMain(this));

        // misc
        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 11596);

    }

    @Override
    public void onDisable() {

    }

    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }

    public void log(Level level, Player cause, String message){

        if(level == Level.FINE){
            if(!config.getBoolean("main.debug")){
                return;
            }
        }

        getLogger().log(Level.INFO, String.format("[%s] %s", cause.getDisplayName(), message));

    }

    public void log(Level level, String source, String message){

        if(level == Level.FINE){
            if(!config.getBoolean("main.debug")){
                return;
            }
        }

        getLogger().log(Level.INFO, String.format("[%s] %s", source, message));

    }

    public boolean checkDependency(String dep){
        return Bukkit.getServer().getPluginManager().getPlugin(dep) != null;
    }

}
