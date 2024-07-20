package org.blbilink.blbiLibrary.utils;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ConfigUtil {
    private final Plugin plugin;
    public File configFile;
    public ConfigUtil(Plugin plugin){
        this.plugin = plugin;
    }
    public FileConfiguration loadConfig(String configName) {
        // 加载配置
        plugin.getLogger().info("开始加载配置文件");
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), configName);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        FileConfiguration configNew = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource("config.yml"))));
        if (YmlUtil.checkVersion(configNew.getString("version"),config.getString("version"))) {
            config.set("version", configNew.getString("version"));
            try {
                config.save(configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            plugin.getLogger().warning("检测到新版本配置文件");
            FileUtil.completeFile(plugin,configName, "version");
        }else{
            plugin.getLogger().info("未检测到新版本配置文件");
        }
        return config;
    }
}
