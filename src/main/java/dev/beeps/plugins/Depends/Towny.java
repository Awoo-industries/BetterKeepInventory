package dev.beeps.plugins.Depends;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Towny {
    
    private final TownyAPI _api;

    private Town town = null;
    private Nation nation = null;

    public Towny(Player ply){

        _api = TownyAPI.getInstance();
        Resident res = _api.getResident(ply);

        if(res != null){
            town = res.getTownOrNull();
            if(town != null){
                nation = town.getNationOrNull();
            }
        }

    }

    public Towny(Location loc){

        _api = TownyAPI.getInstance();
        TownBlock tb = _api.getTownBlock(loc);

        if(tb != null){
            town = tb.getTownOrNull();
            if(town != null){
                nation = town.getNationOrNull();
            }
        }

    }

    public Town getTown() {
        return town;
    }

    public String getTownName(){
        return town == null ? null : town.getName();
    }

    public Nation getNation() {
        return nation;
    }

    public String getNationName(){
        return nation == null ? null : nation.getName();
    }
}
