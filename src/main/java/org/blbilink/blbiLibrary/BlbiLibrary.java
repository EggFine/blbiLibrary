package org.blbilink.blbiLibrary;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlbiLibrary extends JavaPlugin {

    @Override
    public void onEnable() {
        // 类库加载事件
        getLogger().info("blbiLibrary 支持库已加载");
    }

    @Override
    public void onDisable() {

    }

    // 检查是否是Folia服务端核心
    public static boolean checkFolia(Plugin plugin) {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            plugin.getLogger().info("[√] 检测到您正在使用 Folia 服务端核心，我们插件已对其兼容，放心使用");
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
