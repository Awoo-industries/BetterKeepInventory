package dev.beeps.plugins.Library;

import dev.beeps.plugins.BetterKeepInventory;
import dev.beeps.plugins.Depends.PlaceholderAPI;
import dev.beeps.plugins.Exceptions.ConfigurationException;
import dev.beeps.plugins.Library.Types.PlaceholderConditionType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PlaceholderCondition {

    private String placeholder;
    private final PlaceholderConditionType operator;
    private final String value;

    public PlaceholderCondition(ConfigurationSection config) throws ConfigurationException {

        if(config.getString("placeholder") == null){
            throw new ConfigurationException(config.getCurrentPath() + ".placeholder", "Placeholder is required");
        }

        if(config.getString("operator") == null){
            throw new ConfigurationException(config.getCurrentPath() + ".operator", "Placeholder operator is required");
        }

        try {

            String value = Objects.requireNonNull(config.getString("operator")).toUpperCase();
            switch(value){
                case "==":
                case "=":
                    operator = PlaceholderConditionType.EQUALS;
                    break;
                case "!=":
                case "<>":
                case "NOT":
                    operator = PlaceholderConditionType.NOT_EQUALS;
                    break;
                case "INCLUDES":
                    operator = PlaceholderConditionType.CONTAINS;
                    break;
                case "*.":
                case "BEINGS_WITH":
                    operator = PlaceholderConditionType.STARTS_WITH;
                    break;
                case ".*":
                case "STOPS_WITH":
                    operator = PlaceholderConditionType.ENDS_WITH;
                    break;
                case ">":
                    operator = PlaceholderConditionType.GREATER_THAN;
                    break;
                case ">=":
                    operator = PlaceholderConditionType.GREATER_THAN_OR_EQUALS;
                    break;
                case "<":
                    operator = PlaceholderConditionType.LESS_THAN;
                    break;
                case "<=":
                    operator = PlaceholderConditionType.LESS_THAN_OR_EQUALS;
                    break;
                default:
                    operator = PlaceholderConditionType.valueOf(value);
            }

        } catch (IllegalArgumentException e) {
            throw new ConfigurationException(config.getCurrentPath() + ".operator", "Placeholder operator must be one of " + Objects.toString(PlaceholderConditionType.values()));
        }

        if(config.getString("value") == null){
            throw new ConfigurationException(config.getCurrentPath() + ".value", "Placeholder Value is required");
        }


        this.placeholder = config.getString("placeholder");
        if(!this.placeholder.startsWith("%") && !this.placeholder.endsWith("%")) {
            this.placeholder = "%" + this.placeholder + "%";
        }

        this.value = config.getString("value");

    }

    public boolean test(Player player) {

        // Double check that PlaceholderAPI is still loaded (it could have been unloaded)
        if(!BetterKeepInventory.getInstance().checkDependency("PlaceholderAPI")) return false;

        String resolvedPlaceholder = PlaceholderAPI.setPlaceholders(player, this.placeholder);

        BetterKeepInventory.getInstance().getLogger().info("Resolved Placeholder: " + resolvedPlaceholder);
        BetterKeepInventory.getInstance().getLogger().info("Requested Value: " + this.value);
        BetterKeepInventory.getInstance().getLogger().info("Requested operator: " + this.operator);

        switch(operator) {
            case EQUALS:
                return resolvedPlaceholder.equals(value);
            case NOT_EQUALS:
                return !resolvedPlaceholder.equals(value);
            case CONTAINS:
                return resolvedPlaceholder.contains(value);
            case STARTS_WITH:
                return resolvedPlaceholder.startsWith(value);
            case ENDS_WITH:
                return resolvedPlaceholder.endsWith(value);
            case GREATER_THAN:
                return Float.parseFloat(resolvedPlaceholder) > Float.parseFloat(value);
            case LESS_THAN:
                return Float.parseFloat(resolvedPlaceholder) < Float.parseFloat(value);
            case GREATER_THAN_OR_EQUALS:
                return Float.parseFloat(resolvedPlaceholder) >= Float.parseFloat(value);
            case LESS_THAN_OR_EQUALS:
                return Float.parseFloat(resolvedPlaceholder) <= Float.parseFloat(value);
            default:
                return false;
        }


    }
}
