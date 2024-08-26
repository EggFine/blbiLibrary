//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.blbilink.blbiLibrary.utils.nacos;

import java.util.concurrent.CompletableFuture;

public interface Nacos {
    static Nacos getNacos() {
        return NacosImpl.getInstance();
    }

    static Nacos getNacos(String host, int port) {
        return NacosImpl.getInstance(host, port);
    }

    CompletableFuture<NacosConfig> subscribe(String var1, String var2);

    CompletableFuture<String> subscribeString(String var1, String var2);
}
