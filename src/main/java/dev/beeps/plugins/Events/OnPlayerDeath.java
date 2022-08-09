package dev.beeps.plugins.Events;

import dev.beeps.plugins.BetterConfig;
import dev.beeps.plugins.BetterKeepInventory;
import dev.beeps.plugins.Depends.Vault;
import dev.beeps.plugins.Events.Types.ItemHandler;
import dev.beeps.plugins.Events.Types.potionHandler;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.logging.Level;

public class OnPlayerDeath  implements Listener {

    BetterKeepInventory plugin;
    BetterConfig config;


    public OnPlayerDeath(BetterKeepInventory main){
        plugin = main;
        config = main.config;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player ply = event.getEntity();
        World world = ply.getWorld();
        plugin.log(Level.FINE, ply, "############## PlayerHasDied ##############");
        plugin.log(Level.FINE, ply, "Grace->getGrace:" + plugin.graceMap.get(ply.getUniqueId()));

        // plugin enabled
        if(!config.getBoolean("main.enabled", false)){
            plugin.log(Level.FINE, ply, "PlayerHasDied->EventIngored:plugin_disabled");
            return;
        }

        // disabled world
        if(config.GetOverrideForMode("ALL", ply)){
            plugin.log(Level.FINE, ply, "PlayerHasDied->EventIngored:plugin_disabled_world");
            return;
        }

        // grace check
        if(plugin.graceMap.containsKey(ply.getUniqueId())){
            plugin.log(Level.FINE, ply, "PlayerHasDied->EventIngored:grace");
            return;
        }

        // bypass permission
        if(ply.hasPermission("betterkeepinventory.bypass") ){
            plugin.log(Level.FINE, ply, "PlayerHasDied->EventIngored:bypass_all_perm");
            return;
        }

        // ignore if no keepInventory
        if(!event.getKeepInventory()){

            if(plugin.checkDependency("essentials") && !ply.hasPermission("essentials.keepinv")){
                plugin.log(Level.FINE, ply, "PlayerHasDied->EssentialsKeepInv:ON");
            }else{
                plugin.log(Level.FINE, ply, "PlayerHasDied->EventIgnored->keep_inventory_disabled_world (Essentials.keepinv) not present!");
                return;
            }

            plugin.log(Level.FINE, ply, "PlayerHasDied->EventIngored:keep_inventory_disabled_world");
            return;
        }

        // Ignore players in creative mode.
        if(ply.getGameMode() == GameMode.CREATIVE ){
            plugin.log(Level.FINE, ply, "PlayerHasDied->EventIngored:player_is_creative");
        }

        // store current hunger so we can reapply on respawn.
        plugin.hungerMap.put(ply.getUniqueId(), ply.getFoodLevel());

        plugin.log(Level.FINE, ply, "###### HandleItems ######");
        handleItems(ply, event);

        plugin.log(Level.FINE, ply, "###### HandleExp ######");
        if(!ply.hasPermission("betterkeepinventory.bypass.exp") ) {
            handleExperience(ply, event);
        }

        plugin.log(Level.FINE, ply, "###### HandleEffects (Death) ######");
        if(!ply.hasPermission("betterkeepinventory.bypass.potions") && !config.GetOverrideForMode("POTIONS", ply) ) {
            new potionHandler(plugin, ply);
        }

        plugin.log(Level.FINE, ply, "###### Economy ######");
        if(!ply.hasPermission("betterkeepinventory.bypass.eco") && !config.GetOverrideForMode("ECO", ply) ) {
            handleEcon(ply, event);
        }

        if(config.getBoolean("main.grace", true)){
            plugin.log(Level.FINE, ply, "Grace->setPlayerGrace:" + config.getInt("main.grace"));
            plugin.graceMap.put(ply.getUniqueId(), config.getInt("main.grace"));
        }else{
            plugin.log(Level.FINE, ply, "Grace->IsDisabled");
        }


    }

    public void handleItems(Player ply, Event evt){

        Inventory inv = ply.getInventory();
        InventoryType invType = InventoryType.PLAYER;

        // disabled world
        if(config.GetOverrideForMode("ITEMS", ply)){
            plugin.log(Level.FINE, ply, "PlayerHasDied->SkippedItems:override_world:ITEMS");
            return;
        }

        for (int size = 0; size<inv.getSize(); size++) {

            ItemStack item = inv.getItem(size);

            // no use in handling empty slots.
            if(item == null) continue;

            ItemMeta meta = item.getItemMeta();
            Material type = item.getType();

            // air can be skipped.
            if(type == Material.AIR) continue;

            // ARMOR
            if (BetterKeepInventory.contains(plugin.armorSlots, size)) {
                if(!ply.hasPermission("betterkeepinventory.bypass.armor") ) {
                    if(!config.GetOverrideForMode("ARMOR", ply)){
                        new ItemHandler(plugin, ply, item, ItemHandler.SlotType.armor, size);
                    }
                }
            }

            // HOTBAR
            else if (BetterKeepInventory.contains(plugin.hotbarSlots, size)) {
                if(!ply.hasPermission("betterkeepinventory.bypass.hotbar") ) {
                    if(!config.GetOverrideForMode("HOTBAR", ply)){
                        new ItemHandler(plugin, ply, item, ItemHandler.SlotType.hotbar, size);
                    }
                }
            }

            // INVENTORY
            else {
                if(!ply.hasPermission("betterkeepinventory.bypass.inventory") ) {
                    if(!config.GetOverrideForMode("INVENTORY", ply)){
                        new ItemHandler(plugin, ply, item, ItemHandler.SlotType.inventory, size);
                    }
                }
            }

        }

    }

    public void handleExperience(Player ply, Event evt){


        if(!config.GetOverrideForMode("EXP", ply)){

            double min = config.getDouble("exp.levels.min", 0);
            double max = config.getDouble("exp.levels.max", 0);

            switch(plugin.config.getExpMode("exp.levels.mode")){
                case NONE:
                    break;
                case ALL:
                    ply.setLevel(0);
                    break;
                case SIMPLE:
                    int result = Math.max(ply.getLevel() - ((int) (min + (max - min) * plugin.randomizer.nextDouble())), 0);
                    ply.setLevel(result);
                    break;
                case PERCENTAGE:
                    double roll = Math.floor(Math.random()*(max-min+1)+min);
                    ply.setLevel( Math.max(ply.getLevel() - ((int)(((double)ply.getLevel()/100) * roll)), 0));
                    break;
            }
        }

        if(plugin.config.getBoolean("exp.reset_level") && !config.GetOverrideForMode("EXP_LEVEL", ply)){
            ply.setExp(0f);
        }

    }

    public void handleEcon(Player ply, Event evt){

        if(plugin.checkDependency("Vault")){

            if(plugin.config.getDouble("eco.amount") > 0){
                Vault v = new Vault(plugin);
                boolean r = v.takeMoney(ply, plugin.config.getDouble("eco.amount"));
                if(r){
                    ply.sendMessage(ChatColor.RED + "You lost $" + plugin.config.getDouble("eco.amount"));
                }
            }

        }else{

            if(plugin.config.getDouble("eco.amount") > 0){
                plugin.log(Level.INFO, ply, "Tried to take money from the player but Vault was not detected, Or no economy plugin is installed!");
            }

        }
    }
}
