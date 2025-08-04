package com.beepsterr.betterkeepinventory.Library.Types;

import com.beepsterr.betterkeepinventory.BetterKeepInventory;

import java.util.ArrayList;
import java.util.List;

public class SlotType {

    public static int[] armorSlots = new int[]{ 36,37,38,39 };
    public static int[] hotbarSlots = new int[]{ 0,1,2,3,4,5,6,7,8 };
    public static int[] offhandSlots = new int[]{ 40 };
    public static int[] inventorySlots = new int[]{
            9,10,11,12,13,14,15,16,17,
            18,19,20,21,22,23,24,25,26,
            27,28,29,30,31,32,33,34,35 // stupid.
    };

    private List<Integer> slotIds = new ArrayList<>();

    public SlotType(List<String> slots) {

        // loop over all slots
        for (String slot : slots) {

            // try parse slot string as integer
            try{
                slot = slot.trim();
                int slotId = Integer.parseInt(slot);
                if (slotId >= 0 && slotId < 41) { // Valid slot range
                    slotIds.add(slotId);
                }
            }catch (Exception e) {
                // we failed to parse the integer, lets test if we're dealing
                // with a preset slot list
                switch (slot.toUpperCase()) {
                    case "ARMOR":
                        for (int armorSlot : armorSlots) {
                            slotIds.add(armorSlot);
                        }
                        break;
                    case "HOTBAR":
                        for (int hotbarSlot : hotbarSlots) {
                            slotIds.add(hotbarSlot);
                        }
                        break;
                    case "INVENTORY":
                        for (int inventorySlot : inventorySlots) {
                            slotIds.add(inventorySlot);
                        }
                        break;
                    case "OFFHAND":
                        for (int offhandSlot : offhandSlots) {
                            slotIds.add(offhandSlot);
                        }
                        break;
                    case "ALL":
                    case "*":
                        for (int i = 0; i < 41; i++) {
                            slotIds.add(i);
                        }
                        break;
                    default:
                        // Invalid slot type, ignore
                        BetterKeepInventory.getInstance().getLogger().warning("Tried to parse invalid slot type: " + slot + ". Valid types are: ARMOR, HOTBAR, INVENTORY, OFFHAND, ALL or a number between 0 and 40.");
                }
            }
        }

    }

    public List<Integer> getSlotIds() {
        return slotIds;
    }
}

