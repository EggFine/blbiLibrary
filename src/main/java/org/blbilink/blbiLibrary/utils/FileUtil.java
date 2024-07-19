package org.blbilink.blbiLibrary.utils;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

public final class FileUtil {


    /**
     * Complete configuration(key and value, comments, etc)
     * @param resourceFile the resource file you want to complete
     */
    public static void completeFile(Plugin plugin,String resourceFile, String... notNeedSyncKeys) {
        plugin.getLogger().info("开始更新配置文件");
        if (plugin == null) {
            return;
        }

        InputStream stream = plugin.getResource(resourceFile);
        File file = new File(plugin.getDataFolder(), resourceFile);
        if (!file.exists()) {
            if (stream != null) {
                plugin.saveResource(resourceFile, false);
                return;
            }
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
            return;
        }

        if (stream == null) {
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
            return;
        }

        try {
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(reader);
            YamlConfiguration configuration2 = YamlConfiguration.loadConfiguration(file);

            List<String> notSyncKeys = Arrays.asList(notNeedSyncKeys);

            for (String key : configuration.getKeys(true)) {
                boolean needSync = IterableUtil.getIf(notSyncKeys, k -> k != null && (key.equals(k) || key.startsWith(k))).isEmpty();
                if (!needSync) {
                    continue;
                }

                Object value = configuration.get(key);
                if (value instanceof List<?>) {
                    List<?> list2 = configuration2.getList(key);
                    if (list2 == null) {
                        configuration2.set(key, value);
                        continue;
                    }
                }

                if (!configuration2.contains(key)) {
                    configuration2.set(key, value);
                }

                if (!configuration.getComments(key).equals(configuration2.getComments(key))) {
                    configuration2.setComments(key, configuration.getComments(key));
                }

                YamlConfigurationOptions options1 = configuration.options();
                YamlConfigurationOptions options2 = configuration2.options();

                if (!options2.getHeader().equals(options1.getHeader())) {
                    options2.setHeader(options1.getHeader());
                }
            }

            configuration2.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
        }
    }

    /**
     * Complete language file (keys and values, comments, etc.)
     * FORCE SYNC
     * @param plugin plugin instance
     * @param resourceFile the language file you want to complete
     */
    public static void completeLangFile(Plugin plugin,Boolean syncChinese, String resourceFile){
        File file = new File(plugin.getDataFolder() , resourceFile);
        InputStream stream;
        if (syncChinese){
            stream = plugin.getResource("languages/zh_CN.yaml");
        }else{
            stream = plugin.getResource(resourceFile);
        }

        if (!file.exists()) {
            if (stream != null) {
                plugin.saveResource(resourceFile, false);
                return;
            }
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
            return;
        }

        if (stream == null) {
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
            return;
        }

        try {
            Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(reader);
            YamlConfiguration configuration2 = YamlConfiguration.loadConfiguration(file);

            Set<String> keys = configuration.getKeys(true);
            for (String key : keys) {
                Object value = configuration.get(key);
                if (value instanceof List<?> list) {
                    List<?> list2 = configuration2.getList(key);
                    if (list2 == null || !(list.size() == list2.size())) {
                        configuration2.set(key, value);
                        continue;
                    }
                }
                if (!configuration2.contains(key)) {
                    configuration2.set(key, value);
                }
                if (!configuration.getComments(key).equals(configuration2.getComments(key))) {
                    configuration2.setComments(key, configuration.getComments(key));
                }
            }
            for (String key : configuration2.getKeys(true)) {
                if (configuration2.contains(key) & !configuration.contains(key)) {
                    configuration2.set(key, null);
                }
            }
            configuration2.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
        }
    }

    /**
     * Delete a directory
     * @param dirFile the directory
     * @return result
     */
    @CanIgnoreReturnValue
    public static boolean deleteDir(File dirFile){
        Callable<Boolean> callable = () -> {
            if (!dirFile.exists() || !dirFile.isDirectory() || dirFile.listFiles() == null) {
                return false;
            }
            boolean flag = true;

            File[] files = dirFile.listFiles();

            for (File file : Objects.requireNonNull(files)) {
                if (file.isFile()) {
                    flag = deleteFile(file);
                } else {
                    flag = deleteDir(file);
                }
                if (!flag) {
                    break;
                }
            }

            return flag && dirFile.delete();
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(callable);
        executorService.shutdown();

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete a file
     * @param file the file
     * @return result
     */
    @CanIgnoreReturnValue
    public static boolean deleteFile(File file){
        boolean flag = false;

        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }

        return flag;
    }
}