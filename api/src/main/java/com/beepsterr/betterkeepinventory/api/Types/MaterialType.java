package com.beepsterr.betterkeepinventory.api.Types;

import com.beepsterr.betterkeepinventory.api.Exceptions.TypeError;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaterialType {

    public static Material[] RESOURCE_NETHERITE = new Material[]{
            Material.NETHERITE_SCRAP,
            Material.NETHERITE_INGOT,
            Material.NETHERITE_BLOCK,
            Material.ANCIENT_DEBRIS,
    };

    public static Material[] RESOURCE_DIAMOND = new Material[]{
            Material.DIAMOND,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DIAMOND_BLOCK,
    };

    public static Material[] RESOURCE_GOLD = new Material[]{
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.RAW_GOLD,
            Material.RAW_GOLD_BLOCK,
            Material.GOLD_NUGGET,
            Material.GOLD_INGOT,
            Material.GOLD_BLOCK,
    };

    public static Material[] RESOURCE_IRON = new Material[]{
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.RAW_IRON,
            Material.RAW_IRON_BLOCK,
            Material.IRON_NUGGET,
            Material.IRON_INGOT,
            Material.IRON_BLOCK,
    };

    public static Material[] RESOURCE_COPPER = new Material[]{
            Material.COPPER_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.RAW_COPPER,
            Material.RAW_COPPER_BLOCK,
//            Material.COPPER_NUGGET,
            Material.COPPER_INGOT,
            Material.COPPER_BLOCK,
            Material.WAXED_COPPER_BLOCK,
    };

    public static Material[] RESOURCE_MISC = new Material[]{
            Material.EMERALD,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.EMERALD_BLOCK,
            Material.LAPIS_LAZULI,
            Material.LAPIS_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.LAPIS_BLOCK,
            Material.COAL,
            Material.COAL_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.COAL_BLOCK,
            Material.REDSTONE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.REDSTONE_BLOCK,
            Material.QUARTZ,
            Material.NETHER_QUARTZ_ORE,
    };

    public static final Material[] RESOURCES = Stream.of(
            MaterialType.RESOURCE_COPPER,
            MaterialType.RESOURCE_IRON,
            MaterialType.RESOURCE_GOLD,
            MaterialType.RESOURCE_DIAMOND,
            MaterialType.RESOURCE_NETHERITE,
            MaterialType.RESOURCE_MISC
    ).flatMap(Arrays::stream).toArray(Material[]::new);

    public static Material[] ARMOR = new Material[]{
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS,
            Material.NETHERITE_BOOTS,
    };

    public static Material[] WEAPONS = new Material[]{
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD,
            Material.BOW,
            Material.CROSSBOW,
            Material.TRIDENT,
    };

    public static Material[] SWORDS = new Material[]{
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD,
    };

    public static Material[] AXES = new Material[]{
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE,
    };

    public static Material[] PICKAXES = new Material[]{
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE,
    };

    public static Material[] SHOVELS = new Material[]{
            Material.WOODEN_SHOVEL,
            Material.STONE_SHOVEL,
            Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.NETHERITE_SHOVEL,
    };

    public static Material[] HOES = new Material[]{
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_HOE,
    };

    public static Material[] POTIONS = new Material[]{
            Material.POTION,
            Material.SPLASH_POTION,
            Material.LINGERING_POTION,
    };

    public static Material[] MISC_TOOLS = new Material[]{
            Material.FISHING_ROD,
            Material.SHEARS,
            Material.FLINT_AND_STEEL,
    };

    public static Material[] TOOLS = Stream.of(
            MaterialType.AXES,
            MaterialType.PICKAXES,
            MaterialType.SHOVELS,
            MaterialType.HOES,
            MaterialType.MISC_TOOLS
    ).flatMap(Arrays::stream).toArray(Material[]::new);

    public static Material[] EQUIPMENT = Stream.of(
            MaterialType.ARMOR,
            MaterialType.WEAPONS,
            MaterialType.TOOLS
    ).flatMap(Arrays::stream).toArray(Material[]::new);

    private List<Material> materials = new ArrayList<>();
    private boolean includeAll = false;

    public MaterialType(List<String> materialConfig) {

        for(String materialName : materialConfig) {
            materialName = materialName.trim().toUpperCase();
            try {
                Material material = Material.valueOf(materialName);
                if (material.isItem()) {
                    materials.add(material);
                }
            } catch (IllegalArgumentException e) {

                // yeah.
                if(materialName.equals("*") || materialName.equals("ALL")) {
                    includeAll = true;
                    continue;
                }

                // String might be a material group (prefixed with G:)
                // remove the prefix and try
                if (materialName.startsWith("G:")) {
                    String groupName = materialName.substring(2).toUpperCase();
                    switch (groupName) {
                        case "RESOURCES":
                            materials.addAll(List.of(RESOURCES));
                            break;
                        case "NETHERITE":
                            materials.addAll(List.of(RESOURCE_NETHERITE));
                            break;
                        case "DIAMOND":
                            materials.addAll(List.of(RESOURCE_DIAMOND));
                            break;
                        case "GOLD":
                            materials.addAll(List.of(RESOURCE_GOLD));
                            break;
                        case "IRON":
                            materials.addAll(List.of(RESOURCE_IRON));
                            break;
                        case "COPPER":
                            materials.addAll(List.of(RESOURCE_COPPER));
                            break;
                        case "TOOLS":
                            materials.addAll(List.of(TOOLS));
                            break;
                        case "WEAPONS":
                            materials.addAll(List.of(WEAPONS));
                            break;
                        case "SWORDS":
                            materials.addAll(List.of(SWORDS));
                            break;
                        case "AXES":
                            materials.addAll(List.of(AXES));
                            break;
                        case "PICKAXES":
                            materials.addAll(List.of(PICKAXES));
                            break;
                        case "SHOVELS":
                            materials.addAll(List.of(SHOVELS));
                            break;
                        case "HOES":
                            materials.addAll(List.of(HOES));
                            break;
                        case "ARMOR":
                            materials.addAll(List.of(ARMOR));
                            break;
                        case "EQUIPMENT":
                            materials.addAll(List.of(EQUIPMENT));
                            break;
                        case "POTIONS":
                            materials.addAll(List.of(POTIONS));
                            break;
                        default:
                            // Invalid group name, ignore
                            throw new TypeError("Tried to parse invalid material: " + materialName);
                    }
                } else {
                    // Invalid material name, ignore
                }
            }
        }

    }

    public boolean isIncludeAll() {
        return includeAll;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    // TODO: When dropping 1.19, migrate to use Material#GetTranslationKey()
    public static String GetName(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        String displayName;

        if (meta != null && meta.hasDisplayName()) {
            displayName = meta.getDisplayName(); // Use the custom name if present
        } else {
            // Fallback to a readable name from the enum
            displayName = Arrays.stream(item.getType().toString().split("_"))
                    .map(part -> part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));
        }
        return displayName;
    }
}
