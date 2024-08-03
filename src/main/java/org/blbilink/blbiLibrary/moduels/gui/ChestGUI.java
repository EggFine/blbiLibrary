package org.blbilink.blbiLibrary.moduels.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ChestGUI {
    protected final String title;
    protected final int size;
    protected final Map<Integer, ClickableItem> items;

    public ChestGUI(String title, int rows) {
        this.title = title;
        this.size = rows * 9;
        this.items = new HashMap<>();
    }

    public void setItem(int slot, ItemStack item, ClickHandler handler) {
        items.put(slot, new ClickableItem(item, handler));
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, title);
        for (Map.Entry<Integer, ClickableItem> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
        player.openInventory(inventory);
        ChestUIManager.register(player, this);
    }

    public void onClick(Player player, int slot) {
        ClickableItem item = items.get(slot);
        if (item != null && item.getHandler() != null) {
            item.getHandler().onClick(player);
        }
    }

    public static class ClickableItem {
        private final ItemStack item;
        private final ClickHandler handler;

        public ClickableItem(ItemStack item, ClickHandler handler) {
            this.item = item;
            this.handler = handler;
        }

        public ItemStack getItem() {
            return item;
        }

        public ClickHandler getHandler() {
            return handler;
        }
    }

    public interface ClickHandler {
        void onClick(Player player);
    }
}

class ChestUIManager {
    private static final Map<Player, ChestGUI> openInventories = new HashMap<>();

    public static void register(Player player, ChestGUI ui) {
        openInventories.put(player, ui);
    }

    public static void unregister(Player player) {
        openInventories.remove(player);
    }

    public static void onClick(Player player, int slot) {
        ChestGUI ui = openInventories.get(player);
        if (ui != null) {
            ui.onClick(player, slot);
        }
    }
}