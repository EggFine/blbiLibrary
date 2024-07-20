package org.blbilink.blbiLibrary.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConfigUtil {
    private final Plugin plugin;
    private String configName;
    public File configFile;
    public FileConfiguration config;
    public String languageName;
    public String prefix;
    private static final Map<String, Object> configMap = new HashMap<>();
    public ConfigUtil(Plugin plugin,String configName){
        this.plugin = plugin;
        this.configName = configName;
    }
    public void loadConfig() {
        // 加载配置
        plugin.getLogger().info("开始加载配置文件");
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), configName);
        config = YamlConfiguration.loadConfiguration(configFile);
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
        languageName  = config.getString("language", "zh_CN") ;
        prefix = config.getString("prefix", null) ;
        loadConfigToMap();
    }

    private void loadConfigToMap() {
        for (String key : config.getKeys(true)) {
            Object value = config.get(key);
            configMap.put(key, value);
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存配置文件: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) configMap.get(key);
    }

    // 获取特定类型配置项的便捷方法
    public static String getString(String key) {
        return (String) configMap.get(key);
    }

    public static boolean getBoolean(String key) {
        return (boolean) configMap.get(key);
    }

    public static int getInt(String key) {
        return (int) configMap.get(key);
    }

    public static double getDouble(String key) {
        return (double) configMap.get(key);
    }

    public static List<String> getStringList(String key) {
        return (List<String>) configMap.get(key);
    }

    // 动态添加或更新配置项
    public static void set(String key, Object value) {
        configMap.put(key, value);
    }
}
