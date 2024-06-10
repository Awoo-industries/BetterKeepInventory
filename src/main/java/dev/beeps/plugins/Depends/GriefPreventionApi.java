package dev.beeps.plugins.Depends;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;

public class GriefPreventionApi {

    private final GriefPrevention _api;

    public GriefPreventionApi(Player ply){
        _api = GriefPrevention.instance;
    }

    public Claim GetClaimAtPlayerPos(Player ply){
        return _api.dataStore.getClaimAt(ply.getLocation(), false, null);
    }

    public boolean PlayerHasClaimPermission(Player ply, ClaimPermission permission) {
        Claim claim = GetClaimAtPlayerPos(ply);
        if(claim == null){
            return false;
        }

        return claim.hasExplicitPermission(ply.getUniqueId(), permission);

    }

}
