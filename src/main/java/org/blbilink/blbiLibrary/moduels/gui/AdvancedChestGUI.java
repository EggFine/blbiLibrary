package org.blbilink.blbiLibrary.moduels.gui;

import org.blbilink.blbiLibrary.moduels.gui.ChestGUI;
import org.blbilink.blbiLibrary.moduels.gui.ChestUIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AdvancedChestGUI extends ChestGUI implements Listener {
    private final JavaPlugin plugin;
    private final Map<Integer, Map<ClickType, BiConsumer<Player, ClickType>>> advancedClickHandlers;
    private final Map<Player, Boolean> searchingPlayers;
    private BiConsumer<Player, ClickType> prevPageHandler;
    private BiConsumer<Player, ClickType> nextPageHandler;
    private BiConsumer<Player, ClickType> searchHandler;
    private Predicate<String> searchPredicate;
    private int currentPage = 0;
    private int itemsPerPage;

    public AdvancedChestGUI(JavaPlugin plugin, String title, int rows) {
        super(title, rows);
        this.plugin = plugin;
        this.advancedClickHandlers = new HashMap<>();
        this.searchingPlayers = new HashMap<>();
        this.itemsPerPage = (rows - 1) * 9; // Reserve bottom row for navigation
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void setItem(int slot, ItemStack item, BiConsumer<Player, ClickType> handler) {
        super.setItem(slot, item, player -> {}); // Set a dummy handler for compatibility
        setAdvancedClickHandler(slot, handler);
    }

    public void setAdvancedClickHandler(int slot, BiConsumer<Player, ClickType> handler) {
        advancedClickHandlers.computeIfAbsent(slot, k -> new EnumMap<>(ClickType.class));
        for (ClickType clickType : ClickType.values()) {
            advancedClickHandlers.get(slot).put(clickType, handler);
        }
    }

    public void setClickHandler(int slot, ClickType clickType, BiConsumer<Player, ClickType> handler) {
        advancedClickHandlers.computeIfAbsent(slot, k -> new EnumMap<>(ClickType.class)).put(clickType, handler);
    }

    public void setPrevPageHandler(BiConsumer<Player, ClickType> handler) {
        this.prevPageHandler = handler;
    }

    public void setNextPageHandler(BiConsumer<Player, ClickType> handler) {
        this.nextPageHandler = handler;
    }

    public void setSearchHandler(BiConsumer<Player, ClickType> handler) {
        this.searchHandler = handler;
    }

    public void setSearchPredicate(Predicate<String> predicate) {
        this.searchPredicate = predicate;
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, title);
        updateInventory(inventory);
        player.openInventory(inventory);
        ChestUIManager.register(player, this);
    }

    private void updateInventory(Inventory inventory) {
        inventory.clear();
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, items.size());

        for (int i = startIndex; i < endIndex; i++) {
            ClickableItem item = items.get(i);
            if (item != null) {
                inventory.setItem(i - startIndex, item.getItem());
            }
        }

        // Add navigation items
        int lastRow = size - 9;
        inventory.setItem(lastRow, createNavigationItem(Material.ARROW, "Previous Page"));
        inventory.setItem(lastRow + 4, createNavigationItem(Material.COMPASS, "Search"));
        inventory.setItem(lastRow + 8, createNavigationItem(Material.ARROW, "Next Page"));
    }

    private ItemStack createNavigationItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(title)) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        ClickType clickType = event.getClick();

        if (slot < itemsPerPage) {
            int index = currentPage * itemsPerPage + slot;
            Map<ClickType, BiConsumer<Player, ClickType>> handlers = advancedClickHandlers.get(index);
            if (handlers != null) {
                BiConsumer<Player, ClickType> handler = handlers.get(clickType);
                if (handler != null) {
                    handler.accept(player, clickType);
                }
            }
        } else if (slot == size - 9) {
            if (prevPageHandler != null) prevPageHandler.accept(player, clickType);
            changePage(player, -1);
        } else if (slot == size - 5) {
            if (searchHandler != null) searchHandler.accept(player, clickType);
            startSearch(player);
        } else if (slot == size - 1) {
            if (nextPageHandler != null) nextPageHandler.accept(player, clickType);
            changePage(player, 1);
        }
    }

    private void changePage(Player player, int direction) {
        if (direction < 0 && currentPage > 0) {
            currentPage--;
        } else if (direction > 0 && (currentPage + 1) * itemsPerPage < items.size()) {
            currentPage++;
        }
        updateInventory(player.getOpenInventory().getTopInventory());
    }

    private void startSearch(Player player) {
        player.closeInventory();
        player.sendTitle(ChatColor.YELLOW + "Search", ChatColor.GRAY + "Type your search query or 'cancel'", 10, 200, 20);
        searchingPlayers.put(player, true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (searchingPlayers.getOrDefault(player, false)) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> handleSearchChat(player, event.getMessage()));
        }
    }

    private void handleSearchChat(Player player, String query) {
        searchingPlayers.remove(player);

        if (query.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.YELLOW + "Search cancelled.");
            clearPlayerTitle(player);
            return;
        }

        if (searchPredicate != null) {
            List<Map.Entry<Integer, ClickableItem>> searchResults = items.entrySet().stream()
                    .filter(entry -> searchPredicate.test(entry.getValue().getItem().getItemMeta().getDisplayName()))
                    .collect(Collectors.toList());

            if (searchResults.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No items found matching your search.");
                clearPlayerTitle(player);
            } else {
                items.clear();
                advancedClickHandlers.clear();
                for (Map.Entry<Integer, ClickableItem> entry : searchResults) {
                    items.put(items.size(), entry.getValue());
                    if (advancedClickHandlers.containsKey(entry.getKey())) {
                        advancedClickHandlers.put(items.size() - 1, advancedClickHandlers.get(entry.getKey()));
                    }
                }
                currentPage = 0;
                clearPlayerTitle(player);
                open(player);
            }
        }
    }

    private void clearPlayerTitle(Player player) {
        player.resetTitle();
    }
}