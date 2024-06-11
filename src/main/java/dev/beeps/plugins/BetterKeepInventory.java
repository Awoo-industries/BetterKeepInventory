package dev.beeps.plugins;

/*
    COPYRIGHT (c) 2021 Jill "BeepSterr" Megens
    @author BeepSterr
 */

import dev.beeps.plugins.Commands.CmdMain;
import dev.beeps.plugins.Depends.Papi;
import dev.beeps.plugins.Events.OnPlayerDeath;
import dev.beeps.plugins.Events.OnPlayerRespawn;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public final class BetterKeepInventory extends JavaPlugin implements Listener {

    public int[] armorSlots = new int[]{ 36,37,38,39 };
    public int[] hotbarSlots = new int[]{ 0,1,2,3,4,5,6,7,8,40 };

    public int durabilityPointsLost = 0;

    public FileConfiguration _config = getConfig();
    public BetterConfig config;

    public Random randomizer = new Random();

    // Cache maps
    public Map<UUID, Integer> hungerMap = new HashMap<UUID, Integer>();
    public Map<UUID, ArrayList<PotionEffect>> potionMap = new HashMap<UUID, ArrayList<org.bukkit.potion.PotionEffect>>();
    public Map<UUID, Integer> graceMap = new HashMap<UUID, Integer>();

    static public BetterKeepInventory instance;

    @Override
    public void onEnable() {

        instance = this;
        config = new BetterConfig(this, _config);

        // event handlers
        getServer().getPluginManager().registerEvents(new OnPlayerDeath(this), this);
        getServer().getPluginManager().registerEvents(new OnPlayerRespawn(this), this);

        // command handlers
        Objects.requireNonNull(this.getCommand("betterkeepinventory")).setExecutor(new CmdMain(this));

        // misc
        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 11596);

        metrics.addCustomChart(new SingleLineChart("durability_lost", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log(Level.FINE, "Metrics", "durability_lost: " + durabilityPointsLost);
                int amount = durabilityPointsLost;
                durabilityPointsLost = 0;
                return amount;
            }
        }));

        // loops
        startGraceCheck();

        // Enable PAPI Integration
        if(checkDependency("PlaceholderAPI")){
            log(Level.INFO, "PlaceholderAPI found, enabling extension");
            new Papi().register();
        }

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

    public void log(Level level, String message) {
        if(level == Level.FINE){
            if(!config.getBoolean("main.debug")){
                return;
            }
        }
        getLogger().log(Level.INFO, String.format("[%s] %s", "Unknown Player", message));
    }

    public boolean checkDependency(String dep){
        return Bukkit.getServer().getPluginManager().getPlugin(dep) != null;
    }

    public void startGraceCheck()
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<UUID, Integer>> it = graceMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<UUID, Integer> i = it.next();
                    int newval = i.getValue() - 1;
                    if(newval <= 0){
                        it.remove();
                    }else{
                        graceMap.put(i.getKey(), newval);
                    }
                }
            }
        }, 0L, 20L); //0 Tick initial delay, 20 Tick (1 Second) between repeats
    }
}
