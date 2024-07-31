package org.blbilink.blbiLibrary.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PapiUtil {
    private final Plugin plugin;

    public PapiUtil(Plugin plugin) {
        this.plugin = plugin;
    }

    public String getPlaceholders(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public void registerExpansion(PlaceholderExpansion expansion) {
        expansion.register();
    }

    public PlaceholderExpansion createSimpleExpansion(String identifier, String author, String version, PlaceholderRequestHandler handler) {
        return new SimpleExpansion(identifier, author, version) {
            @Override
            public String onPlaceholderRequest(Player player, String identifier) {
                return handler.onPlaceholderRequest(player, identifier);
            }
        };
    }

    public interface PlaceholderRequestHandler {
        String onPlaceholderRequest(Player player, String identifier);
    }

    public static abstract class SimpleExpansion extends PlaceholderExpansion {
        private final String identifier;
        private final String author;
        private final String version;

        public SimpleExpansion(String identifier, String author, String version) {
            this.identifier = identifier;
            this.author = author;
            this.version = version;
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }

        @Override
        public String getAuthor() {
            return author;
        }

        @Override
        public String getVersion() {
            return version;
        }

        // 这里不再需要声明 onPlaceholderRequest 为抽象方法，因为它已经在 PlaceholderExpansion 中声明为抽象方法了
    }
}