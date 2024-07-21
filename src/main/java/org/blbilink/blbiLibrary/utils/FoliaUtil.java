package org.blbilink.blbiLibrary.utils;

import org.blbilink.blbiLibrary.BlbiLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class FoliaUtil {
    public Boolean isFolia = false;
    private final BlbiLibrary blbiLibrary = BlbiLibrary.blbiLibrary;
    private final Plugin plugin;
    public FoliaUtil(Plugin plugin){
        this.plugin = plugin;
    }
    // 检查是否是Folia服务端核心
    public boolean checkFolia(boolean supportFolia) {
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
    public void runTaskLater(Runnable task, long delay) {
        if (!isFolia) {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        } else {
            try {
                // 直接使用 Folia API
                Bukkit.getAsyncScheduler().runDelayed(plugin, (scheduledTask) -> {
                    Bukkit.getGlobalRegionScheduler().execute(plugin, task);
                }, delay * 50, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to schedule task in Folia: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void runTask(Runnable task) {
        if (!isFolia) {
            Bukkit.getScheduler().runTask(plugin, task);
        } else {
            try {
                // 直接使用 Folia API
                Bukkit.getGlobalRegionScheduler().execute(plugin, task);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to run task in Folia: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void runTaskAsynchronously(Runnable task) {
        if (!isFolia) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        } else {
            try {
                // 直接使用 Folia API
                Bukkit.getAsyncScheduler().runNow(plugin, (scheduledTask) -> task.run());
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to run async task in Folia: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
