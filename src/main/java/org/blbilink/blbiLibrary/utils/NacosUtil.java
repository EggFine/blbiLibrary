package org.blbilink.blbiLibrary.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class NacosUtil extends YamlConfiguration {

    public static NacosUtil loadConfig(String fileUrl) {
        NacosUtil config = new NacosUtil();
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                config.loadFromString(content.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    // 可以根据需要添加其他方法
}