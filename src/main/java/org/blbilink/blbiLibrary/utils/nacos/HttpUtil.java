//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.blbilink.blbiLibrary.utils.nacos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpUtil {
    public HttpUtil() {
    }

    public static String getHttpRequestData(URL url) {
        StringBuilder returnString = new StringBuilder();

        try {
            HttpURLConnection connect = (HttpURLConnection)url.openConnection();
            connect.setRequestMethod("GET");
            connect.connect();
            InputStream inputStream = connect.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String str;
            while((str = reader.readLine()) != null) {
                returnString.append(str).append("\n");
            }

            inputStream.close();
            connect.disconnect();
        } catch (Exception var6) {
            Exception e = var6;
            e.printStackTrace();
        }

        return returnString.toString();
    }
}
