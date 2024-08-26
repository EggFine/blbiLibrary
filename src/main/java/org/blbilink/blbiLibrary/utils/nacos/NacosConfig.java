//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.blbilink.blbiLibrary.utils.nacos;

import java.io.IOException;
import java.io.StringReader;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class NacosConfig extends YamlConfiguration {
    private final String dataId;
    private final String group;

    public NacosConfig(String dataId, String group, String data) {
        this.dataId = dataId;
        this.group = group;
        this.loadString(data);
    }

    private void loadString(String s) {
        try {
            this.load(new StringReader(s));
        } catch (InvalidConfigurationException | IOException var3) {
            Exception e = var3;
            ((Exception)e).printStackTrace();
        }

    }

    public String getDataId() {
        return this.dataId;
    }

    public String getGroup() {
        return this.group;
    }

    public void reload() {
        Nacos.getNacos().subscribeString(this.dataId, this.group).thenAcceptAsync(this::loadString);
    }
}
