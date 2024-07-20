package org.blbilink.blbiLibrary;

import org.blbilink.blbiLibrary.utils.AnsiColor;
import org.blbilink.blbiLibrary.utils.ConfigUtil;
import org.blbilink.blbiLibrary.utils.FoliaUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlbiLibrary extends JavaPlugin {

    public static I18n i18n;
    public static ConfigUtil configUtil;
    @Override
    public void onEnable() {
        // 类库加载事件
        loadFunction();
        getLogger().info("blbiLibrary 支持库已加载");

        // 加载 bStats 统计
        Metrics metrics = new Metrics(this, 22707);
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
    }

    @Override
    public void onDisable() {

    }

    private void loadFunction(){
        new FoliaUtil(this);
    }
}
