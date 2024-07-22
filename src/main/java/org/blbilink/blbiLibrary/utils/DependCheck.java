package org.blbilink.blbiLibrary.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class DependCheck {
    public boolean isPluginLoaded(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }
}
