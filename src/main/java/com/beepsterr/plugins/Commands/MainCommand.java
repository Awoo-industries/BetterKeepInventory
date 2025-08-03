package com.beepsterr.plugins.Commands;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Exceptions.ConfigurationException;
import com.beepsterr.plugins.Library.Config;
import com.beepsterr.plugins.Library.Version;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // check if is player and has permission
        if(!(sender.hasPermission("betterkeepinventory.info") || sender instanceof org.bukkit.command.ConsoleCommandSender)){
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if(args.length > 0){
            switch(args[0]){
                case "reload":
                    return OnReloadCommand(sender, command, label, args);
                default:
                    sender.sendMessage(ChatColor.RED + "Unknown command. Use /betterkeepinventory for help.");
                    return true;
            }
        }else{

            BetterKeepInventory plugin = BetterKeepInventory.getInstance();
            sender.sendMessage(ChatColor.AQUA + plugin.getDescription().getName());
            if(sender.hasPermission("betterkeepinventory.info.version") || sender instanceof org.bukkit.command.ConsoleCommandSender){
                sender.sendMessage(ChatColor.GRAY + "Version: " + plugin.getDescription().getVersion() + " (MC " + plugin.getDescription().getAPIVersion() + ") " + ChatColor.DARK_GRAY + Version.getCommitHash());
            }

            sender.sendMessage("");
            sender.sendMessage(ChatColor.YELLOW + "https://www.spigotmc.org/resources/betterkeepinventory.93081/");
            sender.sendMessage("");

            if(sender.hasPermission("betterkeepinventory.reload") || sender instanceof org.bukkit.command.ConsoleCommandSender){
                sender.sendMessage(ChatColor.YELLOW + "/betterkeepinventory reload\n" + ChatColor.GRAY + " to reload the configuration.\n\n");
            }

        }

        return true;
    }

    public boolean OnReloadCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender.hasPermission("betterkeepinventory.reload") || sender instanceof org.bukkit.command.ConsoleCommandSender)){
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        BetterKeepInventory plugin = BetterKeepInventory.getInstance();
        plugin.reloadConfig();

        try {
            plugin.config = new Config(plugin.getConfig());
        }catch (ConfigurationException|IllegalArgumentException e) {

            String alert = "\n"
                    + ChatColor.DARK_RED + ChatColor.BOLD + " RELOAD ERROR!!\n"
                    + ChatColor.RED + "BetterKeepInventory encountered a problem while reloading:\n\n"
                    + ChatColor.YELLOW + e.getMessage() + "\n\n"
                    + ChatColor.RED + "The updated configuration was NOT loaded.\n"
                    + ChatColor.RED + "You should fix the issue, and try again\n";

            sender.sendMessage(alert);
            plugin.getLogger().severe(alert);
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully.");

        return true;

    }

}
