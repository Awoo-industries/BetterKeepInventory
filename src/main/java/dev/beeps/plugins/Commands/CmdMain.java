package dev.beeps.plugins.Commands;

import dev.beeps.plugins.BetterConfig;
import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CmdMain implements CommandExecutor {

    BetterKeepInventory plugin;

    public CmdMain(BetterKeepInventory main){
        plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0){
            return sendHelp(sender, command, label, args);
        }

        switch(args[0]){

            case "reload":
                return reload(sender, command, label, args);

            case "info":
                return sendPluginInfo(sender);

            case "test":
                return sendTestMessage(sender, command, label, args);

            default:
                sender.sendMessage(ChatColor.RED + "Invalid subcommand");


        }

        return true;

    }

    private boolean sendHelp(CommandSender sender, Command command, String label, String[] args){

        sender.sendMessage(ChatColor.GRAY + "=================================================");
        sender.sendMessage(ChatColor.AQUA + plugin.getDescription().getName() + " " + ChatColor.YELLOW + ChatColor.ITALIC + " (Version " + plugin.getDescription().getVersion() + ")");
        sender.sendMessage(ChatColor.GRAY + "");

        sender.sendMessage(ChatColor.GRAY + "");
        sender.sendMessage(ChatColor.BLUE + "/bki info");
        sender.sendMessage(ChatColor.AQUA + "Gets the current settings for BetterKeepInventory");

        sender.sendMessage(ChatColor.GRAY + "");
        sender.sendMessage(ChatColor.BLUE + "/bki test");
        sender.sendMessage(ChatColor.AQUA + "Tests your location for overrides");

        sender.sendMessage(ChatColor.GRAY + "");
        sender.sendMessage(ChatColor.BLUE + "/bki reload");
        sender.sendMessage(ChatColor.AQUA + "Reloads the plugin");
        return true;
    }

    private boolean sendPluginInfo(CommandSender sender){
        sender.sendMessage(ChatColor.GRAY + "=================================================");
        sender.sendMessage(ChatColor.AQUA + plugin.getDescription().getName() + " " + ChatColor.YELLOW + ChatColor.ITALIC + " (Version " + plugin.getDescription().getVersion() + ")");
        sender.sendMessage(ChatColor.GRAY + "");

        if(!plugin.config.getBoolean("main.enabled")){
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Plugin is disabled in the main section!");
            return true;
        }

        //hotbar

        sender.sendMessage(ChatColor.GRAY + "===== Items");

        sender.sendMessage( "Hotbar: " +
                (Objects.equals(plugin.config.getString("items.hotbar.mode"), "NONE") ? ChatColor.YELLOW + "VANILLA" : ChatColor.GREEN + plugin.config.getString("items.hotbar.mode") )
                        + ChatColor.GRAY + " | "
                        + ChatColor.GRAY + "("+ plugin.config.getDouble("items.hotbar.min") + " ~ "+ plugin.config.getDouble("items.hotbar.max")  + ")"
                        + ChatColor.GRAY + " | "
                        + (plugin.config.getBoolean("items.hotbar.use_enchantments") ? ChatColor.GREEN + " ✔ Enchantments" :  ChatColor.RED + " ✖ Enchantments" )
                        + ChatColor.GRAY + " | "
                        + (plugin.config.getBoolean("items.hotbar.dont_break") ? ChatColor.GREEN + " ✔ Unbreaking" :  ChatColor.RED + " ✖ Unbreaking" )
        );

        //inventory
        sender.sendMessage( "Inventory: " +
                (Objects.equals(plugin.config.getString("items.inventory.mode"), "NONE") ? ChatColor.YELLOW + "VANILLA" : ChatColor.GREEN + plugin.config.getString("items.inventory.mode") )
                        + ChatColor.GRAY + " | "
                        + ChatColor.GRAY + "("+ plugin.config.getDouble("items.inventory.min") + " ~ "+ plugin.config.getDouble("items.inventory.max")  + ")"
                        + ChatColor.GRAY + " | "
                        + (plugin.config.getBoolean("items.inventory.use_enchantments") ? ChatColor.GREEN + " ✔ Enchantments" :  ChatColor.RED + " ✖ Enchantments" )
                        + ChatColor.GRAY + " | "
                        + (plugin.config.getBoolean("items.inventory.dont_break") ? ChatColor.GREEN + " ✔ Unbreaking" :  ChatColor.RED + " ✖ Unbreaking" )
        );

        //armor
        sender.sendMessage( "Armor: " +
                (Objects.equals(plugin.config.getString("items.armor.mode"), "NONE") ? ChatColor.YELLOW + "VANILLA" : ChatColor.GREEN + plugin.config.getString("items.armor.mode") )
                        + ChatColor.GRAY + " | "
                        + ChatColor.GRAY + "("+ plugin.config.getDouble("items.armor.min") + " ~ "+ plugin.config.getDouble("items.armor.max")  + ")"
                        + ChatColor.GRAY + " | "
                        + (plugin.config.getBoolean("items.armor.use_enchantments") ? ChatColor.GREEN + " ✔ Enchantments" :  ChatColor.RED + " ✖ Enchantments" )
                        + ChatColor.GRAY + " | "
                        + (plugin.config.getBoolean("items.armor.dont_break") ? ChatColor.GREEN + " ✔ Unbreaking" :  ChatColor.RED + " ✖ Unbreaking" )
        );

        sender.sendMessage(ChatColor.GRAY + "===== Experience");
        sender.sendMessage(
                (Objects.equals(plugin.config.getString("exp.levels.mode"), "NONE") ? ChatColor.YELLOW + "VANILLA" : ChatColor.GREEN + plugin.config.getString("exp.levels.mode") )
                        + ChatColor.GRAY + " | "
                        + ChatColor.GRAY + "("+ plugin.config.getDouble("exp.levels.min") + " ~ "+ plugin.config.getDouble("exp.levels.max")  + ")"
                        + ChatColor.GRAY + " | "
                        + (plugin.config.getBoolean("exp.reset_level") ? ChatColor.GREEN + " ✔ Level Reset" :  ChatColor.RED + " ✖ Level Reset" ));

        sender.sendMessage(ChatColor.GRAY + "===== Hunger");
        sender.sendMessage(
                (Objects.equals(plugin.config.getString("hunger.mode"), "NONE") ? ChatColor.YELLOW + "VANILLA" : ChatColor.GREEN + plugin.config.getString("hunger.mode") )
                        + ChatColor.GRAY + " | "
                        + ChatColor.GRAY + " clamped between ("+ plugin.config.getDouble("hunger.min") + " ~ "+ plugin.config.getDouble("hunger.max")  + ")");

        sender.sendMessage(ChatColor.GRAY + "===== Potions");
        sender.sendMessage(
                (plugin.config.getInt("potions.reduce_potency") <= 0 ? ChatColor.GREEN + " ✔ Potency Kept" :  ChatColor.RED + " ✖ Potency reduced by " + plugin.config.getInt("potions.reduce_potency") )
                        + ChatColor.GRAY + " | "
                        + (plugin.config.getInt("potions.duration_penalty") <= 0 ? ChatColor.GREEN + " ✔ Duration Kept" :  ChatColor.RED + " ✖ Duration reduced by " + plugin.config.getInt("potions.duration_penalty") + "sec." )
        );

        List<PotionEffectType> effectlist =  plugin.config.getEffectList("potions.kept_effects");

        StringJoiner listOfEffects = new StringJoiner(", ");
        for (PotionEffectType potionEffectType : effectlist) {
            if(potionEffectType != null){
                listOfEffects.add(potionEffectType.getName());
            }
        }

        sender.sendMessage(ChatColor.AQUA + "Kept Effects: " + ChatColor.GREEN + listOfEffects);

        return true;
    }

    private boolean reload(CommandSender sender, Command command, String label, String[] args){

        if (sender instanceof Player ply) {
            if(!ply.hasPermission("betterkeepinventory.reload")){
                sender.sendMessage(ChatColor.RED + "Missing permission.");
                return true;
            }
        }

        plugin.config.reload();
        sender.sendMessage(ChatColor.AQUA + "Reloaded!");


        return true;
    }

    private boolean sendTestMessage(CommandSender sender, Command command, String label, String[] args){

        if (sender instanceof Player ply) {
            if(!ply.hasPermission("betterkeepinventory.test")){
                sender.sendMessage(ChatColor.RED + "Missing permission.");
                return true;
            }

            if(plugin.config.GetOverrideForMode("ALL", ply)){
                ply.sendMessage(ChatColor.GREEN + "✔" + ChatColor.GRAY + " All modes are overridden.");
            }else{

                Map<String, Boolean> typeMap = new HashMap<String, Boolean>();
                typeMap.put("ALL", plugin.config.GetOverrideForMode("ALL", ply));
                typeMap.put("ITEMS", plugin.config.GetOverrideForMode("ITEMS", ply));
                typeMap.put("ARMOR", plugin.config.GetOverrideForMode("ARMOR", ply));
                typeMap.put("HOTBAR", plugin.config.GetOverrideForMode("HOTBAR", ply));
                typeMap.put("INVENTORY", plugin.config.GetOverrideForMode("INVENTORY", ply));
                typeMap.put("EXP", plugin.config.GetOverrideForMode("EXP", ply));
                typeMap.put("HUNGER", plugin.config.GetOverrideForMode("HUNGER", ply));
                typeMap.put("POTIONS", plugin.config.GetOverrideForMode("POTIONS", ply));
                typeMap.put("ECO", plugin.config.GetOverrideForMode("ECO", ply));

                StringBuilder mainResponse = new StringBuilder();
                for(Map.Entry<String, Boolean> entry : typeMap.entrySet()){
                    mainResponse.append(entry.getValue() ? ChatColor.GREEN + "✔" : ChatColor.RED + "✖").append(entry.getKey()).append(ChatColor.GRAY).append(" | ");
                }

                ply.sendMessage(mainResponse.toString());
            }

            ply.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Location tested only. Gamemode, Gamerule and Permission Bypasses were not checked.");

            return true;

        }

        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        return false;
    }


}
