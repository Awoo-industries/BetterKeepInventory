package dev.beeps.plugins;

/*
    COPYRIGHT (c) 2021 Jill "BeepSterr" Megens
    @author BeepSterr
 */

import dev.beeps.plugins.Commands.CmdMain;
import dev.beeps.plugins.Events.OnPlayerDeath;
import dev.beeps.plugins.Events.OnPlayerRespawn;
import dev.beeps.plugins.Events.PlayerDeath;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

public final class BetterKeepInventory extends JavaPlugin implements Listener {

    public int[] armorSlots = new int[]{ 36,37,38,39 };
    public int[] hotbarSlots = new int[]{ 0,1,2,3,4,5,6,7,8,40 };

    public FileConfiguration _config = getConfig();
    public BetterConfig config;

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
}
