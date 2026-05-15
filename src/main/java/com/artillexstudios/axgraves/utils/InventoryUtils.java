package com.artillexstudios.axgraves.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class InventoryUtils {

    @NotNull
    public static List<ItemStack> reorderInventory(@NotNull PlayerInventory inventory, @NotNull List<ItemStack> keptItems) {
        final ArrayList<ItemStack> itemsBefore = new ArrayList<>(keptItems);
        final ItemStack[] items = new ItemStack[itemsBefore.size()];
        int n = 0;

        for (String str : CONFIG.getStringList("grave-item-order")) {
            switch (str) {
                case "ARMOR" -> {
                    for (ItemStack it : inventory.getArmorContents()) {
                        if (!itemsBefore.contains(it)) continue;
                        items[n] = it;
                        itemsBefore.remove(it);
                        n++;
                    }
                }
                case "HAND" -> {
                    if (!itemsBefore.contains(inventory.getItemInMainHand())) continue;
                    items[n] = inventory.getItemInMainHand();
                    itemsBefore.remove(inventory.getItemInMainHand());
                    n++;
                }
                case "OFFHAND" -> {
                    if (!itemsBefore.contains(inventory.getItemInOffHand())) continue;
                    items[n] = inventory.getItemInOffHand();
                    itemsBefore.remove(inventory.getItemInOffHand());
                    n++;
                }
            }
        }

        for (ItemStack it : itemsBefore) {
            if (!itemsBefore.contains(it)) continue;
            items[n] = it;
            n++;
        }

        return Arrays.asList(items);
    }

    public static int getRequiredRows(int amount) {
        int rows = amount / 9;
        if (amount % 9 != 0) rows++;
        return Math.max(rows, 1);
    }

    /**
     * Checks if the player's inventory has any available space
     * @param inventory The player's inventory to check
     * @return true if there is at least one empty slot, false otherwise
     */
    public static boolean hasSpace(@NotNull PlayerInventory inventory) {
        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts how many items from the grave can fit in the player's inventory
     * @param inventory The player's inventory
     * @param graveContents The grave's inventory contents
     * @return The number of items that can fit
     */
    public static int countItemsThatCanFit(@NotNull PlayerInventory inventory, @NotNull Inventory graveContents) {
        int count = 0;
        PlayerInventory tempInv = (PlayerInventory) inventory.getHolder().getInventory();

        for (ItemStack item : graveContents.getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            // Check if item can be added (this accounts for stacking)
            int remaining = item.getAmount();
            for (int i = 0; i < 36; i++) {
                ItemStack slot = tempInv.getItem(i);
                if (slot == null || slot.getType() == Material.AIR) {
                    remaining = 0;
                    break;
                } else if (slot.isSimilar(item) && slot.getAmount() < slot.getMaxStackSize()) {
                    remaining -= (slot.getMaxStackSize() - slot.getAmount());
                    if (remaining <= 0) break;
                }
            }

            if (remaining == 0) {
                count++;
            }
        }

        return count;
    }
}
