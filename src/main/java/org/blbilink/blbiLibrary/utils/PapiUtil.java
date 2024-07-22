package org.blbilink.blbiLibrary.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PapiUtil {

    /**
     * 解析包含 PAPI 变量的字符串
     * @param player 玩家对象
     * @param text 包含 PAPI 变量的文本
     * @return 解析后的文本
     */
    public static String setPlaceholders(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    /**
     * 注册自定义的 PAPI 扩展
     * @param expansion 自定义的 PlaceholderExpansion 实现
     */
    public static void registerExpansion(PlaceholderExpansion expansion) {
        expansion.register();
    }

    /**
     * 创建一个简单的 PAPI 扩展
     */
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

        @Override
        public abstract String onPlaceholderRequest(Player player, String identifier);
    }
}