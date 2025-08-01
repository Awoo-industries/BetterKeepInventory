package dev.beeps.plugins.Library.Types;

import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static final Material[] RESOURCES = Stream.of(
            MaterialType.RESOURCE_COPPER,
            MaterialType.RESOURCE_IRON,
            MaterialType.RESOURCE_GOLD,
            MaterialType.RESOURCE_DIAMOND,
            MaterialType.RESOURCE_NETHERITE
    ).flatMap(Arrays::stream).toArray(Material[]::new);

    private List<Material> materials = new ArrayList<>();

    public MaterialType(List<String> materialConfig) {

        for(String materialName : materialConfig) {
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                if (material.isItem()) {
                    materials.add(material);
                }
            } catch (IllegalArgumentException e) {

                // yeah.
                if(materialName.equals("*")){
                    materials.addAll(Arrays.asList(Material.values()));
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
                        default:
                            // Invalid group name, ignore
                            BetterKeepInventory.getInstance().getLogger().warning("Tried to parse invalid material: " + materialName);
                            break;
                    }
                } else {
                    // Invalid material name, ignore
                }
            }
        }

    }

    public List<Material> getMaterials() {
        return materials;
    }
}
