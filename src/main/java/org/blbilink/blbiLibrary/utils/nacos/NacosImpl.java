//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.blbilink.blbiLibrary.utils.nacos;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class NacosImpl implements Nacos {
    private static final NacosImpl INSTANCE = new NacosImpl();
    protected String host;
    protected int port;
    Properties properties = new Properties();

    protected NacosImpl() {
        this.host = "localhost";
        this.port = 8848;
    }

    protected NacosImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Contract(
            value = "_, _ -> new",
            pure = true
    )
    public static @NotNull Nacos getInstance(String host, int port) {
        return new NacosImpl(host, port);
    }

    public static Nacos getInstance() {
        return INSTANCE;
    }

    protected URL getUrl(String dataId, String group) {
        try {
            return new URL(String.format("http://%s:%s/nacos/v1/cs/configs?dataId=%s&group=%s", this.host, this.port, dataId, group));
        } catch (MalformedURLException var4) {
            MalformedURLException e = var4;
            e.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<NacosConfig> subscribe(String dataId, String group) {
        CompletableFuture<NacosConfig> future = new CompletableFuture();
        this.subscribeString(dataId, group).thenApply((data) -> {
            future.complete(new NacosConfig(dataId, group, data));
            return null;
        });
        return future;
    }

    public CompletableFuture<String> subscribeString(String dataId, String group) {
        CompletableFuture<String> completableFuture = new CompletableFuture();
        (new Thread(() -> {
            try {
                completableFuture.complete(HttpUtil.getHttpRequestData(this.getUrl(dataId, group)));
            } catch (Exception var5) {
                Exception e = var5;
                completableFuture.completeExceptionally(e);
            }

        })).start();
        return completableFuture;
    }
}
