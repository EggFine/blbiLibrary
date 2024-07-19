package org.blbilink.blbiLibrary;

import org.blbilink.blbiLibrary.utils.AnsiColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlbiLibrary extends JavaPlugin {

    public static I18n i18n;
    private static Plugin blbiLibrary;

    @Override
    public void onEnable() {
        // 类库加载事件
        blbiLibrary = this;
        getLogger().info("blbiLibrary 支持库已加载");

        // 加载 bStats 统计
        Metrics metrics = new Metrics(this, 22707);
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
    }

    @Override
    public void onDisable() {

    }

    // 检查是否是Folia服务端核心
    public static boolean checkFolia(Plugin plugin,boolean supportFolia) {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            if(supportFolia){
                blbiLibrary.getLogger().info(AnsiColor.AQUA +"[√] 检测到兼容 Folia 核心，并使用与其兼容的插件 ["+plugin.getName()+"] ，正在为您加载" +AnsiColor.RESET);
                plugin.getLogger().info(AnsiColor.AQUA+"[√] 检测到您正在使用 Folia 服务端核心，插件 ["+plugin.getName()+"] 已对其兼容，放心使用"+AnsiColor.RESET);
            }else{
                blbiLibrary.getLogger().warning("[×] 检测到不兼容 Folia 核心的插件 ["+plugin.getName()+"] 请停用该插件！");
                plugin.getLogger().warning("[×] 插件 ["+plugin.getName()+"] 暂时尚未支持 Folia 请停用该插件！");
            }
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
