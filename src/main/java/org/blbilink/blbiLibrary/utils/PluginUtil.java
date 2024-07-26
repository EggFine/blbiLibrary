package org.blbilink.blbiLibrary.utils;

import org.blbilink.blbiLibrary.BlbiLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PluginUtil {
    private final BlbiLibrary blbiLibrary = BlbiLibrary.blbiLibrary;
    private final Plugin plugin;
    public List<String> plugins = new ArrayList<>();

    public PluginUtil(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 检查插件是否已经加载（多数用于软前置判断）
     *
     * @param pluginName 要检查是否加载的插件名
     * @return 如果返回true则表示已加载，否则反之
     */
    public boolean isPluginLoaded(String pluginName) {
        Plugin plugin_check = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin_check != null && plugin_check.isEnabled()) {
            plugins.add(pluginName);
            blbiLibrary.getLogger().info(AnsiColor.AQUA + "[√] 已检测到 " + pluginName + " 插件， [" + plugin.getName() + "] 插件使用其作为软依赖" + AnsiColor.RESET);
            plugin.getLogger().info(AnsiColor.AQUA + "[√] 已检测到 " + pluginName + " 前置，正在进行 Hook" + AnsiColor.RESET);
            return true;
        }
        return false;
    }
}