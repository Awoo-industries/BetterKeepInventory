package com.beepsterr.betterkeepinventory.Library;

import com.beepsterr.betterkeepinventory.BetterKeepInventory;
import org.bstats.charts.SingleLineChart;
import org.bstats.bukkit.Metrics;

import java.util.concurrent.Callable;

public class MetricContainer {

    public int deathsProcessed = 0;
    public int durabilityPointsLost = 0;

    Metrics metrics;
    public MetricContainer(){
        metrics = new Metrics(BetterKeepInventory.getInstance(), 11596);
        
        metrics.addCustomChart(new SingleLineChart("durability_lost", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int amount = deathsProcessed;
                deathsProcessed = 0;
                return amount;
            }
        }));

        metrics.addCustomChart(new SingleLineChart("durability_points_lost", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int amount = durabilityPointsLost;
                durabilityPointsLost = 0;
                return amount;
            }
        }));

    }
}
