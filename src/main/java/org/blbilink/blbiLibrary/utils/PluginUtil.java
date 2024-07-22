package org.blbilink.blbiLibrary.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginUtil {
    /**
     * 检查插件是否已经加载（多数用于软前置判断）
     * @param pluginName 要检查是否加载的插件名
     * @return 如果返回true则表示已加载，否则反之
     */
    public static boolean isPluginLoaded(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }
}