package org.blbilink.blbiLibrary.utils;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ConfigUtil {
    private final Plugin plugin;
    public File configFile;
    public ConfigUtil(Plugin plugin){
        this.plugin = plugin;
    }
    public FileConfiguration loadConfig(String configName) {
        // 加载配置
        String configPrefix = "["+ configName + "]" ;
                plugin.getLogger().info(configPrefix + "开始加载配置文件");
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), configName);
        if(!configFile.exists()){
            plugin.saveResource(configName, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        FileConfiguration configNew = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource("config.yml"))));
        plugin.getLogger().info("The config file version number you are currently using is: " + config.getString("version"));
        plugin.getLogger().info("The latest version of the config file you are using: " + configNew.getString("version"));
        if (YmlUtil.checkVersion(configNew.getString("version"),config.getString("version"))) {
            config.set("version", configNew.getString("version"));
            try {
                config.save(configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            plugin.getLogger().warning(configPrefix + "检测到新版本配置文件");
            FileUtil.completeFile(plugin,configName, "version");
        }else{
            plugin.getLogger().info(configPrefix + AnsiColor.AQUA + "[√] 未检测到新版本配置文件" + AnsiColor.RESET);
        }
        return config;
    }
}
