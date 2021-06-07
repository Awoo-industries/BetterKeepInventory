package dev.beeps.plugins.Commands;

import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CmdMain implements CommandExecutor {

    BetterKeepInventory plugin;

    public CmdMain(BetterKeepInventory main){
        plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player ply = (Player) sender;

            if(!ply.hasPermission("betterkeepinventory.help")){
                sender.sendMessage(ChatColor.RED + "Missing Permission: betterkeepinventory.reload");
                return true;
            }

        }

        if(args.length == 0){
            return sendHelp(sender, command, label, args);
        }

        if(args[0].equals("reload")){
            return reload(sender, command, label, args);
        }else{
            return sendHelp(sender, command, label, args);
        }

    }

    private boolean sendHelp(CommandSender sender, Command command, String label, String[] args){
        sendPluginInfo(sender);
        sender.sendMessage(ChatColor.GRAY + "");
        sender.sendMessage(ChatColor.BLUE + "/betterki reload");
        sender.sendMessage(ChatColor.AQUA + "Reloads the plugin");
        return true;
    }

    private void sendPluginInfo(CommandSender sender){
        sender.sendMessage(ChatColor.GRAY + "=================================================");
        sender.sendMessage(ChatColor.AQUA + plugin.getDescription().getName() + " " + ChatColor.YELLOW + ChatColor.ITALIC + " (Version " + plugin.getDescription().getVersion() + ")");
        sender.sendMessage(ChatColor.GRAY + "");

        if(!plugin.config.getBoolean("enabled")){
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Plugin is disabled in config !!");
            return;
        }

        sender.sendMessage(ChatColor.AQUA + "Enabled: " + ChatColor.GREEN + "YES");
        sender.sendMessage(ChatColor.AQUA + "Equipment damage: " + ChatColor.YELLOW + plugin.config.getInt("min_damage_pct") + "% ~ " + plugin.config.getInt("max_damage_pct") + "%");
        sender.sendMessage(ChatColor.AQUA + "Using Enchants: " + (plugin.config.getBoolean("ignore_enchants") ? ChatColor.RED + "IGNORING" : ChatColor.GREEN + "USING"));
        sender.sendMessage(ChatColor.AQUA + "Destroy Items: " + (!plugin.config.getBoolean("dont_break_items") ? ChatColor.RED + "CAN DESTROY" : ChatColor.GREEN + "ALWAYS KEEP 1 USE"));

    }

    private boolean reload(CommandSender sender, Command command, String label, String[] args){

        if (sender instanceof Player) {
            Player ply = (Player) sender;
            if(!ply.hasPermission("betterkeepinventory.reload")){
                sender.sendMessage(ChatColor.RED + "Missing Permission: betterkeepinventory.reload");
                return true;
            }
        }

        plugin.reloadConfig();
        plugin.config = plugin.getConfig();

        sender.sendMessage(ChatColor.AQUA + "Reloaded!");
        sendPluginInfo(sender);


        return true;
    }


}
