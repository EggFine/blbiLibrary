package org.blbilink.blbiLibrary;

import com.blbilink.bbf.BlbiBasicFunction;
import com.blbilink.bbf.load.YamlVersionCheck;
import com.blbilink.bbf.vars.BbfVars;
import org.blbilink.blbiLibrary.utils.FileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class I18n {
    public static void loadLanguage() {
        BlbiBasicFunction plugin = BlbiBasicFunction.bbf;
        String languageFileName = BbfVars.languageName + ".yml";
        File languageFolder = new File(plugin.getDataFolder(), "languages");
        if (!languageFolder.exists()) {
            languageFolder.mkdirs();
        }
        BbfVars.languageFile = new File(languageFolder, languageFileName);

        // 检查语言文件是否存在，如果不存在则尝试从插件资源库获取
        if (!BbfVars.languageFile.exists()) {
            // 检查插件资源库是否存在用户配置的语言
            InputStream resourceStream = plugin.getResource("languages/" + languageFileName);
            if (resourceStream != null) {
                // 插件内有指定的语言文件，导出该文件
                plugin.saveResource("languages/" + languageFileName, false);
            } else {
                // 插件内没有指定的语言文件，使用默认的 zh_CN.yaml
                plugin.getLogger().warning("指定的语言文件 '" + languageFileName + "' 不存在，尝试使用默认的 zh_CN.yaml");
                plugin.getLogger().warning("The specified language file '" + languageFileName + "' does not exist, try using the default zh_CN.yaml");
                BbfVars.languageName = "zh_CN";
                languageFileName = BbfVars.languageName + ".yml";
                BbfVars.languageFile = new File(languageFolder, languageFileName);
                if (!BbfVars.languageFile.exists()) {
                    // 如果默认语言文件也不存在，从插件资源中提取
                    plugin.saveResource("languages/" + languageFileName, false);
                }
            }
        }
        // 加载语言文件
        BbfVars.language = YamlConfiguration.loadConfiguration(BbfVars.languageFile);
        FileConfiguration languageConfigNew = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource("languages/" + languageFileName))));
        FileConfiguration cnLanguage = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource("languages/zh_CN.yml"))));
        plugin.getLogger().warning("your language version" + languageConfigNew.getString("version"));
        plugin.getLogger().warning("your language last version" + BbfVars.language.getString("version"));
        plugin.getLogger().warning("Chinese(zh_CN.yml) language last version" + cnLanguage.getString("version"));
        if (YamlVersionCheck.checkYmlVersion(languageConfigNew.getString("version"), BbfVars.language.getString("version"))) {
            BbfVars.language.set("version", languageConfigNew.getString("version"));
            try {
                BbfVars.language.save(BbfVars.languageFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            plugin.getLogger().warning("检测到新版本语言文件");
            FileUtil.completeLangFile(plugin, "languages/" + languageFileName, false);
        } else {
            plugin.getLogger().info("未检测到新版本语言文件");
        }
        if (YamlVersionCheck.checkYmlVersion(cnLanguage.getString("version"), BbfVars.language.getString("version"))) {
            plugin.getLogger().warning("It is detected that the Chinese(zh_CN.yaml) language file is newer than your language file, " +
                    "so we are about to synchronize the new language configuration items of Chinese(zh_CN.yml) to your language file");
            FileUtil.completeLangFile(plugin, "languages/" + languageFileName, true);
        }
        plugin.getLogger().info(String.format(I18n.as("loadedLanguage",false,BbfVars.languageName)) + " | " + I18n.as("Language",false));
    }

    public static String as(String key, boolean addPrefix, Object... papi) {
        String str = BbfVars.language.getString(key).replace("&", "§");
        if (addPrefix) str = BbfVars.prefix + str;
        return str.formatted(papi);
    }
    public static String as(String key) {
        return as(key, false);
    }

    public static List<String> asList(String key,boolean addPrefix, Object... papi) {
        List<String> list = BbfVars.language.getStringList(key);
        List<String> result = new ArrayList<>();
        for (String s : list) {
            String processed = s.replace("&", "§");
            if (addPrefix) {
                processed = BbfVars.prefix + processed;
            }
            String apply = processed;
            result.add(apply.formatted(papi));
        }
        return result;
    }
}
