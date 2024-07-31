package org.blbilink.blbiLibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.blbilink.blbiLibrary.utils.AnsiColor;
import org.blbilink.blbiLibrary.utils.FileUtil;
import org.blbilink.blbiLibrary.utils.YmlUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class I18n {
    private FileConfiguration language;
    private final String prefix;
    private final Plugin plugin;
    private String languageName;

    public I18n(Plugin plugin, String prefix, String languageName) {
        this.prefix = prefix;
        this.plugin = plugin;
        this.languageName = languageName;
    }

    public String as(String key, boolean addPrefix, Object... papi) {
        String str = language.getString(key).replace("&", "§");
        if (addPrefix) str = prefix + str;
        return str.formatted(papi);
    }

    public String as(String key) {
        return as(key, false);
    }

    public List<String> asList(String key, boolean addPrefix, Object... papi) {
        List<String> list = language.getStringList(key);
        List<String> result = new ArrayList<>();
        for (String s : list) {
            String processed = s.replace("&", "§");
            if (addPrefix) {
                processed = prefix + processed;
            }
            String apply = processed;
            result.add(apply.formatted(papi));
        }
        return result;
    }

    /*
    public static void as(Player player) {
        player.sendMessage(BlbiLibrary.i18n.as("egg"));

    }
     */


    public void loadLanguage() {
        String languageFileName = languageName + ".yml";
        File languageFolder = new File(plugin.getDataFolder(), "languages");
        if (!languageFolder.exists()) {
            languageFolder.mkdirs();
        }
        File languageFile = new File(languageFolder, languageFileName);

        // 检查语言文件是否存在，如果不存在则尝试从插件资源库获取
        if (!languageFile.exists()) {
            // 检查插件资源库是否存在用户配置的语言
            InputStream resourceStream = plugin.getResource("languages/" + languageFileName);
            if (resourceStream != null) {
                // 插件内有指定的语言文件，导出该文件
                plugin.saveResource("languages/" + languageFileName, false);
            } else {
                // 插件内没有指定的语言文件，使用默认的 zh_CN.yml
                plugin.getLogger().warning("指定的语言文件 '" + languageFileName + "' 不存在，尝试使用默认的 zh_CN.yml");
                plugin.getLogger().warning("The specified language file '" + languageFileName + "' does not exist, try using the default zh_CN.yml");
                languageName = "zh_CN";
                languageFileName = languageName + ".yml";
                languageFile = new File(languageFolder, languageFileName);
                if (!languageFile.exists()) {
                    // 如果默认语言文件也不存在，从插件资源中提取
                    plugin.saveResource("languages/" + languageFileName, false);
                }
            }
        }
        // 加载语言文件
        language = YamlConfiguration.loadConfiguration(languageFile);
        FileConfiguration languageConfigNew = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource("languages/" + languageFileName))));
        FileConfiguration cnLanguage = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource("languages/zh_CN.yml"))));
        plugin.getLogger().info("The language pack version number you are currently using is: " + language.getString("version"));
        plugin.getLogger().info("The latest version of the language pack you are using: " + languageConfigNew.getString("version"));
        plugin.getLogger().info("The latest version of Simplified Chinese language pack: " + cnLanguage.getString("version"));
        if (YmlUtil.checkVersion(languageConfigNew.getString("version"), language.getString("version"))) {
            language.set("version", languageConfigNew.getString("version"));
            try {
                language.save(languageFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            plugin.getLogger().warning("检测到新版本语言文件");
            FileUtil.completeLangFile(plugin, false, "languages/" + languageFileName);
        } else {
            plugin.getLogger().info(AnsiColor.AQUA + "[√] 未检测到新版本语言文件" + AnsiColor.RESET);
        }
        if (YmlUtil.checkVersion(cnLanguage.getString("version"), language.getString("version"))) {
            plugin.getLogger().warning("It is detected that the Chinese(zh_CN.yml) language file is newer than your language file, " +
                    "so we are about to synchronize the new language configuration items of Chinese(zh_CN.yml) to your language file");
            FileUtil.completeLangFile(plugin, true, "languages/" + languageFileName);
        }
        plugin.getLogger().info(AnsiColor.AQUA + "[√] " + String.format(as("loadedLanguage", false, languageName)) + " | " + as("Language", false) + AnsiColor.RESET);
    }
}
