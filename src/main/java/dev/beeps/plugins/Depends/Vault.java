package dev.beeps.plugins.Depends;

import dev.beeps.plugins.BetterKeepInventory;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {

    BetterKeepInventory plugin;
    RegisteredServiceProvider<Economy> economyProvider;

    public Vault(BetterKeepInventory main){
        plugin = main;
        economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    }

    public boolean takeMoney(Player ply, Double amount){
        EconomyResponse r = economyProvider.getProvider().withdrawPlayer(ply, plugin.config.getDouble("eco.amount"));
        return r.transactionSuccess();
    }


}
