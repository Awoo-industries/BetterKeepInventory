package com.beepsterr.plugins.Depends;

import com.beepsterr.plugins.BetterKeepInventory;
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

    public double getPlayerBalance(Player ply){
        return economyProvider.getProvider().getBalance(ply);
    }

    public String format(double amount){
        return economyProvider.getProvider().format(amount);
    }

    public boolean takeMoney(Player ply, Double amount){
        EconomyResponse r = economyProvider.getProvider().withdrawPlayer(ply, amount);
        return r.transactionSuccess();
    }

    public boolean grantMoney(Player ply, Double amount){
        EconomyResponse r = economyProvider.getProvider().depositPlayer(ply, amount);
        return r.transactionSuccess();
    }


}
